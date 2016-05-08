package pt.upa.ca.ws;

import pt.upa.ca.ws.exception.CertificateDoesntExists;

import javax.jws.WebService;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.util.ArrayList;


/**
 * Created by xxlxpto on 06-05-2016.
 */
@WebService(endpointInterface = "pt.upa.ca.ws.CA")
public class CAImplemention implements CA {

    private ArrayList<String> mFileNames = new ArrayList<>();

    public CAImplemention(){
        File dir = new File(".");
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("UpaTransporter") && file.getName().endsWith(".cer") ) {
                mFileNames.add(file.getName());
            }
        }
        mFileNames.add("UpaBroker.cer");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            throw new CertificateDoesntExists(entity);
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
