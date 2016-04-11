package pt.upa.broker.ws;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import java.util.*;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
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
    private TreeMap<String, String> IdConvTable = new TreeMap<>();
    private ArrayList<String> North = new ArrayList<>(Arrays.asList("Porto", "Braga", "Viana do Castelo", "Vila Real", "Braganca"));
    private ArrayList<String> Center = new ArrayList<>(Arrays.asList("Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"));
    private ArrayList<String> South =  new ArrayList<>(Arrays.asList("Setubal", "Evora", "Portalegre", "Beja", "Faro"));
    private long idSeed = 0;
    private boolean JobOffer = false;


    public BrokerPort() throws JAXRException {
        super();
    }

    public BrokerPort(String uddiURL) throws JAXRException {
        super();
        getAllTransporters(uddiURL);
    }

    public BrokerPort(TreeMap<String, TransporterClient> transporters){
        allTransporters = transporters;
    }

    @Override
    public String ping(String name){
        System.out.println("Received: " + name);
        return "Ping: " + name;
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {

        if(invalidPrice(price))
            throw new InvalidPriceFault_Exception("Price is below 0.", new InvalidPriceFault());

        if(isInalidLocation(origin) || isInalidLocation(destination))
            throw new UnknownLocationFault_Exception("Unknown Location.", new UnknownLocationFault());

        for (String companyName : allTransporters.keySet()){
            TransportView transport = creatTransportView(origin, destination, price, companyName);
            JobView offer = null;
            try{
                offer = allTransporters.get(companyName).requestJob(transport.getOrigin(),
                        transport.getDestination(),transport.getPrice());
            }catch (Exception e){
                // FIXME: JP
                e.getMessage();
            }
            if((offer != null) && !(offer.getJobState().value().equals("REJECTED"))){
                transport.setPrice(offer.getJobPrice());
                transport.setState(BUDGETED);
                jobOffers.put(transport.getId(), transport);
                IdConvTable.put(transport.getId(),offer.getJobIdentifier());
                setJobOffer(true);
            }
            else
                transport.setState(FAILED);
        }

        if(!JobOffer)
            throw new UnavailableTransportFault_Exception("No Transport Available", new UnavailableTransportFault());
        // FIXME: JP
        String id = null;
        try{
            id = jobDecision(price).getId();
        }catch (BadJobFault_Exception e){
            e.getMessage();
        }
        return id;
    }

    @Override
    public TransportView viewTransport(String id)  throws UnknownTransportFault_Exception{
        updateView(jobOffers.get(id));
        return jobOffers.get(id);
    }

    @Override
    public List<TransportView> listTransports(){
        ArrayList<TransportView> result = new ArrayList<>();
        for(TransportView entry : jobOffers.values()) {
            updateView(entry);
            result.add(entry);
        }
        return result;
    }

    @Override
    public void clearTransports(){
        for(TransporterClient transporters : allTransporters.values())
            transporters.clearJobs();
        jobOffers.clear();
    }

    public void getAllTransporters(String uddiURL) throws JAXRException {
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        Collection<String> endpointAddress = uddiNaming.list("UpaTransporter%");
        for (String endpAdd :  endpointAddress) {
            TransporterClient transporter = new TransporterClient(uddiURL, getCompanyName(endpAdd));
           allTransporters.put(getCompanyName(endpAdd), transporter);
        }
    }

    private String getCompanyName(String endpAdd){
        String[] companyPort = endpAdd.split("/");
        companyPort = companyPort[2].split(":");
        int companyNumber = Integer.parseInt(companyPort[1]) - 8080;
        String companyName = "UpaTransporter" + companyNumber;
        return companyName;
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
        return transport;
    }

    private TransportView jobDecision(int price) throws UnavailableTransportPriceFault_Exception, BadJobFault_Exception {
        TransportView bestOffer = null;

        for (TransportView offer : jobOffers.values()){
            if (bestOffer == null)
                bestOffer = offer;
            if(bestOffer.getPrice() > offer.getPrice()){
                allTransporters.get(bestOffer.getTransporterCompany()).decideJob(IdConvTable.get(bestOffer.getId()), false);
                bestOffer.setState(FAILED);
                bestOffer = offer;
            }
            else{
                allTransporters.get(offer.getTransporterCompany()).decideJob(IdConvTable.get(offer.getId()), false);
                offer.setState(FAILED);
            }
        }

        if(bestOffer.getPrice() > price)

            throw new UnavailableTransportPriceFault_Exception("Price is above the client offer", new UnavailableTransportPriceFault());
        allTransporters.get(bestOffer.getTransporterCompany()).decideJob(IdConvTable.get(bestOffer.getId()), true);
        bestOffer.setState(BOOKED);

        return bestOffer;
    }

    public void updateView(TransportView transport){
        JobView job = allTransporters.get(transport.getTransporterCompany()).jobStatus(IdConvTable.get(transport.getId()));
        if(job.getJobState().value() == "HEADING")
            transport.setState(HEADING);
        if(job.getJobState().value() == "ONGOING")
            transport.setState(ONGOING);
        if(job.getJobState().value() == "COMPLETED")
            transport.setState(COMPLETED);
    }

    private boolean isInalidLocation(String location){
        return !(North.contains(location) || South.contains(location) || Center.contains(location));
    }

    private boolean invalidPrice(int price){
        return (price < 0);
    }

    private void setJobOffer(boolean s) {JobOffer = s;}

}


