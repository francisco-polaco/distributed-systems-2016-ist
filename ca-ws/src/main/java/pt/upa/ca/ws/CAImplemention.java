package pt.upa.ca.ws;

import pt.upa.ca.ws.exception.CertificateDoesntExists;

import javax.jws.WebService;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;


/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImplemention implements CA {

    final static String CA_CERTIFICATE_FILE = "ca-certificate.pem.txt";

    private ArrayList<String> mFileNames = new ArrayList<>();

    public CAImplemention(){
        File dir = new File(".");
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("UpaTransporter") && file.getName().endsWith(".cer") ) {
                mFileNames.add(file.getName());
            }
        }
        mFileNames.add("UpaBroker1.cer");
        System.out.println("Number of certificates loaded: " + mFileNames.size());
    }

    @Override
    public byte[] getEntityCertificate(String entity) throws CertificateDoesntExists {
        if(entity == null){
            return null;
        }
        System.out.println(entity + " Certificate Requested...");
        if(mFileNames.contains(entity + ".cer")){
            try {
                return readCertificateFile(entity + ".cer");
            } catch (IOException | CertificateException e){
                System.out.println("We are having problems with our certificates.\n" + e.getMessage());
                throw new CertificateDoesntExists(entity);
            }
        }else{
            throw new CertificateDoesntExists(entity);
        }
    }

    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws IOException
     */
    private byte[] readCertificateFile(String certificateFilePath) throws IOException, CertificateException {

        Certificate certificate = readCertificate(certificateFilePath);

        Certificate caCertificate = readCertificate(CA_CERTIFICATE_FILE);
        PublicKey caPublicKey = caCertificate.getPublicKey();

        if (verifySignedCertificate(certificate, caPublicKey)) {
            System.out.println("The asked certificate is valid");
        } else {
            System.err.println("The asked certificate is not valid");
            throw new CertificateException();
        }
        return Files.readAllBytes(Paths.get(certificateFilePath));

    }

    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws Exception
     */
    private Certificate readCertificate(String certificateFilePath) throws IOException, CertificateException {
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
            Certificate cert = cf.generateCertificate(bis);
            return cert;
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

}
