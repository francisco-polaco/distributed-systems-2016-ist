package pt.upa.transporter.ws;

import java.util.Random;

/**
 * Created by xxlxpto on 30-03-2016.
 */
public class ChangeJobStatusThread extends Thread {

    private static final int BOUND = 4001;
    private JobView mJobView;

    public ChangeJobStatusThread(JobView jobView){
        mJobView = jobView;
    }

    public void run() {
        System.out.println("Hello from a thread!");
        JobStateView[] states = {JobStateView.HEADING, JobStateView.ONGOING, JobStateView.COMPLETED};
        int sleepMs;
        Random random = new Random();
        for(JobStateView jb : states){
            sleepMs = random.nextInt(BOUND);
            System.out.println("I'm going to sleep " + sleepMs + " ms.");
            try {
                Thread.sleep(sleepMs + 1000);
            }catch (InterruptedException e){
                System.err.println(e.getMessage());
            }
            mJobView.setJobState(jb);
        }
        System.out.println("Goodbye from a thread!");
    }

}
