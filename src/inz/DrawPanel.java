package inz;

import inz.MapHelpers.MapRenderParams;
import inz.model.Car;
import inz.model.Lane;
import inz.model.LaneExit;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Way;
import inz.model.Car.CarState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

public class DrawPanel extends JPanel {
	
	private static final long serialVersionUID = 3026058640922011855L;
	StreetMap streetMap;
	
	public DrawPanel() {
	}
	
	public void setStreetMap(StreetMap streetMap) {
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
	
	public void setStyleLane2(Graphics2D g) {
		g.setColor(Color.darkGray);
		BasicStroke bs1 = new BasicStroke(scaleWidth(0.5f));
        g.setStroke(bs1);
	}
	
	public void setStyleCar(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(9f));
		BasicStroke bs1 = new BasicStroke(scaleWidth(1f));
        g.setStroke(bs1);
	}
	
	public void drawCars(Graphics2D g, StreetMap streetMap) {
		
		for(Car car : streetMap.cars) {
			setStyleCar(g);
			if (car.state == CarState.intersection_move) {
				g.setColor(Color.green);
			} else if (car.state == CarState.intersection_wait) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);
			}
			int x,y;
			if (car.lane_pos < car.lane.real_length) {
				//na trasie
				double part = car.lane_pos / car.lane.real_length;
				x = car.lane.x1 + (int)Math.round((car.lane.x2 - car.lane.x1) * part);
				y = car.lane.y1 + (int)Math.round((car.lane.y2 - car.lane.y1) * part);
				
				g.drawOval(x-5, y-5, 10, 10);
				g.setColor(Color.black);
				g.drawString(car.carId, x-5, y-5);	//nazwy
			} else {
				if (car.nextLane != null) {
					double part = (car.lane_pos - car.lane.real_length) / car.nextLane.distance;
					x = car.lane.x2 + (int)Math.round((car.nextLane.lane.x1 - car.lane.x2) * part);		//FIXME .lane. sugeruje ze odl tez sie liczy
					y = car.lane.y2 + (int)Math.round((car.nextLane.lane.y1 - car.lane.y2) * part);
					
					g.drawOval(x-5, y-5, 10, 10);
					g.setColor(Color.black);
					g.drawString(car.carId, x-5, y-5);	//nazwy
				}
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
		
		for (Lane l : streetMap.lanes) {
			setStyleLane(g);
			g.drawLine(l.x1, l.y1, l.x2, l.y2);
			for (LaneExit e : l.exits) {
				setStyleLane2(g);
				if (e.lane.node1.intersectionTaken) {
					g.setColor(Color.white);
				}
				g.drawLine(l.x2, l.y2, e.lane.x1, e.lane.y1);
			}
		}
		
		//podpisy
		g.setColor(Color.gray);
		for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		//g.drawString(Long.toString(n.id), n.x, n.y);
        	}
        }
		
	}
	
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (streetMap == null)
        	return;
        
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
        
        drawMap(g2d, streetMap);
        drawCars(g2d, streetMap);
    }
}