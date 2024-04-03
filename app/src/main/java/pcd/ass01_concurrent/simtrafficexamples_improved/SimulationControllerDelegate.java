package pcd.ass01_concurrent.simtrafficexamples_improved;

public interface SimulationControllerDelegate {
    void onStartSimulation(SimulationControllerView view, int stepNumber);
    void onStopSimulation(SimulationControllerView view);
    void onPauseSimulation(SimulationControllerView view);
    void onUnpauseSimulation(SimulationControllerView view);
}
