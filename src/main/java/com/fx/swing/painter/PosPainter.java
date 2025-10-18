package com.fx.swing.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class PosPainter implements Painter<JXMapViewer> {
    
    private final Color color = Color.BLACK;
    private final boolean antiAlias = true;
    private final GeoPosition geoPosition;
    private final DecimalFormat latLonFormat = new DecimalFormat("0.00000");
    
    public PosPainter(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }
    
    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();
        
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);
        
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        g.setColor(color);
        g.setStroke(new BasicStroke(1));
        
        drawPoint(g, map);
        
        g.dispose();
    }
    
    private void drawPoint(Graphics2D g, JXMapViewer map) {
        
        Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
        
        int halfLine = 20;
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        String text = formatLatLonText();
        int width = g.getFontMetrics().stringWidth(text);
        
        g.setColor(Color.RED);
        Rectangle2D rectangle2D = new Rectangle2D.Double(pt.getX(), pt.getY() - halfLine, (double) (width + 10), (double) halfLine);
        //g.fillRect((int) pt.getX(), (int) pt.getY() - halfLine, width + 10, halfLine);
        g.fill(rectangle2D);
        
        g.setColor(Color.BLACK);
        
        g.drawLine((int) pt.getX() - halfLine, (int) pt.getY(), (int) pt.getX() + halfLine, (int) pt.getY());
        g.drawLine((int) pt.getX(), (int) pt.getY() - halfLine, (int) pt.getX(), (int) pt.getY() + halfLine);
        
        g.setColor(Color.WHITE);
        g.drawString(text, (int) pt.getX() + 5, (int) pt.getY() - 5);
    }
    
    private String formatLatLonText() {
        String lat = latLonFormat.format(Math.abs(geoPosition.getLatitude()));
        String lon = latLonFormat.format(Math.abs(geoPosition.getLongitude()));
        
        String latDir = geoPosition.getLatitude() >= 0 ? "N" : "S";
        String lonDir = geoPosition.getLongitude() >= 0 ? "E" : "W";
        
        return String.format("%s° %s %s° %s", lat, latDir, lon, lonDir);
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }
}
