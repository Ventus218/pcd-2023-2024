package pcd.ass01_concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;

import pcd.ass01_concurrent.simtrafficexamples_improved.RoadSimStatistics;
import pcd.ass01_concurrent.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars;

public class TestResultOfSequentialAndConcurrentMassiveTest {
    private static final int NUMBER_OF_CARS = 5000;
    private static final int NUMBER_OF_STEPS = 100;

    @Test
    void testConcurrentResultsAreSameOfSequential() {

        pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars sequentialSimulation = makeAndSetupSequentialSimulationWithStatistics();
        ByteArrayOutputStream sequentialOutput = captureStdOutOf(() -> sequentialSimulation.run(NUMBER_OF_STEPS));

        TrafficSimulationSingleRoadMassiveNumberOfCars concurrentSimulation = makeAndSetupConcurrentSimulationWithStatistics(
                Optional.empty());
        ByteArrayOutputStream concurrentOutput = captureStdOutOf(() -> concurrentSimulation.run(NUMBER_OF_STEPS));

        assertEquals(sequentialOutput.toString(), concurrentOutput.toString());
    }

    @Test
    void testDifferentRandomSeedsGiveDifferentResults() {
        TrafficSimulationSingleRoadMassiveNumberOfCars s1 = makeAndSetupConcurrentSimulationWithStatistics(
                Optional.of(1234));
        ByteArrayOutputStream o1 = captureStdOutOf(() -> s1.run(NUMBER_OF_STEPS));

        TrafficSimulationSingleRoadMassiveNumberOfCars s2 = makeAndSetupConcurrentSimulationWithStatistics(
                Optional.of(5678));
        ByteArrayOutputStream o2 = captureStdOutOf(() -> s2.run(NUMBER_OF_STEPS));

        assertNotEquals(o1.toString(), o2.toString());
    }

    @Test
    void testSameRandomSeedsGiveSameResults() {
        TrafficSimulationSingleRoadMassiveNumberOfCars s1 = makeAndSetupConcurrentSimulationWithStatistics(
                Optional.of(1234));
        ByteArrayOutputStream o1 = captureStdOutOf(() -> s1.run(NUMBER_OF_STEPS));

        TrafficSimulationSingleRoadMassiveNumberOfCars s2 = makeAndSetupConcurrentSimulationWithStatistics(
                Optional.of(1234));
        ByteArrayOutputStream o2 = captureStdOutOf(() -> s2.run(NUMBER_OF_STEPS));

        assertEquals(o1.toString(), o2.toString());
    }

    private ByteArrayOutputStream captureStdOutOf(Runnable runnable) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        runnable.run();

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        return output;
    }

    private TrafficSimulationSingleRoadMassiveNumberOfCars makeAndSetupConcurrentSimulationWithStatistics(
            Optional<Integer> randomSeed) {
        TrafficSimulationSingleRoadMassiveNumberOfCars concurrentSimulation = randomSeed.isPresent()
                ? new TrafficSimulationSingleRoadMassiveNumberOfCars(
                        NUMBER_OF_CARS, new Random(randomSeed.get()))
                : new TrafficSimulationSingleRoadMassiveNumberOfCars(
                        NUMBER_OF_CARS);
        concurrentSimulation.setup();
        concurrentSimulation.addSimulationListener(new RoadSimStatistics());
        return concurrentSimulation;
    }

    private pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars makeAndSetupSequentialSimulationWithStatistics() {
        pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars sequentialSimulation = new pcd.ass01.simtrafficexamples_improved.TrafficSimulationSingleRoadMassiveNumberOfCars(
                NUMBER_OF_CARS);
        sequentialSimulation.setup();
        sequentialSimulation.addSimulationListener(new pcd.ass01.simtrafficexamples_improved.RoadSimStatistics());
        return sequentialSimulation;
    }
}
