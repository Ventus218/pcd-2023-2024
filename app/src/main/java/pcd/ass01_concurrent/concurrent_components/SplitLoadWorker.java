package pcd.ass01_concurrent.concurrent_components;

public class SplitLoadWorker extends Thread {

    private TaskQueue taskQueue;
    private SelfResettingBarrier barrier;
    private boolean shouldStop = false;

    public SplitLoadWorker(TaskQueue taskQueue, SelfResettingBarrier barrier) {
        super();
        this.taskQueue = taskQueue;
        this.barrier = barrier;
    }
    
    public SplitLoadWorker(TaskQueue taskQueue, SelfResettingBarrier barrier, String threadName) {
        super(threadName);
        this.taskQueue = taskQueue;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        super.run();

        while (!shouldStop()) {
            Runnable task = null;
            try {
                task = taskQueue.dequeueTask();
            } catch (InterruptedException e) {
                // This enables to interrupt the thread for checking if should stop
            }
            
            if (task != null) {
                task.run();
                try {
                    barrier.waitForOthers();
                } catch (InterruptedException e) {
                    System.err.println("WARNING:\n Worker thread interrupted while waiting on a barrier.\nThis means that it may be stopped while other worker threads are still running.");
                }
            }
            
        }
    }

    private synchronized boolean shouldStop() { return shouldStop; }

    public synchronized void beginStopping() {
        if (shouldStop == true) {
            System.err.println("WARNING:\nTrying to stop a thread which is already being stopped");
        }
        this.shouldStop = true;
        this.interrupt();
    }

}
