package inz;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainWindow extends JFrame {

	public MainWindow() {

		DrawPanel dpnl = new DrawPanel();
		add(dpnl);

		setTitle("Simple example");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}