package inz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import inz.model.StreetMap;

import javax.swing.Timer;

public class SimRunner {
	
	boolean starting = true;
	long lastFrameTime = 0;
	
	StreetMap streetMap;
	DrawPanel drawPanel;
	
	Timer timer;
	
	public SimRunner(StreetMap sm) {
		this.streetMap = sm;
	}
	
	//50
	public void startSimulation(int delay) {
		timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				runSimulation();
			}
		});
		timer.start(); 
	}
	
	public void setDrawPanel(DrawPanel draw) {
		drawPanel = draw;
		drawPanel.setStreetMap(streetMap);
	}
	
	//TODO no window mode
	private void runSimulation() {
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
		
		if (drawPanel != null) {
			drawPanel.repaint();
		}
		
		timer.restart();
	}
	

}
