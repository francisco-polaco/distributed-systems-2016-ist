package pt.upa.broker.ws.cli;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.*;
import pt.upa.transporter.ws.TransporterPortType;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

public class BrokerClient {

    private BrokerPortType mPort;

    public BrokerClient(String uddiURL, String name) throws JAXRException {
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
        BrokerService service = new BrokerService();
        BrokerPortType port = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }


    public String ping(String message) {
        return mPort.ping(message);
    }


    public String requestTransport(String origin, String destination, int price){
        String request = "";
        try{
            request = mPort.requestTransport(origin, destination, price);
        }catch(InvalidPriceFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (UnavailableTransportFault_Exception e){
            System.err.println(e.getMessage());
        }catch(UnavailableTransportPriceFault_Exception e) {
            System.err.println(e.getMessage());
        }catch (UnknownLocationFault_Exception e){
            System.err.println(e.getMessage());
        }
        return request;
    }


    public TransportView viewTransport(String id){
        TransportView transportView = null;
        try {
            transportView = mPort.viewTransport(id);
        }catch(UnknownTransportFault_Exception e) {
            System.err.println(e.getMessage());
        }
        return transportView;
    }


    public List<TransportView> listTransports(TransporterPortType port) {
        return mPort.listTransports();
    }


    public void clearTransports(TransporterPortType port) {
        mPort.clearTransports();
    }
}
