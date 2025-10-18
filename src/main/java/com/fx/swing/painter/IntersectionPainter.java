package com.fx.swing.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * 
 * @author Neural Cortex
 */
public class IntersectionPainter implements Painter<JXMapViewer> {

    private final boolean antiAlias = true;
    private final GeoPosition geoPosition;
    private final LinePainter.LINE_POS line_pos;
    private final int idx;
    
    // Formatting and styling
    private final DecimalFormat latLonFormat = new DecimalFormat("0.00000");
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0, 200); // Semi-transparent black
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color GLOW_COLOR = new Color(255, 255, 255, 100);
    private final Font LABEL_FONT = new Font("Arial", Font.BOLD, 11);
    private final int LABEL_PADDING = 8;
    private final int CORNER_RADIUS = 12;
    
    private String latLonText;
    private Point2D labelPosition;

    public IntersectionPainter(GeoPosition geoPosition, LinePainter.LINE_POS line_pos,int idx) {
        this.geoPosition = geoPosition;
        this.line_pos = line_pos;
        this.idx=idx;
        formatLatLonText();
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        g = (Graphics2D) g.create();
        
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
        calculateLabelPosition(pt, g,map);
        
        // Draw glowing circle effect
        drawGlowingCircle(g, pt);
        
        // Draw main intersection circle
        drawCircle(g, pt);
        
        // Draw fancy lat/lon label
        drawLatLonLabel(g, pt);
        
        g.dispose();
    }

    private void drawGlowingCircle(Graphics2D g, Point2D center) {
        // Multiple glow layers for fancy effect
        g.setColor(GLOW_COLOR);
        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval((int)(center.getX() - 12), (int)(center.getY() - 12), 24, 24);
        
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval((int)(center.getX() - 8), (int)(center.getY() - 8), 16, 16);
    }

    private void drawCircle(Graphics2D g, Point2D center) {
        double radius = 6;
        g.setColor(Color.MAGENTA);
        g.setStroke(new BasicStroke(2));
        
        // Outer stroke
        g.setStroke(new BasicStroke(3));
        g.drawOval((int)(center.getX() - radius - 1), (int)(center.getY() - radius - 1), 
                  (int)(2 * radius + 2), (int)(2 * radius + 2));
        
        // Filled circle
        g.setStroke(new BasicStroke(1));
        Ellipse2D ellipse2D = new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, 
                                                 2 * radius, 2 * radius);
        g.fill(ellipse2D);
        
        // Inner highlight
        g.setColor(new Color(255, 255, 255, 150));
        Ellipse2D highlight = new Ellipse2D.Double(center.getX() - radius * 0.4, 
                                                 center.getY() - radius * 0.4, 
                                                 radius * 0.8, radius * 0.8);
        g.fill(highlight);
    }

    private void drawLatLonLabel(Graphics2D g, Point2D center) {
        if (labelPosition == null || latLonText == null) return;
        
        g.setFont(LABEL_FONT);
        g.setColor(BACKGROUND_COLOR);
        
        // Draw rounded background rectangle
        RoundRectangle2D bgRect = new RoundRectangle2D.Double(
            labelPosition.getX() - LABEL_PADDING,
            labelPosition.getY() - LABEL_PADDING,
            getTextWidth(g, latLonText) + 2 * LABEL_PADDING,
            g.getFontMetrics().getHeight() + 2 * LABEL_PADDING,
            CORNER_RADIUS, CORNER_RADIUS
        );
        g.fill(bgRect);
        
        // Draw border glow
        g.setColor(new Color(138, 43, 226, 100)); // Blue-violet glow
        g.setStroke(new BasicStroke(2));
        g.draw(bgRect);
        
        // Draw text
        g.setColor(TEXT_COLOR);
        g.drawString(latLonText, (int)labelPosition.getX(), (int)labelPosition.getY() + g.getFontMetrics().getAscent());
    }

    private void calculateLabelPosition(Point2D center, Graphics2D g,JXMapViewer map) {
        g.setFont(LABEL_FONT);
        int textWidth = getTextWidth(g, latLonText);
        int textHeight = g.getFontMetrics().getHeight();
        
        // Position label to the right of circle with some offset
        // Adjust position to avoid going off-screen
        double labelX = center.getX() + 20;
        double labelY = center.getY() - textHeight / 2;
        
        Rectangle viewport = map.getViewportBounds();
        if (labelX + textWidth > viewport.getMaxX()) {
            labelX = center.getX() - textWidth - 20; // Move to left
        }
        if (labelY < viewport.getMinY()) {
            labelY = center.getY() + 10; // Move below
        } else if (labelY + textHeight > viewport.getMaxY()) {
            labelY = center.getY() - textHeight - 10; // Move above
        }
        
        labelPosition = new Point2D.Double(labelX, labelY);
    }

    private int getTextWidth(Graphics2D g, String text) {
        FontMetrics metrics = g.getFontMetrics();
        return metrics.stringWidth(text);
    }

    private void formatLatLonText() {
        String lat = latLonFormat.format(Math.abs(geoPosition.getLatitude()));
        String lon = latLonFormat.format(Math.abs(geoPosition.getLongitude()));
        
        String latDir = geoPosition.getLatitude() >= 0 ? "N" : "S";
        String lonDir = geoPosition.getLongitude() >= 0 ? "E" : "W";
        
        latLonText = String.format("%s° %s %s° %s", lat, latDir, lon, lonDir);
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public LinePainter.LINE_POS getLine_pos() {
        return line_pos;
    }
    
    public String getLatLonText() {
        return latLonText;
    }

    public int getIdx() {
        return idx;
    }
    
    
}