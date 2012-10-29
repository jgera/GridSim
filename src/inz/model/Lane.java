package inz.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Lane {
	public ArrayList<Lane> exits = new ArrayList<>();
	
	public Node node1;
	public Node node2;
	
	public int length; //in real world
	
	public Lane(Node n1, Node n2) {
		this.node1 = n1;
		this.node2 = n2;
		this.length = (int)Math.round(Point2D.distance(node1., y1, x2, y2)); //FIXME SCALE!!!
	}
	
	
	public int x1,y1,x2,y2;
}
