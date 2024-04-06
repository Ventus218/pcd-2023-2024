package pcd.ass01_concurrent.concurrent_components;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class SynchronizedQueue<T> {

    List<T> queue = new ArrayList<>();

    public SynchronizedQueue(int expectedSize) {
        this(Optional.ofNullable(expectedSize));
    }

    public SynchronizedQueue() {
        this(Optional.empty());
    }

    public SynchronizedQueue(Optional<Integer> expectedSize) {
        if (expectedSize.isPresent()) {
            queue = new ArrayList<>(expectedSize.get());
        }
    }

    public synchronized void enqueue(T element) {
        queue.add(element);
    }

    public synchronized <U extends Collection<T>> void enqueueAll(U collection) {
        queue.addAll(collection);
    }

    public synchronized Optional<T> dequeue() {
        int queueSize = queue.size();
        if (queueSize > 0) {
            return Optional.of(queue.remove(queueSize - 1));
        }
        return Optional.empty();
    }

}
