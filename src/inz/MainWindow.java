package inz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;

import javax.swing.AbstractButton;
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
import javax.swing.text.Document;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import com.camick.LimitLinesDocumentListener;
import com.camick.MessageConsole;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import de.erichseifert.gral.data.DataTable;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame mainFrame;
	private DrawPanel simPanel;
	private MessageConsole mc;
	
	private GraphWindow averageSpeedWindow;
	private GraphWindow systemWaitWindow;
	private GraphWindow carsInSystem;

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
		
		averageSpeedWindow = new GraphWindow(Reporter.averageSpeedTable, "average speed");
		systemWaitWindow = new GraphWindow(Reporter.carsWaitingTable, "input queue");
		carsInSystem = new GraphWindow(Reporter.averageSpeed_carsInSystem, "cars in system");
		
		mainFrame = new JFrame();
		mainFrame.setBounds(100, 100, 1000, 800);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTextPane consolePane = new JTextPane();
		JScrollPane consoleScroll = new JScrollPane(consolePane);
		
		mc = new MessageConsole(consolePane);
//		mc.redirectOut();
//		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(6);

		mainFrame.getContentPane().add(consoleScroll, BorderLayout.SOUTH);
		
		
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
		tglbtnNewToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        averageSpeedWindow.setVisible(selected);
			}
		});
		menuPanel.add(tglbtnNewToggleButton, "1, 4, fill, center");
		
		JToggleButton tglbtnNewToggleButton_1 = new JToggleButton("System input wait");
		tglbtnNewToggleButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        systemWaitWindow.setVisible(selected);
			}
		});
		menuPanel.add(tglbtnNewToggleButton_1, "1, 6, fill, center");
		
		JToggleButton tglbtnNewToggleButton_2 = new JToggleButton("Cars in system");
		tglbtnNewToggleButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
		        boolean selected = abstractButton.getModel().isSelected();
		        carsInSystem.setVisible(selected);
			}
		});
		menuPanel.add(tglbtnNewToggleButton_2, "1, 8, fill, center");
		
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
