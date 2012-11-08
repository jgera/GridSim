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
	
	StreetMap streetMap;
	
	public DrawPanel(StreetMap streetMap) {
		this.streetMap = streetMap;
	}
	
	public float scaleWidth(double value) {
		return (float)(value * Static.mapRenderParams.scale / MapRenderParams.def_scale);
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
	
	public void setStyleCar(Graphics2D g) {
		g.setColor(Color.black);
		BasicStroke bs1 = new BasicStroke(scaleWidth(1f));
        g.setStroke(bs1);
	}
	
	public void drawCars(Graphics2D g, StreetMap streetMap) {
		setStyleCar(g);
		for(Car car : streetMap.cars) {
			if (car.lane_pos < car.lane.real_length) {
				//na trasie
				double part = car.lane_pos / car.lane.real_length;
				int x = car.lane.x1 + (int)Math.round((car.lane.x2 - car.lane.x1) * part);
				int y = car.lane.y1 + (int)Math.round((car.lane.y2 - car.lane.y1) * part);
				g.drawOval(x-5, y-5, 10, 10);
			} else {
				
			}
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
		streetMap.lanes = lanes.toArray(new Lane[0]);
		
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
        
        
        g2d.setColor(Color.lightGray);
        BasicStroke bs1 = new BasicStroke(12f);
        g2d.setStroke(bs1);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        Dimension size = getSize();
        Insets insets = getInsets();
        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;
        
        Static.mapRenderParams = MapHelpers.prepareDataToRender(streetMap, w, h);
        System.out.println("Scale: " + Static.mapRenderParams.scale);
        MapHelpers.findLanesPositions(streetMap, Static.mapRenderParams);
        
        drawMap(g2d, streetMap);
        drawCars(g2d, streetMap);
    }
}