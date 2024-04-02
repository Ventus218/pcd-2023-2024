package pcd.ass01_concurrent.concurrent_components;

import java.util.Collection;
import java.util.Queue;
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

    public Runnable nextTask() {
        try {
            mutex.lock();
            var task = taskQueue.poll();
            if (taskQueue.isEmpty()) {
                workloadIsReady = false;
            }
            return task;
        } finally {
            mutex.unlock();
        }
    }

    public void waitForWorkload() throws InterruptedException {
        try {
            mutex.lock();
            while (!workloadIsReady) {
                condition.await();
            }
        } finally {
            mutex.unlock();
        }
    }
}
