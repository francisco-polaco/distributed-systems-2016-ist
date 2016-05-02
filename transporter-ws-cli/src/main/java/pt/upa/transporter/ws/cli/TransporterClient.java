package pt.upa.transporter.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.*;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class TransporterClient{

    private TransporterPortType mPort;

    public TransporterClient(String uddiURL, String name) throws JAXRException, TransporterClientException {

        String endpointAddress;
        try {
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
            UDDINaming uddiNaming = new UDDINaming(uddiURL);

            System.out.printf("Looking for '%s'%n", name);
            endpointAddress = uddiNaming.lookup(name);
        }catch (Exception e) {
            String msg = String.format("Client failed lookup on UDDI at %s!", uddiURL);
            throw new TransporterClientException(msg, e);
        }

        if (endpointAddress == null) {
            String msg = String.format("Service with name %s not found on UDDI at %s", name, uddiURL);
            throw new TransporterClientException(msg);
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        TransporterService service = new TransporterService();
        mPort = service.getTransporterPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) mPort;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    public TransporterClient(TransporterPortType port){
        mPort = port;
    }

    public synchronized String ping(String message) {
        return mPort.ping(message);
    }


    public synchronized JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
        return mPort.requestJob(origin, destination, price);
    }

    public synchronized JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
        return mPort.decideJob(id, accept);
    }


    public synchronized JobView jobStatus(String id) {
        return mPort.jobStatus(id);
    }


    public synchronized List<JobView> listJobs() {
        return mPort.listJobs();
    }


    public synchronized void clearJobs() {
        mPort.clearJobs();
    }

}
