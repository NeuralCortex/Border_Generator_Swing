package com.fx.swing.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class CirclePainter implements Painter<JXMapViewer> {

    private final Color color = Color.BLACK;
    private final boolean antiAlias = true;
    private GeoPosition geoPosition;

    public interface CirclePainterListener {

        public void getCircle(Ellipse2D ellipse2D);
    }
    private CirclePainterListener circlePainterListener;

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

        if (geoPosition != null) {
            drawCircle(g, map);
        }

        g.dispose();
    }

    private void drawCircle(Graphics2D g, JXMapViewer map) {

        Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

        double radius = 20;

        g.setColor(Color.RED);

        double diameter = 2 * radius;

        Ellipse2D ellipse2D = new Ellipse2D.Double(pt.getX() - radius, pt.getY() - radius, diameter, diameter);
        circlePainterListener.getCircle(ellipse2D);
        g.draw(ellipse2D);

        g.setColor(Color.BLACK);

        Line2D line2DHor = new Line2D.Double(pt.getX() - radius, pt.getY(), pt.getX() + radius, pt.getY());
        Line2D line2DVer = new Line2D.Double(pt.getX(), pt.getY() - radius, pt.getX(), pt.getY() + radius);
        g.draw(line2DHor);
        g.draw(line2DVer);
    }

    public void setGeoPosition(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    public void setCirclePainterListener(CirclePainterListener circlePainterListener) {
        this.circlePainterListener = circlePainterListener;
    }
}
