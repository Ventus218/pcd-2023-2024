package pcd.ass01_concurrent.simtrafficbase_improved;

public class TrafficLightInfo {
    TrafficLight sem;
    Road road;
    double roadPos;

    public TrafficLightInfo(TrafficLight sem, Road road, double roadPos) {
        this.sem = sem;
        this.road = road;
        this.roadPos = roadPos;
    }

    public TrafficLight sem() {
        return sem;
    }

    public Road road() {
        return road;
    }

    public double roadPos() {
        return roadPos;
    }
}
