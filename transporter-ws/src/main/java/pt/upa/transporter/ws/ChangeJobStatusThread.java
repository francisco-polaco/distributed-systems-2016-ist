package pt.upa.transporter.ws;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

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
        JobStateView[] states = {JobStateView.HEADING, JobStateView.ONGOING, JobStateView.COMPLETED};
        Random random = new Random();
        ReentrantLock lock = new ReentrantLock();

        System.out.println("Hello from a thread!");
        for(JobStateView jb : states){
            int sleepMs = random.nextInt(BOUND);
            System.out.println("I'm going to sleep " + sleepMs + " ms.");

            try {
                Thread.sleep(sleepMs + 1000);
            }catch (InterruptedException e){
                System.err.println(e.getMessage());
            }

            lock.lock();
            mJobView.setJobState(jb);
            lock.unlock();
        }
        System.out.println("Goodbye from a thread!");
    }

}
