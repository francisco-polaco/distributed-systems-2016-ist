package pt.upa.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.*;


/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class TransporterHandler implements SOAPHandler<SOAPMessageContext> {
    private static KeyPair key;

    static {
        try {
            key = generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        System.out.println("++++++++++++++++++++++++++++++++");

        if (outbound) {
            System.out.println("Outbound SOAP message:");
            try {
                signSoap(getSOAPtoByteArray(smc));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Inbound SOAP message:");
            try {
                //verifySoap(getSOAPtoByteArray(smc), );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        System.out.println("++++++++++++++++++++++++++++++++");
        return true;
    }

    private byte[] getSOAPtoByteArray(SOAPMessageContext smc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            smc.getMessage().writeTo(out);
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private void verifySoap(byte[] cipherDigest, byte[] soap) throws Exception {
        //KeyPair key = generate();

        // verify the signature
        System.out.println("Verifying ...");
        boolean result = verifyDigitalSignature(cipherDigest, soap, key);
        System.out.println("Signature is " + (result ? "right" : "wrong"));

    }
    private byte[] signSoap(byte[] soap) throws Exception {
        //KeyPair key = generate();

        // make digital signature
        System.out.println("Signing ...");
        byte[] cipherDigest = makeDigitalSignature(soap, key);

        // verify the signature
        System.out.println("Verifying ...");
        boolean result = verifyDigitalSignature(cipherDigest, soap, key);
        System.out.println("Signature is " + (result ? "right" : "wrong"));
        return cipherDigest;

    }

    private void xmlParse(byte[] xml) throws Exception {
        // parse XML document
        //
        InputStream xmlInputStream = new ByteArrayInputStream(xml);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        System.out.println("Parsing XML document from string bytes...");
        Document xmlDocument = documentBuilder.parse(xmlInputStream);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        System.out.println("XML document contents:");
        transformer.transform(new DOMSource(xmlDocument), new StreamResult(System.out));
        System.out.println();

        // retrieve body text
        //
        System.out.print("Body text: ");
        Node bodyNode = null;
        for (Node node = xmlDocument.getDocumentElement().getFirstChild(); node != null; node = node.getNextSibling()) {

            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("body")) {
                bodyNode = node;
                break;
            }
        }
        if (bodyNode == null) {
            throw new Exception("Body node not found!");
        }

        String plainText = bodyNode.getTextContent();
        System.out.println(plainText);
        byte[] plainBytes = plainText.getBytes();

        // remove body node
        //xmlDocument.getDocumentElement().removeChild(bodyNode);

    }


    public boolean handleFault(SOAPMessageContext smc) {

        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }




    /*Digital Signature */

    /** auxiliary method to generate KeyPair */
    private static KeyPair generate() throws Exception {
        // generate an RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

        return key;
    }

    /** auxiliary method to calculate digest from text and cipher it */
    private byte[] makeDigitalSignature(byte[] bytes, KeyPair keyPair) throws Exception {

        // get a message digest object using the specified algorithm
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        // calculate the digest and print it out
        messageDigest.update(bytes);
        byte[] digest = messageDigest.digest();
        System.out.println("Digest:");
        System.out.println(printHexBinary(digest));

        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the private key
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        byte[] cipherDigest = cipher.doFinal(digest);

        System.out.println("Cipher digest:");
        System.out.println(printHexBinary(cipherDigest));

        return cipherDigest;
    }

    /**
     * auxiliary method to calculate new digest from text and compare it to the
     * to deciphered digest
     */
    private boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, KeyPair keyPair) throws Exception {

        // get a message digest object using the SHA-1 algorithm
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        // calculate the digest and print it out
        messageDigest.update(text);
        byte[] digest = messageDigest.digest();
        System.out.println("New digest:");
        System.out.println(printHexBinary(digest));

        // get an RSA cipher object
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // decrypt the ciphered digest using the public key
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
        byte[] decipheredDigest = cipher.doFinal(cipherDigest);
        System.out.println("Deciphered digest:");
        System.out.println(printHexBinary(decipheredDigest));

        // compare digests
        if (digest.length != decipheredDigest.length)
            return false;

        for (int i = 0; i < digest.length; i++)
            if (digest[i] != decipheredDigest[i])
                return false;
        return true;
    }

}
