package pcd.ass01_concurrent.simtrafficexamples_improved;

import java.util.Optional;
import java.util.Random;
import pcd.ass01_concurrent.simengineseq_improved.*;
import pcd.ass01_concurrent.simtrafficbase_improved.*;

/**
 * 
 * Traffic Simulation about a number of cars 
 * moving on a single road, no traffic lights
 * 
 */
public class TrafficSimulationSingleRoadSeveralCars extends AbstractSimulation {

	public TrafficSimulationSingleRoadSeveralCars() {
		super();
	}

	public TrafficSimulationSingleRoadSeveralCars(Random random) {
		super(Optional.ofNullable(random));
	}
	
	public void setup() {

		this.setupTimings(0, 1);

		RoadsEnv env = new RoadsEnv();
		this.setupEnvironment(env);
		
		Road road = env.createRoad(new P2d(0,300), new P2d(1500,300));

		int nCars = 30;

		for (int i = 0; i < nCars; i++) {
			
			String carId = "car-" + i;
			// double initialPos = i*30;
			double initialPos = i*10;
			
			double carAcceleration = 1; //  + gen.nextDouble()/2;
			double carDeceleration = 0.3; //  + gen.nextDouble()/2;
			double carMaxSpeed = 7; // 4 + gen.nextDouble();
						
			CarAgent car = new CarAgentBasic(carId, env, 
									road,
									initialPos, 
									carAcceleration, 
									carDeceleration,
									carMaxSpeed,
									newRandomGenerator());
			this.addAgent(car);
		}
		
		this.syncWithTime(25);
	}	
}
	