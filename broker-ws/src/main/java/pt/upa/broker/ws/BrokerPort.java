package pt.upa.broker.ws;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.JobView;
import static pt.upa.broker.ws.TransportStateView.*;
import pt.upa.transporter.ws.cli.TransporterClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClientException;

@WebService(
        endpointInterface="pt.upa.broker.ws.BrokerPortType",
        wsdlLocation="broker.2_0.wsdl",
        name="BrokerWebService",
        portName="BrokerPort",
        targetNamespace="http://ws.broker.upa.pt/",
        serviceName="BrokerService"
)
public class BrokerPort implements BrokerPortType{

    private ConcurrentHashMap<String, TransporterClient> allTransporters = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TransportView> jobOffers = new ConcurrentHashMap<>();
    //private ConcurrentHashMap<String, TransportView> jobOffers_aux = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> idConvTable = new ConcurrentHashMap<>();
    private ArrayList<String> north = new ArrayList<>(Arrays.asList("Porto", "Braga", "Viana do Castelo", "Vila Real", "Braganca"));
    private ArrayList<String> center = new ArrayList<>(Arrays.asList("Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"));
    private ArrayList<String> south =  new ArrayList<>(Arrays.asList("Setubal", "Evora", "Portalegre", "Beja", "Faro"));
    private int idSeed = 0;


    public BrokerPort() {
        super();
    }

    public BrokerPort(String uddiURL) throws JAXRException, TransporterClientException {
        super();
        getAllTransporters(uddiURL);
    }

    public BrokerPort(ConcurrentHashMap<String, TransporterClient> transporters){
        allTransporters = transporters;
    }

    @Override
    public String ping(String name){
        if(name == null){
            name = "";
        }
        System.out.println("Received Ping: " + name);
        GregorianCalendar rightNow = new GregorianCalendar();
        String result = "Broker: " + rightNow.get(Calendar.HOUR_OF_DAY) + ":" +
                rightNow.get(Calendar.MINUTE) + ":" + rightNow.get(Calendar.SECOND) +
                " --- " +
                rightNow.get(Calendar.DAY_OF_MONTH) + "/" + rightNow.get(Calendar.MONTH) + "/" +
                rightNow.get(Calendar.YEAR);;
        for(String key : allTransporters.keySet()){
            result += allTransporters.get(key).ping(name);
        }
        return result;
    }

    @Override
    public String requestTransport(String origin, String destination, int price)
            throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
            UnknownLocationFault_Exception {

        ConcurrentHashMap<String, TransportView> jobOffers_aux = new ConcurrentHashMap<>();

        if(invalidPrice(price))
            throw new InvalidPriceFault_Exception("Price is below 0.", new InvalidPriceFault());

        if(origin == null || destination == null || isInvalidLocation(origin) || isInvalidLocation(destination))
            throw new UnknownLocationFault_Exception("Unknown Location.", new UnknownLocationFault());
        boolean jobOffer = false;
        for (String companyName : allTransporters.keySet()){
            TransportView transport = createTransportView(origin, destination, price, companyName);
            JobView offer = null;
            try{
                offer = allTransporters.get(companyName).requestJob(transport.getOrigin(),
                        transport.getDestination(),transport.getPrice());
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            if((offer != null) && !(offer.getJobState().value().equals("REJECTED"))){
                transport.setPrice(offer.getJobPrice());
                transport.setState(BUDGETED);
                jobOffers_aux.put(transport.getId(), transport);
                idConvTable.put(transport.getId(), offer.getJobIdentifier());
                jobOffer = true;
            }
            else {
                transport.setState(FAILED);
            }
        }

        if(!jobOffer){
            throw new UnavailableTransportFault_Exception("No Transport Available", new UnavailableTransportFault());}
        String id = null;
        try{
            id = jobDecision(price, jobOffers_aux).getId();
        }catch (BadJobFault_Exception e){
            System.out.println(e.getMessage());
        }
        return id;
    }

    @Override
    public TransportView viewTransport(String id)  throws UnknownTransportFault_Exception{
        if(id == null || !jobOffers.containsKey(id)){
            throw new UnknownTransportFault_Exception(id, new UnknownTransportFault());
        }
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
        idConvTable.clear();
        idSeed = 0;
    }
    // FIXME: DANIEL
    @Override
    public String iAmAlive(String in) {
        return null;
    }

    public String updateClr(String in) {
        return null;
    }

    public String updateSeed(int in) {
        return null;
    }

    public String updateOffers(String id, TransportView transportView) {
        return null;
    }

    public String updateTable(String id, String target) {
        return null;
    }

    public String updateTransporters(String companyName, String endpointAddress) {
        return null;
    }


    private void getAllTransporters(String uddiURL) throws JAXRException, TransporterClientException {
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
        return "UpaTransporter" + companyNumber;
    }

    private TransportView createTransportView(String origin, String destination, int price, String companyName){
        TransportView transport = new TransportView();
        String id = Integer.toString(idSeed++);
        transport.setTransporterCompany(companyName);
        transport.setDestination(destination);
        transport.setOrigin(origin);
        transport.setPrice(price);
        transport.setId(id);
        transport.setState(REQUESTED);
        return transport;
    }

    private TransportView jobDecision(int price, ConcurrentHashMap<String, TransportView> jobOffers_aux) throws UnavailableTransportPriceFault_Exception, BadJobFault_Exception {

        ArrayList<TransportView> transportViews = new ArrayList<>();
        transportViews.addAll(jobOffers_aux.values());
        if(transportViews.size() > 1) {
            Collections.sort(transportViews, new Comparator<TransportView>() {
                @Override
                public int compare(TransportView o1, TransportView o2) {
                    return o1.getPrice() - o2.getPrice();
                }
            });
        }
        TransportView bestOffer = transportViews.get(0);

        for (TransportView offer : transportViews){
            if(!offer.equals(bestOffer)) {
                offer.setState(FAILED);
                allTransporters.get(offer.getTransporterCompany()).decideJob(idConvTable.get(offer.getId()), false);
                jobOffers.put(offer.getId(),offer);
            }
        }

        if(bestOffer.getPrice() > price) {
            bestOffer.setState(FAILED);
            allTransporters.get(bestOffer.getTransporterCompany()).decideJob(idConvTable.get(bestOffer.getId()), false);
            jobOffers.put(bestOffer.getId(),bestOffer);
            throw new UnavailableTransportPriceFault_Exception("Price is above the client offer", new UnavailableTransportPriceFault());
        }

        bestOffer.setState(BOOKED);
        allTransporters.get(bestOffer.getTransporterCompany()).decideJob(idConvTable.get(bestOffer.getId()), true);
        jobOffers.put(bestOffer.getId(),bestOffer);
        jobOffers_aux.clear();
        return bestOffer;
    }

    private void updateView(TransportView transport){
        JobView job = allTransporters.get(transport.getTransporterCompany()).jobStatus(idConvTable.get(transport.getId()));
        if(job != null){
            switch (job.getJobState().value()) {
                case "HEADING":
                    transport.setState(HEADING);
                    break;
                case "ONGOING":
                    transport.setState(ONGOING);
                    break;
                case "COMPLETED":
                    transport.setState(COMPLETED);
                    break;
            }
        }
    }

    private boolean isInvalidLocation(String location){
        return !(north.contains(location) || south.contains(location) || center.contains(location));
    }

    private boolean invalidPrice(int price){
        return (price < 0);
    }


}


