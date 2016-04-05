package pt.upa.broker.ws;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import java.util.*;

import pt.upa.transporter.ws.JobView;
import static pt.upa.broker.ws.TransportStateView.*;
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
    //private ArrayList<JobView> jobOffers = new ArrayList<>();
    private ArrayList<TransportView> jobOffers = new ArrayList<>();

    private long idSeed = 0;

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


        for (TransporterClient transporter : allTransporters){
            TransportView transport = creatTransportView(origin, destination, price, transporter.getName());
            jobOffers.add(transport);
            transporter.requestJob(transport.getOrigin(),transport.getDestination(),transport.getPrice());
        }

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

    public void getAllTransporters(String uddiURL) throws JAXRException {
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        Collection<String> endpointAddress = uddiNaming.list("UpaTransp%");
        for (String endpAdd :  endpointAddress) {
            TransporterClient transporter = new TransporterClient(uddiURL, endpAdd);
            allTransporters.add(transporter);
        }
    }

    public TransportView creatTransportView(String origin, String destination, int price, String companyName){
        TransportView transport = new TransportView();
        String id = Long.toString(idSeed++);
        transport.setTransporterCompany(companyName);
        transport.setDestination(destination);
        transport.setOrigin(origin);
        transport.setPrice(price);
        transport.setId(id);
        transport.setState(REQUESTED);
    }
}


