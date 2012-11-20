package inz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import inz.model.Car;
import inz.model.Lane;
import inz.model.Node;
import inz.model.StreetMap;

public class Sim {
	
	public static void init(StreetMap streetMap) {
		for(int i = 0; i < 10; i++) {
			int rndLane = new Random().nextInt(streetMap.lanes.length);
			addCar(streetMap, streetMap.lanes[rndLane], i == 0);
		}
	}

	public static void tick(StreetMap streetMap, long timeDelta) {
		
		for(Car car : streetMap.cars) {
			
			double v0 = 70 * 10f / 36f;	//desired speed on free road		[m/s]
			double T = 5; //safe time							[s]
			double a = 3; //acceleration						[m/s^2]
			double b = 7; //breakign deceleration				[m/s^2]
			double s0 = 7; //safe distance (bumper to bumper)	[m]
			double beta = 6;  //acceleration exponent			[?]
			
			Node intersection = null;
			
			if (car.lane_pos > car.lane.real_length + car.nextLane.distance) { //nastepny fragment
				car.lane.node2.intersectionTaken = false;
				makeJump(car);
			} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
				if (car.lane.exits.size() > 1)  {
					car.lane.node2.intersectionTaken = true;
				}	
			}
			
			Obstacle obst = getDistanceToObstacle(streetMap, car); 
			
			if (obst.node != null && obst.distance < s0 + 2) {
				intersection = car.lane.node2;						//zblizamy sie do skrzyzowania
			}
			
			if (car.speed < 2 && intersection != null) {			// zblizamy sie && zwolnilismy wystarczajaco
				System.out.println("Equeuing on intersection");
				intersection.queue.addLast(car);
				car.onIntersection = intersection;					// jestesmy na skrzyzowaniu
			}
			
			double obstDistance  = obst.distance;
			
			if (car.onIntersection != null && car.onIntersection.queue.peekLast() == car) {		// na skrzyzowaniu && na poczatku listy
				System.out.println("Moving through intersection");
				obstDistance = 9999;								// HAXXX! zignoruj przeszkode
			}
			
			if ((obst.node == null || obst.node != car.onIntersection) && car.onIntersection != null) {		// przed nami auto albo inne skrzyzowanie
				System.out.println("Leaving intersection");
				car.onIntersection.queue.remove(car);
				car.onIntersection = null;
				obstDistance = 9999; //HAAXXX!
			}
			
			double vD = 0; 	// velocity difference
			if (obst.car != null) {
				vD = car.speed - obst.car.speed;
			} else {
				vD = car.speed;
			}
			vD = vD * 10f / 36f;
			
			double s = obstDistance;
			double v = car.speed * 10f / 36f; 
			double ss = s0 + (v*T + 
						(v * vD) / (2 * Math.sqrt(a*b))
					);											//desired distance
			double dv_dt = a * (
					1 -  Math.pow((v/v0), beta)
					- Math.pow((ss/s),2)
				);
			
			car.speed = car.speed + dv_dt * (timeDelta/1000f) * 3.6f;
			double move = car.speed * 10 / 36; 					// przesuniecie w skali swiata [m/s]
			car.lane_pos += move * timeDelta / 1000;
			
			if (car.isFocused) {
				System.out.println("Closest obstacle: " + Math.round(obstDistance));
				System.out.println("Speed: " + Math.round(car.speed));
				System.out.println("Desired distance: " + Math.round(ss));
			}

		}
	}
	
	private static void addCar(StreetMap streetMap, Lane lane, boolean focused) {
		Car testCar = new Car();
        testCar.lane = lane;
        testCar.nextLane = lane.exits.get(0);
        testCar.lane_pos = 0;
        testCar.isFocused = focused;
        testCar.speed = 40; //	km/h
        streetMap.cars.add(testCar);
	}
	
	
	private static Obstacle getDistanceToObstacle(StreetMap streetMap, Car car) {
		
		List<Lane> straightRoad = new ArrayList<Lane>();	// odcinek "widocznosci"
		straightRoad.add(car.lane);
		Lane l = car.lane;
		while(l.exits.size() == 1) {
			l = l.exits.get(0).lane;
			straightRoad.add(l);
		}
		
		//closest car (until intersection)
		HashMap<Double, Car> closeCars = new HashMap<Double, Car>();
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
		Car closestCar = null;
		double closestCarDistance = 1000000;
		for (Entry<Double, Car> e : closeCars.entrySet()) {			
			if (e.getKey() < closestCarDistance && e.getKey() > 0) {
				closestCarDistance = e.getKey();
				closestCar = e.getValue();
			}
		}
		
		//closest intersections
		double intersectionDistance = -1;
		for (Lane lane : straightRoad) {
			intersectionDistance += lane.real_length;
			intersectionDistance += lane.exits.get(0).distance;
		}
		Node n = straightRoad.get(straightRoad.size() - 1).node2;
		intersectionDistance -= car.lane_pos;
		
		//closest turnaround
		//TODO
		
		if (closestCarDistance == -1) {
			Obstacle o = new Obstacle();
			o.node = n;
			o.distance = intersectionDistance;
			return o;
		}
		
		if (intersectionDistance < closestCarDistance) {
			Obstacle o = new Obstacle();
			o.node = n;
			o.distance = intersectionDistance;
			return o;
		} else {
			Obstacle o = new Obstacle();
			o.car = closestCar;
			o.distance = closestCarDistance;
			return o;
		}
		
		
	}
	
	private static class Obstacle {
		Car car = null;
		Node node = null;
		double distance;
	}
	
	private static void makeJump(Car car) {
		car.lane = car.nextLane.lane;
		int rnd_exit = new Random().nextInt(car.lane.exits.size());
		car.nextLane = car.lane.exits.get(rnd_exit);
		car.lane_pos = 0;
	}
}
