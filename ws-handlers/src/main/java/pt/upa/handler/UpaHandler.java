package pt.upa.handler;

import pt.upa.ca.ws.CertificateDoesntExists_Exception;
import pt.upa.ca.ws.cli.CAClient;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 * Created by xxlxpto on 07-05-2016.
 */

public abstract class UpaHandler implements SOAPHandler<SOAPMessageContext> {

    protected HandlerConstants handlerConstants;

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        try {

            System.out.println("=================SOAP HANDLER=================");
            if (outbound) {
                System.out.println("Outbound SOAP message.");
                addSenderToSoap(smc.getMessage());
                signMessage(smc);
            } else {
                System.out.println("Inbound SOAP message.");
                String sender = getSenderFromSoap(smc, false);
                if(!checkIfOtherCertificateIsPresent(sender)){
                    System.out.println("Certificate is not present, downloading...");
                    getCertificateFromCA(sender,  sender + handlerConstants.CERTIFICATE_EXTENSION);
                }
                verifySignature(smc);
                getSenderFromSoap(smc, true);

            }
            System.out.println("=============END SOAP HANDLER=================");

        }catch(AuthenticationException | MissedFormedSOAPException e){
            System.out.println(e.getMessage());
            throw e;
        }catch (Exception e) {
            System.out.println("Caught exception in handleMessage: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Continue normal processing...");
        }
        return true;
    }

    private void verifySignature(SOAPMessageContext smc) throws Exception {
        System.out.println("Verifying Signature... ");
        byte[] signature = getSignatureFromSoap(smc);
        smc.getMessage().saveChanges();
        Certificate certificate = readCertificateFile(handlerConstants.RCPT_CERTIFICATE_FILE_PATH);
        if(certificate == null){
            failAuthentication("Could not open the Recipient's certificate.");
        }
        PublicKey publicKey = certificate.getPublicKey();
        boolean isValid = verifyDigitalSignature(signature, getSOAPtoByteArray(smc), publicKey);
        if (isValid) {
            System.out.println("The digital signature is valid");
        } else {
            System.out.println("The digital signature is NOT valid");
            failAuthentication("Recipient's authentication is not valid.");
        }
    }

    private void failAuthentication(String info) throws AuthenticationException {
        throw new AuthenticationException(info);
    }

    private void checkOwnSignature(SOAPMessageContext smc, byte[] signature)
            throws Exception {
        System.out.println("Checking signature...");
        KeyStore keystore = readKeystoreFile(handlerConstants.KEYSTORE_FILE,
                handlerConstants.KEYSTORE_PASSWORD.toCharArray());
        if(keystore == null){
            failAuthentication("KeyStore doesn't exist.");
        }
        Certificate certificate = keystore.getCertificate(handlerConstants.KEY_ALIAS);
        PublicKey publicKey = certificate.getPublicKey();
        boolean isValid = verifyDigitalSignature(signature, getSOAPtoByteArray(smc), publicKey);
        if (isValid) {
            System.out.println("The digital signature is valid");
        } else {
            System.out.println("The digital signature is NOT valid");
            failAuthentication("Own signature is not valid.");
        }
    }

    private void signMessage(SOAPMessageContext smc) throws Exception {
        System.out.println("Signing... ");
        byte[] plainBytes = getSOAPtoByteArray(smc);
        byte[] digitalSignature = makeDigitalSignature(plainBytes,
                getPrivateKeyFromKeystore(handlerConstants.KEYSTORE_FILE,
                        handlerConstants.KEYSTORE_PASSWORD.toCharArray(),
                        handlerConstants.KEY_ALIAS, handlerConstants.KEY_PASSWORD.toCharArray()));

        checkOwnSignature(smc, digitalSignature);

        System.out.println("Add signature to SOAP...");
        addSignatureToSoap(digitalSignature, smc.getMessage());
    }

    private void getCertificateFromCA(String entity, String filename) throws Exception {
        CAClient caClient = new CAClient();
        try{
            caClient.getAndWriteEntityCertificate(entity, filename);
        }catch (IOException e){
            failAuthentication("Error downloading certificate.");
        }
        Certificate certificate = readCertificateFile(filename);
        KeyStore keyStore = readKeystoreFile(handlerConstants.KEYSTORE_FILE,
                handlerConstants.KEYSTORE_PASSWORD.toCharArray());
        if(keyStore == null){
            failAuthentication("KeyStore doesn't exist.");
        }
        Certificate caCertificate =  keyStore.getCertificate("ca");
        PublicKey caPublicKey = caCertificate.getPublicKey();
        System.out.println("Checking Certificate from CA...");
        if (verifySignedCertificate(certificate, caPublicKey)) {
            System.out.println("The signed certificate is valid");
        } else {
            System.err.println("The signed certificate is not valid");
            failAuthentication("Sender's certificate is not valid.");
        }
    }

    private byte[] getSOAPtoByteArray(SOAPMessageContext smc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            smc.getMessage().writeTo(out);
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
        //out.writeTo(System.out);

        return out.toByteArray();
    }

    private void addSignatureToSoap(byte[] signature, SOAPMessage msg) throws SOAPException {
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();

        // add header
        SOAPHeader sh = se.getHeader();
        if (sh == null)
            sh = se.addHeader();

        // add header element (name, namespace prefix, namespace)
        Name name = se.createName(handlerConstants.SIG_ELEMENT_NAME, handlerConstants.PREFIX, handlerConstants.NAMESPACE);
        SOAPHeaderElement element = sh.addHeaderElement(name);
        System.out.println("Adding signature to SOAP...");
        // add header element value
        element.addTextNode(printBase64Binary(signature));
        msg.saveChanges();

    }

    private void addSenderToSoap(SOAPMessage msg) throws SOAPException {
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();

        // add header
        SOAPHeader sh = se.getHeader();
        if (sh == null)
            sh = se.addHeader();

        // add header element (name, namespace prefix, namespace)
        Name name = se.createName(handlerConstants.SENDER_ELEMENT_NAME,
                handlerConstants.PREFIX, handlerConstants.NAMESPACE);
        SOAPHeaderElement element = sh.addHeaderElement(name);
        System.out.println("Adding sender to SOAP...");
        // add header element value
        element.addTextNode(handlerConstants.SENDER_SERVICE_NAME);
        msg.saveChanges();

    }

    private String getSenderFromSoap(SOAPMessageContext smc, boolean toRemove) throws SOAPException {
        // get SOAP envelope header
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPHeader sh = se.getHeader();

        // check header
        checkSOAPHeader(sh);

        // get first header element
        Name name = se.createName(handlerConstants.SENDER_ELEMENT_NAME,
                handlerConstants.PREFIX, handlerConstants.NAMESPACE);
        Iterator it = sh.getChildElements(name);
        // check header element
        checkSOAPHeaderElement(it);

        SOAPElement element = (SOAPElement) it.next();
        String sender = element.getValue();
        if(toRemove) {
            System.out.println("Removing sender from SOAP...");
            it.remove();
            element.removeAttribute(name);
            element.removeContents();
            smc.getMessage().saveChanges();
        }
        return sender;
    }

    private void checkSOAPHeaderElement(Iterator it) {
        if (!it.hasNext()) {
            failMissedFormedSOAP("Header element not found.");
        }
    }

    private void checkSOAPHeader(SOAPHeader sh) {
        if (sh == null) {
            failMissedFormedSOAP("Header not found.");
        }
    }


    private byte[] getSignatureFromSoap(SOAPMessageContext smc) throws SOAPException {
        // get SOAP envelope header
        SOAPMessage msg = smc.getMessage();
        SOAPPart sp = msg.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPHeader sh = se.getHeader();

        // check header
        checkSOAPHeader(sh);

        // get first header element
        Name name = se.createName(handlerConstants.SIG_ELEMENT_NAME,
                handlerConstants.PREFIX, handlerConstants.NAMESPACE);
        Iterator it = sh.getChildElements(name);
        // check header element
        checkSOAPHeaderElement(it);

        SOAPElement element = (SOAPElement) it.next();

        // get header element value
        String valueString = element.getValue();
        byte[] signature = parseBase64Binary(valueString);

        // Removing Signature
        System.out.println("Removing signature from SOAP...");
        it.remove();
        element.removeAttribute(name);
        element.removeContents();

        // put header in a property context
        smc.put(handlerConstants.CONTEXT_PROPERTY, signature);
        // set property scope to application client/server class can access it
        smc.setScope(handlerConstants.CONTEXT_PROPERTY, MessageContext.Scope.APPLICATION);
        smc.getMessage().saveChanges();
        return signature;
    }

    public boolean handleFault(SOAPMessageContext smc) {

        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    private boolean checkIfOtherCertificateIsPresent(String entity){
        return new File(entity + handlerConstants.CERTIFICATE_EXTENSION).exists();
    }

    private void failMissedFormedSOAP(String info){
        throw new MissedFormedSOAPException(info);
    }

    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws Exception
     */
    private Certificate readCertificateFile(String certificateFilePath) throws Exception {
        FileInputStream fis;

        try {
            fis = new FileInputStream(certificateFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("Certificate file <" + certificateFilePath + "> not found.");
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        if (bis.available() > 0) {
            return cf.generateCertificate(bis);
            // It is possible to print the content of the certificate file:
            // System.out.println(cert.toString());
        }
        bis.close();
        fis.close();
        return null;
    }

    /**
     * Verifica se um certificado foi devidamente assinado pela CA
     *
     * @param certificate
     *            certificado a ser verificado
     * @param caPublicKey
     *            certificado da CA
     * @return true se foi devidamente assinado
     */
    private boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
        try {
            certificate.verify(caPublicKey);
        } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
                | SignatureException e) {
            System.err.println("ERRO VERIFICACAO CERTIFICADO:\n" + e.getMessage());
            // O método Certifecate.verify() não retorna qualquer valor (void).
            // Quando um certificado é inválido, isto é, não foi devidamente
            // assinado pela CA
            // é lançada uma excepção: java.security.SignatureException:
            // Signature does not match.
            // também são lançadas excepções caso o certificado esteja num
            // formato incorrecto ou tenha uma
            // chave inválida.

            return false;
        }
        return true;
    }

    /**
     * Reads a PrivateKey from a key-store
     *
     * @return The PrivateKey
     * @throws Exception
     */
    private PrivateKey getPrivateKeyFromKeystore(String keyStoreFilePath, char[] keyStorePassword,
                                                       String keyAlias, char[] keyPassword) throws Exception {

        KeyStore keystore = readKeystoreFile(keyStoreFilePath, keyStorePassword);

        return (PrivateKey) keystore.getKey(keyAlias, keyPassword);
    }

    /**
     * Reads a KeyStore from a file
     *
     * @return The read KeyStore
     * @throws Exception
     */
    private KeyStore readKeystoreFile(String keyStoreFilePath, char[] keyStorePassword) throws Exception {
        FileInputStream fis;
        try {
            fis = new FileInputStream(keyStoreFilePath);
        } catch (FileNotFoundException e) {
            System.err.println("Keystore file <" + keyStoreFilePath + "> not found.");
            return null;
        }
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(fis, keyStorePassword);
        return keystore;
    }

    /** auxiliary method to calculate digest from text and cipher it */
    private byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws Exception {

        // get a signature object using the SHA-1 and RSA combo
        // and sign the plain-text with the private key
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initSign(privateKey);
        sig.update(bytes);

        return sig.sign();
    }

    /**
     * auxiliary method to calculate new digest from text and compare it to the
     * to deciphered digest
     */
    private boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)
            throws Exception {

        // verify the signature with the public key
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(publicKey);
        sig.update(bytes);
        try {
            return sig.verify(cipherDigest);
        } catch (SignatureException se) {
            System.err.println("Caught exception while verifying signature " + se);
            return false;
        }
    }
}
