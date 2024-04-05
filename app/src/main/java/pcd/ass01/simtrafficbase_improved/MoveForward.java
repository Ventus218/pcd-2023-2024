package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineseq_improved.Action;

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
