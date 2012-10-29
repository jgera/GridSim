package inz;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.print.Doc;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import inz.model.Lane;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Way;

public class OsmParser {
	public static StreetMap parseMap(String filename) {
		
		try {
			FileInputStream is = new FileInputStream(filename);
			Builder parser = new Builder();
			Document doc = parser.build(is);
			Element root = doc.getRootElement();
			
			HashMap<Long, Node> nodes = new HashMap<>();
			ArrayList<Way> ways = new ArrayList<>();
			
			for(int i = 0; i < root.getChildElements().size(); i++) {
				Element e = root.getChildElements().get(i);
				String name = e.getLocalName();
				
				if (name.equals("node")) {
					Node n = new Node(	Long.parseLong(e.getAttributeValue("id")),
										Double.parseDouble(e.getAttributeValue("lat")),
										Double.parseDouble(e.getAttributeValue("lon")));
					nodes.put(n.id, n);
				} else if (name.equals("way")) {
					
					ArrayList<Node> way_nodes = new ArrayList<>();
					
					for(int j = 0; j < e.getChildElements().size(); j++) {
						Element e2 = e.getChildElements().get(j);
						if (e2.getLocalName().equals("nd")) {
							Long id = Long.parseLong(e2.getAttributeValue("ref"));
							Node n = nodes.get(id);
							way_nodes.add(n);
						}
					}
					
					Way w = new Way(Long.parseLong(e.getAttributeValue("id")), way_nodes.toArray(new Node[0]));
					ways.add(w);
				}
				
				
			}
			
			StreetMap map = new StreetMap(ways.toArray(new Way[0]));
			return map;
			
		} catch (ParsingException | IOException e) {
			e.printStackTrace();
			return null;
		}
		
	
	}
	
	public static void prepareMap(StreetMap map) {
		for (Way way : map.ways) {
			Node lastNode = null;
			for (Node n : way.nodes) {
				if (n == way.nodes[0]) {	// pierwszy
					lastNode = n;
				} else {					// nastepny
					Lane laneForward = new Lane(lastNode, n);	 //TODO zrobic ref
					Lane laneBackward = new Lane(n, lastNode);
					
					for (Lane l : lastNode.enters) {
						l.exits.add(laneForward);
					}
					
					for (Lane l : lastNode.exits) {
						laneBackward.exits.add(l);
					}
					
					for (Lane l : n.enters) {
						l.exits.add(laneBackward);
					}
					
					for (Lane l : n.exits) {
						laneForward.exits.add(l);
					}
					
					lastNode.exits.add(laneForward);
					n.enters.add(laneForward);
					
					n.exits.add(laneBackward);
					lastNode.enters.add(laneBackward);
				}
				lastNode = n;
			}
		}
		
		//nawrotka na œlepej ulicy
		//TODO SINK / SOURCE
		for (Way way : map.ways) {
			for (Node n : way.nodes) {
				for (Lane l : n.enters) {
					if (l.exits.size() == 0) {
						for (Lane l2 : n.exits) {
							l.exits.add(l2);
						}
					}
				}
			}
		}
		
	}
	
	
	
}
