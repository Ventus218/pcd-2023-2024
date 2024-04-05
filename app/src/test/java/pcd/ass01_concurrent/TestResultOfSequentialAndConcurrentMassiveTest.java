package pcd.ass01_concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import pcd.ass01_concurrent.simtrafficexamples_improved.RoadSimStatistics;
import pcd.ass01_concurrent.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars;

public class TestResultOfSequentialAndConcurrentMassiveTest {
    private static final int NUMBER_OF_CARS = 5000;
    private static final int NUMBER_OF_STEPS = 100;

    @Test void test() {
		
		pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars sequentialSimulation = new pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS);
		sequentialSimulation.setup();
        sequentialSimulation.addSimulationListener(new pcd.ass01.simtrafficexamples_improved.RoadSimStatistics());

        ByteArrayOutputStream sequentialOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sequentialOutput));
		
        sequentialSimulation.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));


        TrafficSimulationSingleRoadMassiveNumberOfCars concurrentSimulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS);
		concurrentSimulation.setup();
        concurrentSimulation.addSimulationListener(new RoadSimStatistics());

        ByteArrayOutputStream concurrentOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(concurrentOutput));
		
        concurrentSimulation.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        
        assertEquals(sequentialOutput.toString(), concurrentOutput.toString());
    }
}
