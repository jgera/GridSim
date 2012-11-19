package inz.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Lane {
	public ArrayList<LaneExit> exits = new ArrayList<LaneExit>();
	
	public Node node1;
	public Node node2;
	
	//world
	public double real_length; //in real world
	public Point2D.Double real_start, real_end;
	
	//screen
	public int x1,y1,x2,y2;
	
	public Lane(Node n1, Node n2) {
		this.node1 = n1;
		this.node2 = n2;
	}
	
}
