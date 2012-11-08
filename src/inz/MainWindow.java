package inz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import inz.model.Car;
import inz.model.StreetMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainWindow extends JFrame implements ActionListener {
	
	StreetMap streetMap;
	DrawPanel dpnl;
	Timer timer;
	
	
	public MainWindow() {
		
		streetMap = MapHelpers.parseMap("data/simple.osm");
		MapHelpers.projectNodePoints(streetMap);
		MapHelpers.normaliseNodePositions(streetMap);
		MapHelpers.prepareMap(streetMap);

		dpnl = new DrawPanel(streetMap);
		add(dpnl);

		setTitle("Simple example");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		timer = new Timer(10000, this);
		timer.setInitialDelay(10);
		timer.start(); 
	}
	
	boolean starting = true;
	long lastFrameTime = 0;
	
	@Override
	//tu siedzi symulacja
	public void actionPerformed(ActionEvent e) {
		
		if (lastFrameTime == 0) {
			lastFrameTime = System.currentTimeMillis();
			return;
		}
		
		long time = System.currentTimeMillis();
		long timeDelta = time - lastFrameTime; 
		lastFrameTime = time;
		
		if (starting) {
			Car testCar = new Car();
	        testCar.lane = streetMap.lanes[0];
	        testCar.nextLane = streetMap.lanes[0].exits.get(0);
	        testCar.lane_pos = streetMap.lanes[0].real_length * 0.5;
	        testCar.speed = 40; //	km/h
	        streetMap.cars.add(testCar);
	        starting = false;
		}
		
		for(Car car : streetMap.cars) {
			double move = car.speed * 10 / 36; // przesuniecie w skali swiata [m/s]
			car.lane_pos += move * timeDelta / 1000;
			double connect_length = 20; // m
			
			if (car.lane_pos > car.lane.real_length + connect_length) { //nastepny fragment
				car.lane = car.nextLane;
				car.nextLane = car.lane.exits.get(0);
				car.lane_pos = 0;
				System.out.println("jmp");
			} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
				System.out.println("conn");
			}
		}
		dpnl.repaint();
		timer.restart();
	}
}