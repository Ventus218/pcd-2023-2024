package pcd.ass01_concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;

import org.junit.jupiter.api.Test;

import pcd.ass01_concurrent.simtrafficexamples_improved.RoadSimStatistics;
import pcd.ass01_concurrent.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars;

public class TestResultOfSequentialAndConcurrentMassiveTest {
    private static final int NUMBER_OF_CARS = 5000;
    private static final int NUMBER_OF_STEPS = 100;

    @Test void testConcurrentResultAreSameOfSequential() {
		
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

    @Test void testDifferentRandomSeedsGiveDifferentResults() {
		TrafficSimulationSingleRoadMassiveNumberOfCars s1 = new TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS, new Random(1234));
		s1.setup();
        s1.addSimulationListener(new RoadSimStatistics());

        ByteArrayOutputStream s1Output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(s1Output));
		
        s1.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));


        TrafficSimulationSingleRoadMassiveNumberOfCars s2 = new TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS, new Random(5678));
		s2.setup();
        s2.addSimulationListener(new RoadSimStatistics());

        ByteArrayOutputStream s2Output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(s2Output));
		
        s2.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));


        assertNotEquals(s1Output.toString(), s2Output.toString());
    }

    @Test void testSameRandomSeedsGiveSameResults() {
		TrafficSimulationSingleRoadMassiveNumberOfCars s1 = new TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS, new Random(1234));
		s1.setup();
        s1.addSimulationListener(new RoadSimStatistics());

        ByteArrayOutputStream s1Output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(s1Output));
		
        s1.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));


        TrafficSimulationSingleRoadMassiveNumberOfCars s2 = new TrafficSimulationSingleRoadMassiveNumberOfCars(NUMBER_OF_CARS, new Random(1234));
		s2.setup();
        s2.addSimulationListener(new RoadSimStatistics());

        ByteArrayOutputStream s2Output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(s2Output));
		
        s2.run(NUMBER_OF_STEPS);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        
        assertEquals(s1Output.toString(), s2Output.toString());
    }
}
