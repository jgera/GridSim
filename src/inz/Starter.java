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
        		
                MainWindow ex = new MainWindow(streetMap);
                ex.setVisible(true);
            }
        });
	}

}
