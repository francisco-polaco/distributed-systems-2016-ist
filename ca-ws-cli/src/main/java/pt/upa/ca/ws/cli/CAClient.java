package pt.upa.ca.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CA;
import pt.upa.ca.ws.CAImplementionService;
import pt.upa.ca.ws.CertificateDoesntExists_Exception;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

/**
 * Created by xxlxpto on 06-05-2016.
 */

public class CAClient {
    private static final String UDDI_URL = "http://localhost:9090";
    private static final String UPA_CA = "UpaCA";
    private CA mCa;

    public CAClient() throws JAXRException {
        System.out.println("============CA Client============");
        System.out.println("Creating CA Client...");
        CAImplementionService tttImplService = new CAImplementionService();
        mCa = tttImplService.getCAImplementionPort();

        System.out.printf("Contacting UDDI at %s%n", UDDI_URL);
        UDDINaming uddiNaming = new UDDINaming(UDDI_URL);

        System.out.printf("Looking for '%s'%n", UPA_CA);
        String endpointAddress = uddiNaming.lookup(UPA_CA);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");


        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) mCa;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    public void getAndWriteEntityCertificate(String entity, String filename) throws IOException, CertificateDoesntExists_Exception {
        byte[] certificate = mCa.getEntityCertificate(entity);
        File f = new File(filename);
        if(f.createNewFile()) {
            System.out.println("Writing File " + filename);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(certificate);
            fileOutputStream.close();
        }
        System.out.println("============END: CA Client============");
    }




}
