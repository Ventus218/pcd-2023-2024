package pcd.ass01_concurrent.concurrent_components;

import java.util.Collection;
import java.util.Queue;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicWorkload {
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private final Lock mutex = new ReentrantLock(true);
    private final Condition condition = mutex.newCondition();
    private boolean workloadIsReady = false;

    public <T extends Collection<Runnable>> void assignWorkload(T workload) {
        try {
            mutex.lock();
            taskQueue.addAll(workload);
            workloadIsReady = true;
            condition.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    public Optional<Runnable> nextTask() throws InterruptedException {
        try {
            mutex.lock();
            var task = taskQueue.poll();
            if (taskQueue.isEmpty()) {
                workloadIsReady = false;
            }
            return Optional.ofNullable(task);
        } finally {
            mutex.unlock();
        }
    }

    public void waitForWorkload() throws InterruptedException {
        try {
            mutex.lock();
            // While on await is not needed in this specific case
            // as we want the thread to skip this cycle if he
            // wasn't able to get any task
            if (!workloadIsReady) {
                condition.await();
            }
        } finally {
            mutex.unlock();
        }
    }
}
