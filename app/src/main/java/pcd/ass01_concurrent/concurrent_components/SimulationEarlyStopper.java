package pcd.ass01_concurrent.concurrent_components;

public class SimulationEarlyStopper {

    private boolean shouldStop = false;

    public synchronized void stopSimulation() {
        shouldStop = true;
    }

    public synchronized boolean shouldStopSimulation() {
        return shouldStop;
    }
}
