package pt.upa.transporter.ws;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xxlxpto on 30-03-2016.
 */
public class ChangeJobStatusTask extends TimerTask {

    private static final int BOUND = 4001;
    private static final JobStateView[] states = {JobStateView.HEADING, JobStateView.ONGOING, JobStateView.COMPLETED};

    private JobView mJobView;
    private Timer mTimer;
    private int mStatePointer;
    private Random mRandom;
    private ReentrantLock mLock;
    // scheduling the task at interval


    public ChangeJobStatusTask(JobView jobView){
        mJobView = jobView;
        mTimer = new Timer();
        mRandom = new Random();
        mLock = new ReentrantLock();
        mStatePointer = 0;
        mTimer.schedule(this, mRandom.nextInt(BOUND));
    }

    public void run() {
        System.out.println("Welcome to the best TimerTask!");
        if(mStatePointer < 2) {
            mTimer.schedule(this, mRandom.nextInt(BOUND));
        }

        mLock.lock();
        mJobView.setJobState(states[mStatePointer]);
        mLock.unlock();
        mStatePointer++;

        System.out.println("See you soon!");
    }

}
