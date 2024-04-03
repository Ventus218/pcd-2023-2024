package pcd.ass01_concurrent.simtrafficexamples_improved;

import pcd.ass01_concurrent.concurrent_components.SimulationEarlyStopper;
import pcd.ass01_concurrent.concurrent_components.SimulationPauser;

/**
 * 
 * Main class to create and run a simulation
 * 
 */
public class RunTrafficSimulation {

	public static void main(String[] args) {

		// var simulation = new TrafficSimulationSingleRoadTwoCars();
		// var simulation = new TrafficSimulationSingleRoadSeveralCars();
		// var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
		var simulation = new TrafficSimulationWithCrossRoads();
		simulation.setup();

		SimulationEarlyStopper earlyStopper = new SimulationEarlyStopper();
		simulation.setEarlyStopper(earlyStopper);
		SimulationPauser pauser = new SimulationPauser();
		simulation.setPauser(pauser);

		RoadSimStatistics stat = new RoadSimStatistics();
		RoadSimView simulatorView = new RoadSimView();
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(simulatorView);

		SimulationControllerView controllerView = new SimulationControllerView(100, new SimulationControllerDelegate() {

			@Override
			public void onStartSimulation(SimulationControllerView view, int stepNumber) {
				simulatorView.display();

				Thread t = new Thread(() -> {
					simulation.run(stepNumber);
					view.disablePauseButton();
				});
				t.start();
			}

			@Override
			public void onStopSimulation(SimulationControllerView view) {
				pauser.unpause();
				earlyStopper.stopSimulation();
				simulatorView.close();
				view.close();
			}

			@Override
			public void onPauseSimulation(SimulationControllerView view) {
				pauser.pause();
			}

			@Override
			public void onUnpauseSimulation(SimulationControllerView view) {
				pauser.unpause();
			}
		});

		controllerView.display();
	}
}
