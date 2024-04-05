package pcd.ass01_concurrent.simtrafficbase_improved;

import pcd.ass01_concurrent.simengineseq_improved.Action;

/**
 * Car agent move forward action
 */
public class MoveForward implements Action {
    String agentId;
    double distance;

    public MoveForward(String agentId, double distance) {
        this.agentId = agentId;
        this.distance = distance;
    }

    public String agentId() {
        return agentId;
    }

    public double distance() {
        return distance;
    }
}
