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

    public BrokerClient(String uddiURL, String name) throws JAXRException, BrokerClientException {

        String endpointAddress;
        try {
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
            UDDINaming uddiNaming = new UDDINaming(uddiURL);

            System.out.printf("Looking for '%s'%n", name);
            endpointAddress = uddiNaming.lookup(name);
        }catch (Exception e) {
            String msg = String.format("Client failed lookup on UDDI at %s!",
                    uddiURL);
            throw new BrokerClientException(msg, e);
        }
        if (endpointAddress == null) {
            String msg = String.format("Service with name %s not found on UDDI at %s", name, uddiURL);
            throw new BrokerClientException(msg);
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        BrokerService service = new BrokerService();
        mPort = service.getBrokerPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) mPort;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
    }

    public String ping(String message) {
        return mPort.ping(message);
    }


    public String requestTransport(String origin, String destination, int price) throws UnavailableTransportPriceFault_Exception, UnavailableTransportFault_Exception, UnknownLocationFault_Exception, InvalidPriceFault_Exception {
        return mPort.requestTransport(origin, destination, price);
    }


    public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
        return mPort.viewTransport(id);
    }


    public List<TransportView> listTransports() {
        return mPort.listTransports();
    }


    public void clearTransports() {
        mPort.clearTransports();
    }
}
