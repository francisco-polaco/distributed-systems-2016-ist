package pt.upa.broker.ws;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import java.util.*;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import java.util.List;

@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPortType",
        wsdlLocation="broker.1_0.wsdl",
        name="BrokerWebService",
        portName="BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType{

    private ArrayList<TransporterClient> allTransporters = new ArrayList<>();

	// TODO

    @Override
    public String ping(String name){
        System.out.println("Received: " + name);
        return "Ping: " + name;
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {
     //   transporterclient.requestJob(origin, destination, price);
    return null;
    }

    @Override
    public TransportView viewTransport(String id)  throws UnknownTransportFault_Exception{
        return null;
    }

    @Override
    public List<TransportView> listTransports(){
        return null;
    }

    @Override
    public void clearTransports(){

    }

    public void getAllClients(String uddiURL) throws JAXRException {
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        Collection<String> endpointAddress = uddiNaming.list("UpaTransp%");
        for (String endpAdd :  endpointAddress) {
            TransporterClient transporter = new TransporterClient(uddiURL, endpAdd);
            allTransporters.add(transporter);
        }
    }
}


