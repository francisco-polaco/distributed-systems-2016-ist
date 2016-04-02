package pt.upa.transporter.ws;

import javax.jws.WebService;
import java.util.*;

import static pt.upa.transporter.ws.JobStateView.ACCEPTED;
import static pt.upa.transporter.ws.JobStateView.PROPOSED;
import static pt.upa.transporter.ws.JobStateView.REJECTED;

@WebService(
        endpointInterface="pt.upa.transporter.ws.TransporterPortType",
        wsdlLocation="transporter.1_0.wsdl",
        name="TransporterWebService",
        portName="TransporterPort",
        targetNamespace="http://ws.transporter.upa.pt/",
        serviceName="TransporterService"
)
public class TransporterPort implements TransporterPortType {

    private static final int DEFAULT_PRICE = 7;
    private static final String NORTH = "Norte";
    private static final String CENTER = "Centro";
    private static final String SOUTH = "Sul";

    private long idSeed = 0;
    private String mCompanyName;

    private TreeMap<String, JobView> mJobs = new TreeMap<>();
    private ArrayList<String> mLocations = new ArrayList<>();
    private Random mRandom = new Random();


    public TransporterPort(){
        super();
    }

    public TransporterPort(String companyName){
        mCompanyName = companyName;
        int serverId = Integer.parseInt(companyName.substring(14));
        mLocations.add(CENTER);
        mLocations.add((serverId % 2 == 0) ? NORTH : SOUTH);
    }

    @Override
    public String ping(String name) {
        System.out.println("Received: " + name);
        return mCompanyName + ": " + Calendar.DAY_OF_MONTH + "/" + Calendar.MONTH + "/" + Calendar.YEAR +
                " ---> " + Calendar.HOUR_OF_DAY + ":" + Calendar.MINUTE + ":" + Calendar.SECOND;
    }

    @Override
    public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception,
            BadPriceFault_Exception {
        if(isAInvalidRegion(origin) || isAInvalidRegion(destination)){
            throw new BadLocationFault_Exception("Unknown Region.", new BadLocationFault());
        }else if(price < 0){
            throw new BadPriceFault_Exception("Price is below 0.", new BadPriceFault());
        }
        JobView jobView = null;
        if(isOneOfMyLocations(origin) || isOneOfMyLocations(destination) || price <= 100){
            jobView = new JobView();
            jobView.setJobDestination(destination);
            jobView.setJobIdentifier(Long.toString(idSeed++));
            jobView.setCompanyName(mCompanyName);
            jobView.setJobOrigin(origin);
            jobView.setJobState(PROPOSED);
            if(price <= 10) {
                jobView.setJobPrice(DEFAULT_PRICE);
            }else if(mLocations.contains(NORTH)){ // even id
                //TODO : Refactor
                if(price % 2 == 0){
                    jobView.setJobPrice(mRandom.nextInt(price));
                }else{
                    jobView.setJobPrice(mRandom.nextInt(price) + price);
                }
            }else{ //odd id
                if(price % 2 == 0){
                    jobView.setJobPrice(mRandom.nextInt(price) + price);

                }else{
                    jobView.setJobPrice(mRandom.nextInt(price));
                }

            }
        }
        return jobView;
    }

    private boolean isAInvalidRegion(String location) {
        return !(location.equals(NORTH) || location.equals(CENTER)
                || location.equals(SOUTH));
    }

    private boolean isOneOfMyLocations(String location) {
        return mLocations.contains(location);
    }

    @Override
    public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
        if(!mJobs.containsKey(id) || mJobs.get(id).getJobState() != PROPOSED){
            // This verification will avoid unnecessary work
            throw new BadJobFault_Exception("Unknown ID or Job is already accepted.", new BadJobFault());
        }
        JobView jobView = mJobs.get(id);
        if(accept) {
            jobView.setJobState(ACCEPTED);
            (new ChangeJobStatusThread(jobView)).run();
        }else{
            jobView.setJobState(REJECTED);
        }
        return jobView;
    }

    @Override
    public JobView jobStatus(String id) {
        return mJobs.get(id);
    }

    @Override
    public List<JobView> listJobs() {
        ArrayList<JobView> result = new ArrayList<>();
        for(Map.Entry<String,JobView> entry : mJobs.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Override
    public void clearJobs() {
        mJobs.clear();
    }


}
