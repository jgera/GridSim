package inz.model;

public class Car {
	
	//state
	public Lane lane;
	public double lane_pos;
	
	public LaneExit nextLane;
	public double speed; //		km/h
	
	
	
	public Node onIntersection = null;
	
	public boolean isFocused;
	
	public CarState state = CarState.normal;
	public enum CarState {
		normal,
		intersection_slowdown,
		intersection_wait,
		intersection_move	//RED
	}
}
