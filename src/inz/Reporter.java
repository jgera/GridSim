package inz;

import java.util.ArrayList;
import java.util.List;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.util.Orientation;

import inz.model.Car;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Car.CarState;

public class Reporter {
	
	public static DataTable averageSpeedTable = new DataTable(Double.class, Double.class);
	public static DataTable carsWaitingTable = new DataTable(Long.class, Long.class);
	
	public static DataTable averageSpeed_carsInSystem = new DataTable(Long.class, Double.class);
	public static DataTable systemOutputTable = new DataTable(Long.class, Long.class);
	
	public static long simStartTime = 0;
	public static boolean firstMark = false;
	public static boolean secondMark = false;
	public static boolean thirdMark = false;
	
	static {
		averageSpeedTable.add((double)0,(double)0);
		carsWaitingTable.add((long)0,(long)0);
		averageSpeed_carsInSystem.add((long)10, (double)0);
	}
	
	public void perTick(StreetMap map) {
		
		if (simStartTime== 0) {
			simStartTime = System.currentTimeMillis();
		}
		
		int carsInSystem = 0;
		double carSpeedSum = 0;
		double averageSpeed = 0;
		int carsWaitingOnStop = 0;
		int carsMovingThroughtIntersection = 0;
		long carsWaitingToEnter = 0;
		
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
		
		
		long time = System.currentTimeMillis()-simStartTime;
		if (time >= 1 * 60 * 1000 && !firstMark) {
			firstMark = true;
			System.out.println("mark at " + time);
			double med = averageSpeedTable.getStatistics().get(Statistics.MEDIAN, Orientation.VERTICAL, 1);
			double variance = averageSpeedTable.getStatistics().get(Statistics.VARIANCE, Orientation.VERTICAL, 1);
			double mean = averageSpeedTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			double cars_mean = carsWaitingTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			System.out.println("speed median: " + med);
			System.out.println("speed variance: " + variance);
			System.out.println("speed mean: " + mean);
			System.out.println("cars wainting mean: " + cars_mean);
		}
		if (time >= 3 * 60 * 1000 && !secondMark) {
			secondMark = true;
			System.out.println("mark at " + time);
			double med = averageSpeedTable.getStatistics().get(Statistics.MEDIAN, Orientation.VERTICAL, 1);
			double variance = averageSpeedTable.getStatistics().get(Statistics.VARIANCE, Orientation.VERTICAL, 1);
			double mean = averageSpeedTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			double cars_mean = carsWaitingTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			System.out.println("speed median: " + med);
			System.out.println("speed variance: " + variance);
			System.out.println("speed mean: " + mean);
			System.out.println("cars wainting mean: " + cars_mean);
		}
		if (time >= 6 * 60 * 1000 && !thirdMark) {
			thirdMark = true;
			System.out.println("mark at " + time);
			double med = averageSpeedTable.getStatistics().get(Statistics.MEDIAN, Orientation.VERTICAL, 1);
			double variance = averageSpeedTable.getStatistics().get(Statistics.VARIANCE, Orientation.VERTICAL, 1);
			double mean = averageSpeedTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			double cars_mean = carsWaitingTable.getStatistics().get(Statistics.MEAN, Orientation.VERTICAL, 1);
			System.out.println("speed median: " + med);
			System.out.println("speed variance: " + variance);
			System.out.println("speed mean: " + mean);
			System.out.println("cars wainting mean: " + cars_mean);
		}
		
		carsWaitingTable.add((time)/1000, carsWaitingToEnter);
		averageSpeedTable.add((double)(time)/1000, averageSpeed);
		averageSpeed_carsInSystem.add((long)map.cars.size(), averageSpeed);
//		System.out.println("average speed: " + averageSpeed);
//		System.out.println("Cars waiting: " + carsWaitingToEnter);
	} 
	
}
