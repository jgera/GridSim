package inz;

import inz.MapHelpers.MapRenderParams;
import inz.model.Car;
import inz.model.Lane;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Way;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JPanel;

import com.jhlabs.map.proj.MercatorProjection;

public class DrawPanel extends JPanel {
	
	
	final double def_scale = 7680072;
	double scale = def_scale;
	
	public float scaleWidth(double value) {
		return (float)(value * scale / def_scale);
	}
	
	public void setStyleCenterline(Graphics2D g) {
		g.setColor(Color.lightGray);
		BasicStroke bs1 = new BasicStroke(scaleWidth(18f));
        g.setStroke(bs1);
	}
	
	public void setStyleCarriageway(Graphics2D g) {
		g.setColor(Color.white);
		BasicStroke bs1 = new BasicStroke(scaleWidth(1f));
        g.setStroke(bs1);
	}
	
	public void setStyleLane(Graphics2D g) {
		g.setColor(Color.darkGray);
		BasicStroke bs1 = new BasicStroke(scaleWidth(0.5f));
        g.setStroke(bs1);
	}
	
	public void drawCars(Graphics2D g, StreetMap streetMap) {
		for(Car car : streetMap.cars) {
			
		}
	}

	public void drawMap(Graphics2D g, StreetMap streetMap) {
		setStyleCenterline(g);
        for (Way way : streetMap.ways) {
        	int last_x = 0;
        	int last_y = 0;
        	for (Node n : way.nodes) {
        		if (n == way.nodes[0]) {
        			last_x = n.x;
        			last_y = n.y;
        		} else {
            		int x = n.x;
                	int y = n.y;
                    g.drawLine(last_x, last_y, x, y);
                    last_x = x;
                    last_y = y;
        		}
        	}
        }

        setStyleCarriageway(g);
        for (Way way : streetMap.ways) {
        	int last_x = 0;
        	int last_y = 0;
        	for (Node n : way.nodes) {
        		if (n == way.nodes[0]) {
        			last_x = n.x;
        			last_y = n.y;
        		} else {
            		int x = n.x;
                	int y = n.y;
                    g.drawLine(last_x, last_y, x, y);
                    last_x = x;
                    last_y = y;
        		}
        	}
        }
        
        setStyleLane(g);
        Set<Lane> lanes = new HashSet<Lane>();
		for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		lanes.addAll(n.enters);
        		lanes.addAll(n.exits);
        	}
		}
		streetMap.lanes = (Lane[])lanes.toArray();
		
		for (Lane l : lanes) {
			g.drawLine(l.x1, l.y1, l.x2, l.y2);
			for (Lane e : l.exits) {
				g.drawLine(l.x2, l.y2, e.x1, e.y1);
			}
		}
		
		g.setColor(Color.red);
		for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		g.drawString(Long.toString(n.id), n.x, n.y);
        	}
        }
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        StreetMap streetMap = OsmParser.parseMap("data/simple.osm");
        OsmParser.prepareMap(streetMap);
        
        g2d.setColor(Color.lightGray);
        BasicStroke bs1 = new BasicStroke(12f);
        g2d.setStroke(bs1);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        Dimension size = getSize();
        Insets insets = getInsets();
        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;
        
        MapRenderParams rpar = MapHelpers.prepareDataToRender(streetMap, w, h);
        this.scale = rpar.scale;
        
        MapHelpers.findLanesPositions(streetMap, rpar);
        
        drawMap(g2d, streetMap);
        
        Car testCar = new Car();
        testCar.lane = streetMap.lanes[0];
        testCar.nextLane = streetMap.lanes[0].exits.get(0);
        testCar.lane_pos = 0;
        streetMap.cars.add(testCar);
        
        drawCars(g2d, streetMap);
    }
}