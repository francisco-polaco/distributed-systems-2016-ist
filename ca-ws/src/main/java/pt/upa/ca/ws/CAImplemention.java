package pt.upa.ca.ws;

import com.sun.org.apache.bcel.internal.classfile.Unknown;
import pt.upa.ca.ws.exception.UnknownServiceException;

import javax.jws.WebService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;


/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImplemention implements CA {

    @Override
    public byte[] getEntityCertificate(String entity) throws UnknownServiceException {
        if(entity == null){
            return null;
        }
        System.out.println(entity + " Certificate Requested...");
        switch (entity) {
            case "UpaBroker":
                try {
                    return readCertificateFile("UpaBroker.cer");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "UpaTransporter1":
                try {
                    return readCertificateFile("UpaTransporter1.cer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.err.println("INVALID REQUEST!!!!");
                throw new UnknownServiceException();
        }
        return null;
    }

    /**
     * Reads a certificate from a file
     *
     * @return
     * @throws IOException
     */
    private byte[] readCertificateFile(String certificateFilePath) throws IOException {
        return Files.readAllBytes(Paths.get(certificateFilePath));

    }

}
