package pcd.ass01_concurrent.concurrent_components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SplitCyclicWorkloadMaster {
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private final SelfResettingBarrier startCycleBarrier;
    private final SelfResettingBarrier endCycleBarrier;
    private final List<SplitCyclicWorkloadWorker> splitLoadWorkers;

    public SplitCyclicWorkloadMaster(Optional<Integer> expectedNumberOfTasksPerCycle, Optional<Integer> numberOfWorkers) {

        final int nWorkers;
        if (numberOfWorkers.isPresent()) {
            nWorkers = numberOfWorkers.get();
        } else {
            int processors = Runtime.getRuntime().availableProcessors();
            nWorkers = Math.min(processors, expectedNumberOfTasksPerCycle.orElse(processors));
        }

        startCycleBarrier = new SelfResettingBarrier(nWorkers + 1); // Master thread will wait at the barrier too
        endCycleBarrier = new SelfResettingBarrier(nWorkers + 1); // Master thread will wait at the barrier too
        splitLoadWorkers = new ArrayList<>();

        for (int i = 0; i < nWorkers; i++) {
            SplitCyclicWorkloadWorker worker = new SplitCyclicWorkloadWorker(taskQueue, startCycleBarrier, endCycleBarrier, "Worker " + i);
            splitLoadWorkers.add(worker);
            worker.start();
        }
    }

    public void executeWorkload(List<Runnable> tasks) throws InterruptedException {
        taskQueue.addAll(tasks);
        startCycleBarrier.waitForOthers();
        endCycleBarrier.waitForOthers();
    }

    public void workFinished() {
        for (SplitCyclicWorkloadWorker worker : splitLoadWorkers) {
            worker.beginStopping();
        }
        for (SplitCyclicWorkloadWorker worker : splitLoadWorkers) {
            try {
                worker.join();
            } catch (InterruptedException e) { }
        }
    }
}
