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
	
	public ArrayList<Lane> enters = new ArrayList<Lane>();
	public ArrayList<Lane> exits = new ArrayList<Lane>();
	
	//world
	public double lat;
	public double lon;
	
	//world normalised
	public Point2D.Double point;
	
	//screen
	public int x;
	public int y;
	
	//state
	public boolean intersectionTaken = false;
}
