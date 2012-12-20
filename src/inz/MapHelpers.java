package inz;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import com.jhlabs.map.proj.MercatorProjection;
import com.jhlabs.map.proj.Projection;

import inz.model.Lane;
import inz.model.LaneExit;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Way;

public class MapHelpers {
	
	public static class MapRenderParams {
		public double scale;
		public static final double def_scale = 1.85;
		
		public float scaleWidth(double value) {
			return (float)(value * scale / def_scale);
		}
	}
	
	public static Point2D.Double projectToXY(double lon, double lat) {
		Projection projection = new MercatorProjection();
		Point2D.Double p = projection.project(lon, lat, new Point2D.Double());
		p.x = 4139593.33 * p.x;	//FIXME nie wiem skad ta wartosc
		p.y = 4139593.33 * p.y;
		return p;
	}
	
	/**
	 * Wykonuje projekcje wszystkich punktow na mapie na plaszczyzne XY.
	 * @param streetMap
	 */
	public static void projectNodePoints(StreetMap streetMap) {
		for (Way way : streetMap.ways) {
			for (Node n : way.nodes) {
				double latitude = n.lat * Math.PI / 180;
				double longitude = n.lon * Math.PI / 180;
				n.point = projectToXY(longitude, latitude);
			}
		}
	}
	
	public static void normaliseNodePositions(StreetMap streetMap) {
		HashMap<Long, Point2D.Double> xys = new HashMap<Long, Point2D.Double>();
        for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
                xys.put(n.id, n.point);
        	}
        }
		
        //szukamy œrodka
        double avg_x = 0, avg_y = 0;
        for (Point2D p : xys.values()) {
        	avg_x += p.getX();
        	avg_y += p.getY();
        }
        avg_x = avg_x / xys.size();
        avg_y = avg_y / xys.size();
        
        //normalizacja
        for (Point2D.Double p : xys.values()) {
        	p.setLocation(p.getX() - avg_x, p.getY() - avg_y);
        }
	}
	
	/**
	 * Uzupelnia elementy Node o wspolzedne ekranowe. Zwraca skale.
	 * 
	 * @param streetMap
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	public static MapRenderParams prepareDataToRender(StreetMap streetMap, int screenWidth, int screenHeight) {
		HashMap<Long, Point2D.Double> xys = new HashMap<Long, Point2D.Double>();
        for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
                xys.put(n.id, new Point2D.Double(n.point.x, n.point.y));
        	}
        }
        
        double biggestX =0, biggestY =0, smallestX =0, smallestY =0;
        for (Point2D p : xys.values()) {
        	if (xys.get(0) == p) { //pierwszy
        		biggestX = p.getX();
        		smallestX = p.getX();
        		biggestY = p.getY();
        		smallestY = p.getY();
        	}
        	
        	if (p.getX() > biggestX) 
        		biggestX = p.getX();
        	
        	if (p.getX() < smallestX)
        		smallestX = p.getX();
        	
        	if (p.getY() > biggestY) 
        		biggestY = p.getY();
        	
        	if (p.getY() < smallestY)
        		smallestY = p.getY();
        }
        
        double requiredWidth = Math.abs(biggestX - smallestX);
        double requiredHeight = Math.abs(biggestY - smallestY);
        
        double scaleX = screenWidth / requiredWidth;
        double scaleY = screenHeight / requiredHeight;
        double scale = scaleX < scaleY ? scaleX : scaleY;
        
        scale = scale * 0.9;	//margines
        
        for (Point2D p : xys.values()) {
        	p.setLocation(p.getX() * scale + screenWidth/2, screenHeight/2 - p.getY() * scale);
        }
        
        for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		Point2D.Double p = xys.get(n.id);
        		n.x = (int)Math.round(p.getX());
        		n.y = (int)Math.round(p.getY());
        		for (Lane l : n.exits) {				
        			l.x1 = (int)Math.round(l.real_start.x * scale + screenWidth/2);
            		l.y1 = (int)Math.round(screenHeight/2 - l.real_start.y * scale);
            		l.x2 = (int)Math.round(l.real_end.x * scale + screenWidth/2);
            		l.y2 = (int)Math.round(screenHeight/2 - l.real_end.y * scale);
        		}
        	}
        }

        MapRenderParams ret = new MapRenderParams();
        ret.scale = scale;
        return ret;
	}
	
	public static void findConnectorLengths(StreetMap streetMap) {
		Set<Lane> lanes = new HashSet<Lane>();
		for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		lanes.addAll(n.enters);
        		lanes.addAll(n.exits);
        	}
		}
		for (Lane l : lanes) {
			for (LaneExit exit : l.exits) {
				exit.distance = l.real_end.distance(exit.lane.real_start);
			}
		}
	}
	
	/**
	 * Znajduje pozycje ekranowe obiektow Lane.
	 * @param streetMap
	 * @param params
	 */
	public static void findLanesPositions(StreetMap streetMap) {
		Set<Lane> lanes = new HashSet<Lane>();
		for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		lanes.addAll(n.enters);
        		lanes.addAll(n.exits);
        	}
		}
		for (Lane l : lanes) {
			Node n1 = l.node1;
			Node n2 = l.node2;

			//wektor prostopadly
			double x = n2.point.x - n1.point.x;
			double y = -(n2.point.y - n1.point.y);
			double len = Math.sqrt(x*x + y*y);
			double p_x = y / len; //?!WTF
			double p_y = x / len;
			
			//przesuniecie do osi
			double margin = -2; // [m]
			double sx = n1.point.x + p_x * margin;
			double sy = n1.point.y + p_y * margin;
			double ex = n2.point.x + p_x * margin;
			double ey = n2.point.y + p_y * margin;
			
			//przesuniecie od koncowek
			double margin2 = 2; // [m]
			double nx = (ex - sx);
			double ny = (ey - sy);
			double nlen = Math.sqrt((nx*nx+ny*ny));
			nx = nx / nlen;
			ny = ny / nlen;
			nx = nx * margin2;
			ny = ny * margin2;
			ex -= nx;
			ey -= ny;
			sx += nx;
			sy += ny;
			
			//TODO ograniczyc zeby sie nie zamienia³y miejscami
			
			l.real_start = new Point2D.Double(sx, sy);
			l.real_end = new Point2D.Double(ex, ey);
			l.real_length = l.real_start.distance(l.real_end);
		}
	}
	
	
