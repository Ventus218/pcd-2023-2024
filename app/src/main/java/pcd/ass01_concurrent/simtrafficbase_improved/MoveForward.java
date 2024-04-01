package pcd.ass01_concurrent.simtrafficbase_improved;

import pcd.ass01_concurrent.simengineseq_improved.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(String agentId, double distance) implements Action {}
