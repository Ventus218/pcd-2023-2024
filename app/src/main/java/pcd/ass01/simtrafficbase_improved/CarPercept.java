package pcd.ass01.simtrafficbase_improved;

import java.util.Optional;

import pcd.ass01.simengineseq_improved.Percept;

/**
 * 
 * Percept for Car Agents
 * 
 * - position on the road
 * - nearest car, if present (distance)
 * - nearest semaphore, if present (distance)
 * 
 */
public class CarPercept implements Percept {
    double roadPos;
    Optional<CarAgentInfo> nearestCarInFront;
    Optional<TrafficLightInfo> nearestSem;

    public double roadPos() {
        return roadPos;
    }

    public Optional<CarAgentInfo> nearestCarInFront() {
        return nearestCarInFront;
    }

    public Optional<TrafficLightInfo> nearestSem() {
        return nearestSem;
    }
}