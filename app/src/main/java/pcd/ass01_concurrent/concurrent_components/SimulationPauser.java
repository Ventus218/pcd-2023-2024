package pcd.ass01_concurrent.concurrent_components;

public class SimulationPauser {
    private boolean isPaused = false;

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void unpause() {
        isPaused = false;
        notifyAll();
    }

    public synchronized void waitWhilePaused() throws InterruptedException {
        while (isPaused) {
            wait();
        }
    }
}
