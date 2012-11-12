package inz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import inz.model.Car;
import inz.model.Lane;
import inz.model.StreetMap;

public class Sim {
	
	public static void init(StreetMap streetMap) {
		for(int i = 0; i < 1; i++) {
			int rndLane = new Random().nextInt(streetMap.lanes.length);
			addCar(streetMap, streetMap.lanes[rndLane]);
		}
	}

	public static void tick(StreetMap streetMap, long timeDelta) {
		for(Car car : streetMap.cars) {
			
			double move = car.speed * 10 / 36; // przesuniecie w skali swiata [m/s]
			car.lane_pos += move * timeDelta / 1000;
			
			if (car.lane_pos > car.lane.real_length + car.nextLane.distance) { //nastepny fragment
				car.lane.node2.intersectionTaken = false;
				makeJump(car);
			} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
				if (car.lane.exits.size() > 1) 
					car.lane.node2.intersectionTaken = true;
			}
			
			//System.out.println("Closest obstacle: " + getDistanceToObstacle(streetMap, car));
			
		}
	}
	
	private static void addCar(StreetMap streetMap, Lane lane) {
		Car testCar = new Car();
        testCar.lane = lane;
        testCar.nextLane = lane.exits.get(0);
        testCar.lane_pos = 0;
        testCar.speed = 40; //	km/h
        streetMap.cars.add(testCar);
	}
	
	
	private static double getDistanceToObstacle(StreetMap streetMap, Car car) {
		
		List<Lane> straightRoad = new ArrayList<>();	// odcinek "widocznosci"
		straightRoad.add(car.lane);
		Lane l = car.lane;
		while(l.exits.size() == 1) {
			l = l.exits.get(0).lane;
			straightRoad.add(l);
		}
		
		//closest car (until intersection)
		HashMap<Double, Car> closeCars = new HashMap<>();
		for (Car c : streetMap.cars) {
			if (c == car) 
				continue;
			
			if (straightRoad.contains(c.lane)) {
				double distance = 0;
				for (Lane lane : straightRoad) {
					if (lane == c.lane) {
						distance += c.lane_pos;	
						break;
					} else {
						distance += lane.real_length;
						distance += lane.exits.get(0).distance;
					}
				}
				distance -= car.lane_pos;
				closeCars.put(distance, c);
			}
		}
		double closestCarDistance = -1;
		for (Entry<Double, Car> e : closeCars.entrySet()) {
			if (closestCarDistance == -1)
				closestCarDistance = e.getKey();
			
			if (e.getKey() < closestCarDistance) {
				closestCarDistance = e.getKey();
			}
		}
		
		//closest intersections
		double intersectionDistance = -1;
		for (Lane lane : straightRoad) {
			intersectionDistance += lane.real_length;
			intersectionDistance += lane.exits.get(0).distance;
		}
		intersectionDistance -= car.lane_pos;
		
		//closest turnaround
		//TODO
		if (closestCarDistance == -1) {
			return intersectionDistance;
		}
		return intersectionDistance < closestCarDistance ? intersectionDistance : closestCarDistance;
	}
	
	private static void makeJump(Car car) {
		System.out.println("jmp");
		car.lane = car.nextLane.lane;
		int rnd_exit = new Random().nextInt(car.lane.exits.size());
		car.nextLane = car.lane.exits.get(rnd_exit);
		car.lane_pos = 0;
		
		
	}
}
