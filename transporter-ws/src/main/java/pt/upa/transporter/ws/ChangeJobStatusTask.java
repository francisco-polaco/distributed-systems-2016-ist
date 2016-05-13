package pt.upa.transporter.ws;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;



class ChangeJobStatusTask extends TimerTask {

    private static final int BOUND = 4001;
    private static final JobStateView[] states = {JobStateView.HEADING, JobStateView.ONGOING, JobStateView.COMPLETED};

    private JobView mJobView;
    private Timer mTimer;
    private int mStatePointer;
    private ThreadLocalRandom mRandom;
    private static ReentrantLock mLock;


    public ChangeJobStatusTask(JobView jobView, ThreadLocalRandom random, int statePointer){
        mJobView = jobView;
        mTimer = new Timer();
        mRandom = random;
        mLock = new ReentrantLock();
        mStatePointer = statePointer;
        mTimer.schedule(this, mRandom.nextInt(BOUND) + 1000);
    }

    public void run() {
        System.out.println("Welcome to the best TimerTask! This will be the " + (mStatePointer + 1) + "th time.");
        mLock.lock();
        mJobView.setJobState(states[mStatePointer++]);
        mLock.unlock();
        System.out.println("The " + mJobView.getJobIdentifier() + " JobView's state is: " + mJobView.getJobState());
        if(mStatePointer < 3) {
            new ChangeJobStatusTask(mJobView, mRandom, mStatePointer);
        }
        System.out.println("See you soon!");
    }

}
