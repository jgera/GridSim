package inz.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Node {
	
	public Node(long id, double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}
	
	public long id;
	public double lat;
	public double lon;
	
	public int x;
	public int y;
	
	public Point2D.Double point;
	
	public ArrayList<Lane> enters = new ArrayList<>();
	public ArrayList<Lane> exits = new ArrayList<>();
	
	public boolean intersectionTaken = false;
}
