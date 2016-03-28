package pt.upa.transporter.ws;

import javax.jws.WebService;
import java.util.*;

import static pt.upa.transporter.ws.JobStateView.PROPOSED;

@WebService(
        endpointInterface="pt.upa.transporter.ws.TransporterPortType",
        wsdlLocation="transporter.1_0.wsdl",
        name="TransporterWebService",
        portName="TransporterPort",
        targetNamespace="http://ws.transporter.upa.pt/",
        serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType{

    private static final int DEFAULT_PRICE = 7;

    private long idSeed = 0;
    private String mCompanyName;

    private TreeMap<String, JobView> mActiveJobs = new TreeMap<>();
    private TreeMap<String, JobView> mPendingJobs = new TreeMap<>();
    private ArrayList<String> mLocations = new ArrayList<>();
    private Random mRandom = new Random();


    public TransporterPort(){
        super();
    }

    public TransporterPort(String companyName){
        mCompanyName = companyName;
        int serverId = Integer.parseInt(companyName.substring(14));
        mLocations.add(LocationConstants.CENTER);
        mLocations.add((serverId % 2 == 0) ? LocationConstants.NORTH : LocationConstants.SOUTH);
    }

    @Override
    public String ping(String name) {
        System.out.println("Received: " + name);
        return "Ping: " + Calendar.DAY_OF_MONTH + " / " + Calendar.MONTH + " / " + Calendar.YEAR +
                " ---> " + Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE + ":" + Calendar.SECOND;
    }

    @Override
    public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception, BadPriceFault_Exception {
        if(isAInvalidRegion(origin) || isAInvalidRegion(destination)){
            throw new BadLocationFault_Exception("Unknown Region.", new BadLocationFault());
        }else if(price < 0){
            throw new BadPriceFault_Exception("Price is below 0.", new BadPriceFault());
        }

        if(isOneOfMyLocations(origin) || isOneOfMyLocations(destination) || price <= 100){
            JobView jobView = new JobView();
            jobView.setJobDestination(destination);
            jobView.setJobIdentifier(Long.toString(idSeed++));
            jobView.setCompanyName(mCompanyName);
            jobView.setJobOrigin(origin);
            jobView.setJobState(PROPOSED);
            if(price <= 10) {
                jobView.setJobPrice(DEFAULT_PRICE);
                return jobView;
            }
            else if(mLocations.contains(LocationConstants.NORTH)){ // even id
                //TODO : Refactor
                if(price % 2 == 0){
                    jobView.setJobPrice(mRandom.nextInt(price));
                }else{
                    jobView.setJobPrice(mRandom.nextInt(price) + price);
                }
                return jobView;
            }else{ //odd id
                if(price % 2 == 0){
                    jobView.setJobPrice(mRandom.nextInt(price) + price);

                }else{
                    jobView.setJobPrice(mRandom.nextInt(price));
                }
                return jobView;
            }
        }


        return null;
    }

    private boolean isAInvalidRegion(String location) {
        return !(location.equals(LocationConstants.NORTH) || location.equals(LocationConstants.CENTER)
                || location.equals(LocationConstants.SOUTH));
    }

    private boolean isOneOfMyLocations(String location) {
        return mLocations.contains(location);
    }

    @Override
    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
        if(!mPendingJobs.containsKey(id)){
            throw new BadJobFault_Exception("Unknown ID", new BadJobFault());
        }
        JobView jobView = null; // If we dont need to return this, we should return always null;
        if(accept) {
            jobView = mPendingJobs.get(id);
            mActiveJobs.put(id, jobView);
        }
        mPendingJobs.remove(id);
        return jobView;
    }

    @Override
    public JobView jobStatus(String id) {
        if(mActiveJobs.containsKey(id))
            return mActiveJobs.get(id);
        else
            return null;
    }

    @Override
    public List<JobView> listJobs() {
        ArrayList<JobView> result = new ArrayList<>();
        for(Map.Entry<String,JobView> entry : mActiveJobs.entrySet()) {
            result.add(entry.getValue());
        }
        for(Map.Entry<String,JobView> entry : mPendingJobs.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public void clearJobs() {
        mActiveJobs.clear();
        mPendingJobs.clear();
    }


}
