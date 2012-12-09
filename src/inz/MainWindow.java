package inz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Button;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import java.awt.Font;

public class MainWindow {

	private JFrame mainFrame;
	private DrawPanel simPanel;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		mainFrame = new JFrame();
		mainFrame.setBounds(100, 100, 489, 340);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTextPane consolePane = new JTextPane();
		consolePane.setText(" \r\n \r\n \r\n ");
		mainFrame.getContentPane().add(consolePane, BorderLayout.SOUTH);
		
		
		simPanel = new DrawPanel();
		mainFrame.getContentPane().add(simPanel, BorderLayout.CENTER);
		
		JPanel menuPanel = new JPanel();
		mainFrame.getContentPane().add(menuPanel, BorderLayout.EAST);
		menuPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("121px"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblMenu = new JLabel("Menu");
		lblMenu.setFont(new Font("Tahoma", Font.BOLD, 11));
		menuPanel.add(lblMenu, "1, 2, center, center");
		
		JToggleButton tglbtnNewToggleButton = new JToggleButton("Average speed");
		menuPanel.add(tglbtnNewToggleButton, "1, 4, fill, center");
		
		JToggleButton tglbtnNewToggleButton_1 = new JToggleButton("System input wait");
		menuPanel.add(tglbtnNewToggleButton_1, "1, 6, fill, center");
		
		JToggleButton tglbtnCarsDetails = new JToggleButton("Cars details");
		menuPanel.add(tglbtnCarsDetails, "1, 8");
		
		JToggleButton tglbtnNodeDetails = new JToggleButton("Node details");
		menuPanel.add(tglbtnNodeDetails, "1, 10");
		
		JLabel lblSimulation = new JLabel("Simulation time");
		lblSimulation.setFont(new Font("Tahoma", Font.BOLD, 11));
		menuPanel.add(lblSimulation, "1, 12, center, default");
		
		JLabel lblTime = new JLabel("00:00:00");
		menuPanel.add(lblTime, "1, 14, center, default");
	}

	public DrawPanel getSimPanel() {
		return simPanel;
	}
	public JFrame getMainFrame() {
		return mainFrame;
	}
}
