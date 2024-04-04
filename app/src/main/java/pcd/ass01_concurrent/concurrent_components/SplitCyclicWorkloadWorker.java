package pcd.ass01_concurrent.concurrent_components;

import java.util.Queue;

public class SplitCyclicWorkloadWorker extends Thread {

    private Queue<Runnable> tasksQueue;
    private SelfResettingBarrier startCycleBarrier;
    private SelfResettingBarrier endCycleBarrier;
    private boolean shouldStop = false;

    public SplitCyclicWorkloadWorker(Queue<Runnable> tasksQueue, SelfResettingBarrier startCycleBarrier, SelfResettingBarrier endCycleBarrier) {
        super();
        this.startCycleBarrier = startCycleBarrier;
        this.tasksQueue = tasksQueue;
        this.endCycleBarrier = endCycleBarrier;
    }
    
    public SplitCyclicWorkloadWorker(Queue<Runnable> tasksQueue, SelfResettingBarrier startCycleBarrier, SelfResettingBarrier endCycleBarrier, String threadName) {
        super(threadName);
        this.startCycleBarrier = startCycleBarrier;
        this.tasksQueue = tasksQueue;
        this.endCycleBarrier = endCycleBarrier;
    }

    @Override
    public void run() {
        super.run();

        while (!shouldStop()) {
            try {
                startCycleBarrier.waitForOthers();

                Runnable task = null;
                while ((task = tasksQueue.poll()) != null) {
                    task.run();
                }

                try {
                    endCycleBarrier.waitForOthers();
                } catch (InterruptedException e) {
                    System.err.println(
                            "WARNING:\n Worker thread interrupted while waiting on a barrier.\nThis means that it may be stopped while other worker threads are still running.");
                }
            } catch (InterruptedException e) {
                // This enables to interrupt the thread for checking if should stop
            }
        }
    }

    private synchronized boolean shouldStop() {
        return shouldStop;
    }

    public synchronized void beginStopping() {
        if (shouldStop == true) {
            System.err.println("WARNING:\nTrying to stop a thread which is already being stopped");
        }
        this.shouldStop = true;
        this.interrupt();
    }

}
