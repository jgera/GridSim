package inz;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataListener;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.PlotArea;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;


public class GraphWindow extends JFrame {

	private JPanel contentPane;
	Timer timer;

	/**
	 * Create the frame.
	 */
	public GraphWindow(DataTable data, String title) {
		setTitle(title);
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		XYPlot plot = new XYPlot(data);
		plot.setInsets(new Insets2D.Double(40.0));

		// Format plot area
		plot.getPlotArea().setSetting(PlotArea.BORDER, null);
		
		getContentPane().add(new InteractivePanel(plot));
		
		timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				contentPane.repaint();
			}
		});
		timer.start(); 
	}
	

}