/**
 * Parsuje XMLa z OpenStreetMap. Tworzy obiekty Node i Way.
 * @param filename nazwa pliku XML
 * @return
 */
public static StreetMap parseMap(String filename) {
		
		try {
			FileInputStream is = new FileInputStream(filename);
			Builder parser = new Builder();
			Document doc = parser.build(is);
			Element root = doc.getRootElement();
			
			HashMap<Long, Node> nodes = new HashMap<Long, Node>();
			ArrayList<Way> ways = new ArrayList<Way>();
			
			for(int i = 0; i < root.getChildElements().size(); i++) {
				Element e = root.getChildElements().get(i);
				String name = e.getLocalName();
				
				if (name.equals("node")) {
					Node n = new Node(	Long.parseLong(e.getAttributeValue("id")),
										Double.parseDouble(e.getAttributeValue("lat")),
										Double.parseDouble(e.getAttributeValue("lon")));
					nodes.put(n.id, n);
				} else if (name.equals("way")) {
					
					ArrayList<Node> way_nodes = new ArrayList<Node>();
					
					for(int j = 0; j < e.getChildElements().size(); j++) {
						Element e2 = e.getChildElements().get(j);
						if (e2.getLocalName().equals("nd")) {
							Long id = Long.parseLong(e2.getAttributeValue("ref"));
							Node n = nodes.get(id);
							way_nodes.add(n);
						}
					}
					
					Node[] node_array = way_nodes.toArray(new Node[0]);
					Way w = new Way(Long.parseLong(e.getAttributeValue("id")), node_array);
					ways.add(w);
				}
				
				
			}
			
			StreetMap map = new StreetMap(ways.toArray(new Way[0]));
			map.nodes = nodes.values().toArray(new Node[0]);
			return map;
			
		} catch (ParsingException e) {
			e.printStackTrace();
			return null;
		} catch  (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	
	}
	
/**
 * Na podstawie obiektow Node tworzy odpowiadajace im obiekty Lane.
 * @param map
 */
	public static void prepareMap(StreetMap map) {
		for (Way way : map.ways) {
			Node lastNode = null;
			for (Node n : way.nodes) {
				if (n == way.nodes[0]) {	// pierwszy element drogi
					lastNode = n;
				} else {					// kolejne elementy
					Lane laneForward = new Lane(lastNode, n);	 //TODO zrobic ref
					Lane laneBackward = new Lane(n, lastNode);
					
					for (Lane l : lastNode.enters) 
						l.exits.add(new LaneExit(laneForward));
					
					for (Lane l : lastNode.exits) 
						laneBackward.exits.add(new LaneExit(l));
					
					for (Lane l : n.enters) 
						l.exits.add(new LaneExit(laneBackward));
					
					for (Lane l : n.exits) 
						laneForward.exits.add(new LaneExit(l));
					
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
						n.isSink = true;
						for (Lane l2 : n.exits) {
							n.sourceRatio = 0.01;
							//l.exits.add(new LaneExit(l2));
						}
					}
				}
			}
		}
		
		 Set<Lane> lanes = new HashSet<Lane>();
		for (Way way : map.ways) {
        	for (Node n : way.nodes) {
        		lanes.addAll(n.enters);
        		lanes.addAll(n.exits);
        	}
		}
		map.lanes = lanes.toArray(new Lane[0]);
		
	}
}
	
