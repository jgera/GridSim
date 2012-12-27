package inz.model;

public class Car {
	
	//state
	public Lane lane;
	public double lane_pos;
	
	public LaneExit nextLane;
	public double speed; //		km/h
	public double last_speed = 0;
	
	public String carId = "";
	
	public Node onIntersection = null;
	
	public boolean isFocused;
	
	public CarState state = CarState.normal;
	public enum CarState {
		normal,
		intersection_slowdown,
		intersection_wait,
		intersection_move	//RED
	}
	
	
	private static volatile int carNo = 0;
	public static synchronized String getNextCarId(String prefix) {
		carNo++;
		return prefix + carNo;
	}
	
}
