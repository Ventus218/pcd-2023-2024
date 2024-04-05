package pcd.ass01.simtrafficexamples_improved;

/**
 * 
 * Main class to create and run a simulation
 * 
 */
public class RunTrafficSimulation {

	public static void main(String[] args) {		

		// TrafficSimulationSingleRoadTwoCars simulation = new TrafficSimulationSingleRoadTwoCars();
		// TrafficSimulationSingleRoadSeveralCars simulation = new TrafficSimulationSingleRoadSeveralCars();
		// TrafficSimulationSingleRoadWithTrafficLightTwoCars simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
		TrafficSimulationWithCrossRoads simulation = new TrafficSimulationWithCrossRoads();
		simulation.setup();
		
		RoadSimStatistics stat = new RoadSimStatistics();
		RoadSimView view = new RoadSimView();
		view.display();
		
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);		
		simulation.run(10000);
	}
}
