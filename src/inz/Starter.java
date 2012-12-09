package inz;

import inz.model.StreetMap;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Unable to load native look and feel");
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to load native look and feel");
		} catch (InstantiationException e) {
			System.out.println("Unable to load native look and feel");
		} catch (IllegalAccessException e) {
			System.out.println("Unable to load native look and feel");
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				StreetMap streetMap = MapHelpers.parseMap("data/simple.osm");

				MapHelpers.projectNodePoints(streetMap);
				MapHelpers.normaliseNodePositions(streetMap);
				MapHelpers.prepareMap(streetMap);
				MapHelpers.findLanesPositions(streetMap);
				MapHelpers.findConnectorLengths(streetMap);

				MainWindow mainWindow = new MainWindow();
				mainWindow.getMainFrame().setVisible(true);

				SimRunner simRunner = new SimRunner(streetMap);
				simRunner.setDrawPanel(mainWindow.getSimPanel());

				simRunner.startSimulation(50);
			}
		});
	}

}
