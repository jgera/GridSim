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
	public void actionPerformed(ActionEvent e) {
		
		if (lastFrameTime == 0) {
			lastFrameTime = System.currentTimeMillis();
			return;
		}
		
		long time = System.currentTimeMillis();
		long timeDelta = time - lastFrameTime; 
		lastFrameTime = time;
		
		if (starting) {
			Sim.init(streetMap);
			starting = false;
		}
		
		Sim.tick(streetMap, timeDelta);
		
		dpnl.repaint();
		timer.restart();
	}
	
	
}