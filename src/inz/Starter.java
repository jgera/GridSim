package inz;

import javax.swing.SwingUtilities;

public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow ex = new MainWindow();
                ex.setVisible(true);
            }
        });
	}

}
