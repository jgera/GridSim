package inz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import inz.model.StreetMap;

import javax.swing.JFrame;
import javax.swing.Timer;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = 3823711528095863438L;

	public DrawPanel drawPanel;
	
	public MainWindow() {

		drawPanel = new DrawPanel();
		add(drawPanel);

		setTitle("Simple example");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}