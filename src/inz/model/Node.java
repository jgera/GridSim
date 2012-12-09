package inz.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class Node {
	
	public Node(long id, double lat, double lon) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}
	
	public long id;
	
	public ArrayList<Lane> enters = new ArrayList<Lane>();
	public ArrayList<Lane> exits = new ArrayList<Lane>();
	public LinkedList<Car> queue = new LinkedList<Car>();
	
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
	
	//sink source
	public boolean isSink = false;
	public double sourceRatio = 0.01;
}
