package inz.model;

import inz.MapHelpers.MapRenderParams;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.jhlabs.map.proj.LambertConformalConicProjection;
import com.jhlabs.map.proj.Projection;

public class Lane {
	public ArrayList<Lane> exits = new ArrayList<>();
	
	public Node node1;
	public Node node2;
	
	public double real_length; //in real world
	public Point2D.Double real_start;
	public Point2D.Double real_end;
	public int x1,y1,x2,y2;
	
	public Lane(Node n1, Node n2) {
		this.node1 = n1;
		this.node2 = n2;
		
		this.real_start = node1.point;
		this.real_end = node2.point;
		this.real_length = real_start.distance(real_end);
	}
	
	public void setScreenLocations(int x1, int y1, int x2, int y2, MapRenderParams params) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
	
	
}