package inz;

import inz.model.StreetMap;

import javax.swing.SwingUtilities;

public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	StreetMap streetMap = MapHelpers.parseMap("data/simple.osm");
        		
        		MapHelpers.projectNodePoints(streetMap);
        		MapHelpers.normaliseNodePositions(streetMap);
        		MapHelpers.prepareMap(streetMap);
        		MapHelpers.findLanesPositions(streetMap);
                MapHelpers.findConnectorLengths(streetMap);
                
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                
                SimRunner simRunner = new SimRunner(streetMap);
        		simRunner.setDrawPanel(mainWindow.drawPanel);
        		
        		simRunner.startSimulation(50);
            }
        });
	}

}
