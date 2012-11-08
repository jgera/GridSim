package inz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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
		
		timer = new Timer(50, this);
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
			if (car.lane_pos > car.lane.real_length + Static.intersectionLength) { //nastepny fragment
				makeJump(car);
				System.out.println("jmp");
			} else if (car.lane_pos > car.lane.real_length) { //na zlaczeniu
				// wait
			}
		}
		dpnl.repaint();
		timer.restart();
	}
	
	private void makeJump(Car car) {
		car.lane = car.nextLane;
		int rnd_exit = new Random().nextInt(car.lane.exits.size());
		car.nextLane = car.lane.exits.get(rnd_exit);
		car.lane_pos = 0;
	}
}