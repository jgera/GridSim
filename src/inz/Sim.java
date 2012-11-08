package inz;

import java.util.Random;

import inz.model.Car;
import inz.model.StreetMap;

public class Sim {
	
	public static double intersectionLength = 15; // [m]
	
	public static void init(StreetMap streetMap) {
		Car testCar = new Car();
        testCar.lane = streetMap.lanes[0];
        testCar.nextLane = streetMap.lanes[0].exits.get(0);
        testCar.lane_pos = streetMap.lanes[0].real_length * 0.5;
        testCar.speed = 40; //	km/h
        streetMap.cars.add(testCar);
	}

	public static void tick(StreetMap streetMap, long timeDelta) {
		for(Car car : streetMap.cars) {
			
			double move = car.speed * 10 / 36; // przesuniecie w skali swiata [m/s]
			car.lane_pos += move * timeDelta / 1000;
			
			if (car.lane_pos > car.lane.real_length + Sim.intersectionLength) { //nastepny fragment
				car.lane.node2.intersectionTaken = false;
				makeJump(car);
				System.out.println("jmp");
			} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
				
				if (car.lane.exits.size() > 1) 
					car.lane.node2.intersectionTaken = true;
			}
			
		}
	}
	
	private static boolean isIntersectionFree(StreetMap streetMap, Car car) {
		//TODO ?!? !
		return false;
	}
	
	private static void getDistanceToObstacle(StreetMap streetMap, Car car) {
		//TODO
	}
	
	private static void makeJump(Car car) {
		car.lane = car.nextLane;
		int rnd_exit = new Random().nextInt(car.lane.exits.size());
		car.nextLane = car.lane.exits.get(rnd_exit);
		car.lane_pos = 0;
		
		
	}
}
