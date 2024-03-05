package com.fx.swing.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;


public class IntersectionPainter implements Painter<JXMapViewer> {

    private final boolean antiAlias = true;
    private final GeoPosition geoPosition;
    private final LinePainter.LINE_POS line_pos;

    public IntersectionPainter(GeoPosition geoPosition, LinePainter.LINE_POS line_pos) {
        this.geoPosition = geoPosition;
        this.line_pos = line_pos;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setStroke(new BasicStroke(2));

        drawCircle(g, map);

        g.dispose();
    }

    private void drawCircle(Graphics2D g, JXMapViewer map) {

        Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

        double radius = 20;

        g.setColor(Color.MAGENTA);

        double diameter = 2 * radius;

        Ellipse2D ellipse2D = new Ellipse2D.Double(pt.getX() - radius, pt.getY() - radius, diameter, diameter);
        g.fill(ellipse2D);
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public LinePainter.LINE_POS getLine_pos() {
        return line_pos;
    }
}
