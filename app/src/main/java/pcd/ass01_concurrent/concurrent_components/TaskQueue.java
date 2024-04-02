package pcd.ass01_concurrent.concurrent_components;

import java.util.List;
import java.util.ArrayList;

public class TaskQueue {

    private final List<Runnable> queue = new ArrayList<>();

    public synchronized void enqueueTask(Runnable task) {
        queue.add(task);
        notify();
    }

    public synchronized Runnable dequeueTask() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.removeLast();
    }
}
