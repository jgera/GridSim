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
import inz.model.Car.CarState;

public class Sim {
	
	static Reporter reporter = new Reporter();
	
	public static void init(StreetMap streetMap) {
		
		List<Lane> lanesWithExits = new ArrayList<Lane>();
		for(Lane l : streetMap.lanes) {
			if (l.exits.size() > 0 ) {
				lanesWithExits.add(l);
			}
		}
		
		for(int i = 0; i < 10; i++) {
			int rndLane = new Random().nextInt(lanesWithExits.size());
			addCar("r", streetMap, lanesWithExits.get(rndLane), i == 0);
		}
	}

	public static void tick(StreetMap streetMap, long timeDelta) {
		
		List<Car> carsToBeRemoved = new ArrayList<Car>();
		
		Random rnd = new Random();
		for(Node n : streetMap.nodes) {
			if (n.exits.size() > 0) {
				if(n.sourceRatio > 0) {
					if (rnd.nextDouble() < n.sourceRatio) {
						n.sourceWaitCarsCount++;
					}
				}
				
				Obstacle obst = findClosestCar(streetMap, n.exits.get(0));
				if(n.sourceWaitCarsCount > 0 && obst.distance > 7) {
					n.sourceWaitCarsCount--;
					addCar("s", streetMap, n.exits.get(0), false);
				}
			}
		}

		for(Car car : streetMap.cars) {
			
			if (car.lane.node2.isSink == true) {
				if (car.lane_pos >= car.lane.real_length) {
					carsToBeRemoved.add(car);
					continue;
				}
			}
			
			//car.state = CarState.normal;
			
			//NORMAL
			double v0 = 70 * 10f / 36f;	//desired speed on free road		[m/s]
			double T = 3; //safe time							[s]
			double a = 3; //acceleration						[m/s^2]
			double b = 7; //breakign deceleration				[m/s^2]
			double s0 = 7; //safe distance (bumper to bumper)	[m]
			double beta = 6;  //acceleration exponent			[?]
			
			//ANGRY DRIVERS
//			v0 = 90 *  10f / 36f;
//			T = 2;
//			a = 5;
//			b = 9;
//			s0 = 7;
//			beta = 6;
			
			//na zderzaku
//			s0=4;
			
			Node intersection = null;
			
			if(car.nextLane != null) {
				if (car.lane_pos > car.lane.real_length + car.nextLane.distance) { //nastepny fragment
					car.lane.node2.intersectionTaken = false;
					makeJump(car);
				} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
					if (car.lane.exits.size() > 1)  {
						car.lane.node2.intersectionTaken = true;
					}	
				}
			}
			
			
			Obstacle obst = getDistanceToObstacle(streetMap, car); 
			
			if (obst.node != null && obst.distance < s0 + 2) {
				intersection = obst.node;						//zblizamy sie do skrzyzowania
			}
			
			if (car.speed < 2 && intersection != null && car.onIntersection == null) {			// zblizamy sie && zwolnilismy wystarczajaco
				intersection.queue.addLast(car);
				car.onIntersection = intersection;					// jestesmy na skrzyzowaniu
				car.state = CarState.intersection_wait;
//				System.out.println("[" + car.carId + "] " + "waiting");
			}
			
			double obstDistance  = obst.distance;
			
//			if (car.onIntersection != null && car.onIntersection.queue.peekFirst() == car) {		// na skrzyzowaniu && na poczatku listy
			
			// magiczne skrzyzowanie
			if (car.onIntersection != null) {		// na skrzyzowaniu && na poczatku listy
				if (car.nextLane == null || findClosestCar(streetMap, car.nextLane.lane).distance > s0) {
					car.state = CarState.intersection_move;
					car.onIntersection.intersectionTaken = true;
				} else {
					car.onIntersection.queue.remove(car);
					car.onIntersection.queue.add(car);

//					System.out.println("[" + car.carId + "] " + "waiting - NO SPACE");
				}
			}
			
			if (car.state == CarState.intersection_move) {
				obstDistance = 9999;						// HAXXX! zignoruj przeszkode
//				System.out.println("[" + car.carId + "] " + "moving intersection");
			}
			
			if (car.onIntersection != null) {
				if (obst.node == null || obst.node != car.onIntersection || (obst.node == car.onIntersection && obst.distance > s0 + 2 )) {		// przed nami auto albo inne skrzyzowanie
//					System.out.println("! za skrzyzowaniem");
					car.onIntersection.queue.remove(car);
					car.onIntersection.intersectionTaken = false;
					car.onIntersection = null;
					obstDistance = 9999; //HAAXXX!
					car.state = CarState.normal;
				}
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
			
			if(car.speed < 0 || Double.isNaN(car.speed)) {
				//FIXME log!!!
				car.speed = 0;
			}
			
// zaspany kierowca
			
//			if(car.speed > car.last_speed) {
//				double ratio = 0.5;
//				if(car.last_speed == 0) {
//					ratio = 0.8;
//				}
//				if(rnd.nextDouble() > ratio) {
//					car.speed = car.last_speed;
//				}
//				car.last_speed = car.speed;
//			}
			
			double move = car.speed * 10 / 36; 					// przesuniecie w skali swiata [m/s]
			car.lane_pos += move * (timeDelta / 1000f);
			
//			if (car.isFocused) {
//				System.out.println("    obstacle: " + Math.round(obstDistance) + "  v: " + Math.round(car.speed));
//			}

		}
		
		reporter.perTick(streetMap);
		
		for(Car c : carsToBeRemoved) {
//			System.out.println("car: " + c.carId + " exits");
		}
		streetMap.cars.removeAll(carsToBeRemoved);
		
	}
	
	private static void addCar(String prefix, StreetMap streetMap, Lane lane, boolean focused) {
		Car testCar = new Car();
		testCar.carId = Car.getNextCarId(prefix);
        testCar.lane = lane;
        testCar.nextLane = lane.exits.get(0);
        testCar.lane_pos = 0;
        testCar.isFocused = focused;
        testCar.speed = 40; //	km/h
        streetMap.cars.add(testCar);
	}
	
	private static Obstacle findClosestCar(StreetMap streetMap, Lane lane) {
		
		List<Lane> straightRoad = new ArrayList<Lane>();	// odcinek "widocznosci"
		straightRoad.add(lane);
		Lane l = lane;
		while(l.exits.size() == 1) {
			l = l.exits.get(0).lane;
			straightRoad.add(l);
		}
		
		//closest car (until intersection)
		HashMap<Double, Car> closeCars = new HashMap<Double, Car>();
		for (Car c : streetMap.cars) {
			
			if (straightRoad.contains(c.lane)) {
				double distance = 0;
				for (Lane lane2 : straightRoad) {
					if (lane2 == c.lane) {
						distance += c.lane_pos;	
						break;
					} else {
						distance += lane2.real_length;
						distance += lane2.exits.get(0).distance;
					}
				}
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
		
		Obstacle o = new Obstacle();
		o.car = closestCar;
		o.distance = closestCarDistance;
		return o;
		
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
			if(lane.exits.size() > 0) {
				intersectionDistance += lane.real_length;
				intersectionDistance += lane.exits.get(0).distance;
			} else {
				intersectionDistance = 1000000;
			}
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
		car.lane_pos = 0;

		if (car.lane.exits.size() == 0) {
			car.nextLane = null;
		} else {
			int rnd_exit = new Random().nextInt(car.lane.exits.size());
			car.nextLane = car.lane.exits.get(rnd_exit);
		}
	}
}
