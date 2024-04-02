package pcd.ass01_concurrent.concurrent_components;

public class SplitCyclicWorkloadWorker extends Thread {

    private CyclicWorkload cyclicWorkload;
    private SelfResettingBarrier barrier;
    private boolean shouldStop = false;

    public SplitCyclicWorkloadWorker(CyclicWorkload cyclicWorkload, SelfResettingBarrier barrier) {
        super();
        this.cyclicWorkload = cyclicWorkload;
        this.barrier = barrier;
    }

    public SplitCyclicWorkloadWorker(CyclicWorkload cyclicWorkload, SelfResettingBarrier barrier, String threadName) {
        super(threadName);
        this.cyclicWorkload = cyclicWorkload;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        super.run();

        while (!shouldStop()) {
            try {
                cyclicWorkload.waitForWorkload();
                Runnable task = null;
                while ((task = cyclicWorkload.nextTask()) != null) {
                    task.run();
                }

                try {
                    barrier.waitForOthers();
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
