package pcd.ass01_concurrent.concurrent_components;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SelfResettingBarrier {

    private int numberOfWorkers;
    private int arrivedWorkers;
    private int exitedWorkers;
    private Lock mutex;
    private Condition condition;

    public SelfResettingBarrier(int numberOfWorkers) {
        this.numberOfWorkers = numberOfWorkers;
        this.mutex = new ReentrantLock(true); // Fairness is important
        this.condition = mutex.newCondition();
        reset();
    }

    public void waitForOthers() throws InterruptedException {
        try {
            mutex.lock();
            arrivedWorkers++;
            if (arrivedWorkers < numberOfWorkers) {
                while (arrivedWorkers < numberOfWorkers) {
                    condition.await();
                }
            } else {
                condition.signalAll();
            }
            exitedWorkers++;
            if (exitedWorkers == numberOfWorkers) {
                reset();
            }
        } finally {
            mutex.unlock();
        }
    }

    public void reset() {
        mutex.lock();
        arrivedWorkers = 0;
        exitedWorkers = 0;
        mutex.unlock();
    }

}
