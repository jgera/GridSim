package inz.model;

public class Way {
	public long id;
	public Node[] nodes;
	public String name = "";
	
	public Way(long id, Node[] nodes) {
		this.id = id;
		this.nodes = nodes;
	}
	
	
}
