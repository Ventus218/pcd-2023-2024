package pcd.ass01_concurrent.concurrent_components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SplitCyclicLoadMaster {
    private final CyclicWorkload cyclicWorkload = new CyclicWorkload();
    private final SelfResettingBarrier barrier;
    private final List<SplitLoadWorker> splitLoadWorkers;

    public SplitCyclicLoadMaster(Optional<Integer> expectedNumberOfTasksPerCycle, Optional<Integer> numberOfWorkers) {

        final int nWorkers;
        if (numberOfWorkers.isPresent()) {
            nWorkers = numberOfWorkers.get();
        } else {
            int processors = Runtime.getRuntime().availableProcessors();
            nWorkers = Math.min(processors, expectedNumberOfTasksPerCycle.orElse(processors));
        }

        barrier = new SelfResettingBarrier(nWorkers + 1); // Master thread will wait at the barrier too
        splitLoadWorkers = new ArrayList<>();

        for (int i = 0; i < nWorkers; i++) {
            SplitLoadWorker worker = new SplitLoadWorker(cyclicWorkload, barrier, "Worker " + i);
            splitLoadWorkers.add(worker);
            worker.start();
        }
    }

    public void executeWorkload(List<Runnable> tasks) throws InterruptedException {
        cyclicWorkload.assignWorkload(tasks);
        barrier.waitForOthers();
    }

    public void workFinished() {
        for (var worker : splitLoadWorkers) {
            worker.beginStopping();
        }
    }
}
