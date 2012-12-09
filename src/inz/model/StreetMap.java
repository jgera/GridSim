package inz.model;

import java.util.ArrayList;
import java.util.List;

public class StreetMap {
	public Way[] ways;
	public List<Car> cars = new ArrayList<Car>();
	public Lane[] lanes;
	public Node[] nodes;
	
	public StreetMap(Way[] ways) {
		this.ways = ways;
	}
}
