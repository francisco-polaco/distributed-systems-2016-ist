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

    public TransporterClient(String uddiURL, String name) throws JAXRException {

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);
        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
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

    public String ping(String message) {
        return mPort.ping(message);
    }


    public JobView requestJob(String origin, String destination, int price){
        JobView jobView = null;
        try{
            jobView = mPort.requestJob(origin, destination, price);
        }catch(BadLocationFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (BadPriceFault_Exception e){
            System.err.println(e.getMessage());
        }
        return jobView;
    }

    public JobView decideJob(String id, boolean accept){
        JobView jobView = null;
        try{
            jobView = mPort.decideJob(id, accept);
        }catch(BadJobFault_Exception e) {
            System.err.println(e.getMessage());
        }
        return jobView;
    }


    public JobView jobStatus(String id) {
        return mPort.jobStatus(id);
    }


    public List<JobView> listJobs() {
        return mPort.listJobs();
    }


    public void clearJobs() {
        mPort.clearJobs();
    }

}
