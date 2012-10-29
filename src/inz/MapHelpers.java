package inz;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jhlabs.map.proj.MercatorProjection;

import inz.model.Lane;
import inz.model.Node;
import inz.model.StreetMap;
import inz.model.Way;

public class MapHelpers {
	
	public static class MapRenderParams {
		public double scale;
		final double def_scale = 7680072;
		
		public float scaleWidth(double value) {
			return (float)(value * scale / def_scale);
		}
	}

	public static MapRenderParams prepareDataToRender(StreetMap streetMap, int screenWidth, int screenHeight) {
		
		MercatorProjection projection = new MercatorProjection();
        List<Point2D.Double> xys = new ArrayList<Point2D.Double>();
        for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		double latitude = n.lat * Math.PI / 180;
                double longitude = n.lon * Math.PI / 180;
                Point2D.Double d = projection.project(longitude, latitude, new Point2D.Double());
                n.point = d;
                xys.add(d);
        	}
        }
        
        //szukamy œrodka
        double avg_x = 0, avg_y = 0;
        for (Point2D p : xys) {
        	avg_x += p.getX();
        	avg_y += p.getY();
        }
        avg_x = avg_x / xys.size();
        avg_y = avg_y / xys.size();
        
        //normalizacja
        for (Point2D p : xys) {
        	p.setLocation(p.getX() - avg_x, p.getY() - avg_y);
        }
        
        double biggestX =0, biggestY =0, smallestX =0, smallestY =0;
        for (Point2D p : xys) {
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
        
        scale = scale * 0.9;
        
        for (Point2D p : xys) {
        	p.setLocation(p.getX() * scale + screenWidth/2, screenHeight/2 - p.getY() * scale);
        }
        
        for (Way way : streetMap.ways) {
        	for (Node n : way.nodes) {
        		n.x = (int)Math.round(n.point.getX());
        		n.y = (int)Math.round(n.point.getY());
        	}
        }

        MapRenderParams ret = new MapRenderParams();
        ret.scale = scale;
        return ret;
	}
	
	public static void findLanesPositions(StreetMap streetMap, MapRenderParams params) {
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
			double x = n2.x - n1.x;
			double y = -(n2.y - n1.y);
			double len = Math.sqrt(x*x + y*y);
			double p_x = y / len; //?!WTF
			double p_y = x / len;
			
			//przesuniecie do osi
			double margin = params.scaleWidth(5f);
			int sx = (int)Math.round(n1.x + p_x * margin);
			int sy = (int)Math.round(n1.y + p_y * margin);
			int ex = (int)Math.round(n2.x + p_x * margin);
			int ey = (int)Math.round(n2.y + p_y * margin);
			
			//przesuniecie od koncowek
			double margin2 = params.scaleWidth(5f);
			double nx = (ex - sx);
			double ny = (ey - sy);
			double nlen = Math.sqrt((nx*nx+ny*ny));
			nx = nx / nlen;
			ny = ny / nlen;
			nx = nx * margin2;
			ny = ny * margin2;
			ex -= (int)Math.round(nx);
			ey -= (int)Math.round(ny);
			sx += (int)Math.round(nx);
			sy += (int)Math.round(ny);
			
			//TODO ograniczyc zeby sie nie zamienia³y miejscami
			
			l.x1 = sx;
			l.y1 = sy;
			
			l.x2 = ex;
			l.y2 = ey;
		}
	}
}
	
