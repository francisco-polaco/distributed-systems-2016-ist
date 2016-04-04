package pt.upa.broker.ws;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import java.util.*;

import pt.upa.transporter.ws.JobView;
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
    private ArrayList<JobView> jobOffers = new ArrayList<>();
    private ArrayList<JobView> jobAceppted = new ArrayList<>();
    private ArrayList<JobView> jobRejected = new ArrayList<>();

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
        JobView bestOffer = new JobView();
        for (TransporterClient transporter : allTransporters)
            jobOffers.add(transporter.requestJob(origin, destination, price));
        for (JobView Offer : jobOffers) {
            if (bestOffer.getJobPrice() > Offer.getJobPrice()) {
                allTransporters.get(jobOffers.indexOf(bestOffer)).decideJob(bestOffer.getJobIdentifier(), false);
                jobRejected.add(bestOffer);
                bestOffer = Offer;
            } else {
                allTransporters.get(jobOffers.indexOf(Offer)).decideJob(Offer.getJobIdentifier(), false);
                jobRejected.add(Offer);
            }
        }
        allTransporters.get(jobOffers.indexOf(bestOffer)).decideJob(bestOffer.getJobIdentifier(), true);
        jobAceppted.add(bestOffer);
        jobOffers.clear();
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
}


