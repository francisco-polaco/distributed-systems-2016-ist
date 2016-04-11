package pt.upa.transporter.ws;

import javax.jws.WebService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static pt.upa.transporter.ws.JobStateView.*;

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

    private static long idSeed = 0;
    private String mCompanyName;
    private boolean mNorthRegion;

    private ConcurrentHashMap<String, JobView> mJobs = new ConcurrentHashMap<>();
    private ArrayList<String> mLocations = new ArrayList<>();
    private ArrayList<String> mKnownLocations = new ArrayList<>();
    private ThreadLocalRandom mRandom =  ThreadLocalRandom.current();


    public TransporterPort(){
        super();
    }

    public TransporterPort(String companyName){
        mCompanyName = companyName;
        int serverId = Integer.parseInt(companyName.substring(14));
        mLocations.addAll(Arrays.asList("Lisboa",
                "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"));
        if(serverId % 2 == 0){
            mLocations.addAll(Arrays.asList("Porto",
                    "Braga", "Viana do Castelo", "Vila Real", "Braganca"));
            mNorthRegion = true;
            mKnownLocations.addAll(Arrays.asList("Setubal",
                    "Evora", "Portalegre", "Beja", "Faro"));
        } else {
            mLocations.addAll(Arrays.asList("Setubal",
                    "Evora", "Portalegre", "Beja", "Faro"));
            mNorthRegion = false;
            mKnownLocations.addAll(Arrays.asList("Porto",
                    "Braga", "Viana do Castelo", "Vila Real", "Braganca"));
        }
        mKnownLocations.addAll(mLocations);
    }

    @Override
    public String ping(String name) {
        System.out.println("Ping Received: " + name);
        GregorianCalendar rightNow = new GregorianCalendar();
        return mCompanyName + ": " +  rightNow.get(Calendar.HOUR_OF_DAY) + ":" +
                rightNow.get(Calendar.MINUTE) + ":" + rightNow.get(Calendar.SECOND) +
                " --- " +
                rightNow.get(Calendar.DAY_OF_MONTH) + "/" + rightNow.get(Calendar.MONTH) + "/" +
                rightNow.get(Calendar.YEAR);
    }

    @Override
    public JobView requestJob(String origin, String destination, int price) throws BadLocationFault_Exception,
            BadPriceFault_Exception {
        if(isAInvalidRegion(origin) || isAInvalidRegion(destination)){
            throw new BadLocationFault_Exception("Unknown Region.", new BadLocationFault());
        }else if(price < 0){
            throw new BadPriceFault_Exception("Price is below 0.", new BadPriceFault());
        }else {
            JobView jobView = null;
            if (price <= 100 && (doIWorkHere(origin) || doIWorkHere(destination))) {
                jobView = new JobView();
                String id = Long.toString(TransporterPort.idSeed++);
                jobView.setJobDestination(destination);
                jobView.setJobIdentifier(id);
                jobView.setCompanyName(mCompanyName);
                jobView.setJobOrigin(origin);
                jobView.setJobState(PROPOSED);

                if (price <= 10)
                    jobView.setJobPrice(DEFAULT_PRICE);

                else if (mNorthRegion) { // even id
                    //TODO : Refactor
                    if (price % 2 == 0)
                        jobView.setJobPrice(mRandom.nextInt(price));
                    else
                        jobView.setJobPrice(mRandom.nextInt(price) + price);

                } else { //odd id
                    if (price % 2 == 0)
                        jobView.setJobPrice(mRandom.nextInt(price) + price);
                    else
                        jobView.setJobPrice(mRandom.nextInt(price));
                }
                mJobs.put(id, jobView);
            }
            return jobView;
        }
    }

    private boolean isAInvalidRegion(String location) {
        return !mKnownLocations.contains(location);
    }

    private boolean doIWorkHere(String location) {
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
            new ChangeJobStatusTask(jobView, mRandom, 0);
        }else
            jobView.setJobState(REJECTED);
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
        idSeed = 0;
    }


}
