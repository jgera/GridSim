package inz;

import inz.model.Car;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Car.CarState;

public class Reporter {
	
	public void perTick(StreetMap map) {
		
		int carsInSystem = 0;
		double carSpeedSum = 0;
		double averageSpeed = 0;
		int carsWaitingOnStop = 0;
		int carsMovingThroughtIntersection = 0;
		int carsWaitingToEnter = 0;
		
		for(Car c : map.cars) {
			carsInSystem += 1;
			carSpeedSum += c.speed;
			
			if(c.state == CarState.intersection_move) {
				carsMovingThroughtIntersection += 1;
			}
			
			if(c.state == CarState.intersection_wait) {
				carsWaitingOnStop += 1;
			}

		}
		
		averageSpeed = carSpeedSum / (double)carsInSystem;
		
		for(Node n : map.nodes) {
			carsWaitingToEnter += n.sourceWaitCarsCount;
		}
		
		System.out.println("average speed: " + averageSpeed);
		System.out.println("Cars waiting: " + carsWaitingToEnter);
	} 
	
}
