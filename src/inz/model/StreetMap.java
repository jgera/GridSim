package inz.model;

import java.util.List;

public class StreetMap {
	public Way[] ways;
	public List<Car> cars;
	public Lane[] lanes;
	
	public StreetMap(Way[] ways) {
		this.ways = ways;
	}
}
