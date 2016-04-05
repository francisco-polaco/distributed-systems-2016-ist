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

    private TreeMap<String, TransporterClient> allTransporters = new TreeMap<>();
    private TreeMap<String, TransportView> jobOffers = new TreeMap<>();

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


        for (TransporterClient transporter : allTransporters.values()){
            TransportView transport = creatTransportView(origin, destination, price, transporter.getName());
            JobView Offer = transporter.requestJob(transport.getOrigin(),transport.getDestination(),transport.getPrice());
            transport.setPrice(Offer.getJobPrice());
            transport.setState(BUDGETED);
            jobOffers.put(transport.getId(), transport);
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
            allTransporters.put(transporter.getName(), transporter);
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

    public void jobDecision() {
        TransportView bestOffer = null;

        for (TransportView offer : jobOffers.values()){
            if (bestOffer == null)
                bestOffer = offer;
            if(bestOffer.getPrice() > offer.getPrice()){
                allTransporters.get(bestOffer.getTransporterCompany()).decideJob(bestOffer.getId(), false);
                bestOffer.setState(FAILED);
                bestOffer = offer;
            }
            else{
                allTransporters.get(offer.getTransporterCompany()).decideJob(offer.getId(), false);
                offer.setState(FAILED);
            }
        }
        allTransporters.get(bestOffer.getTransporterCompany()).decideJob(bestOffer.getId(), true);
        bestOffer.setState(BOOKED);
    }

}


