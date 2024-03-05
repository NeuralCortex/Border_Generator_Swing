package com.fx.swing.painter;

import com.fx.swing.pojo.PositionPOJO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class BorderPainter implements Painter<JXMapViewer> {

    private final List<PositionPOJO> border;
    private GeoPosition center;
    private final Color color;

    public BorderPainter(List<PositionPOJO> border, Color color) {
        this.border = border;
        this.color = color;
        calcCenter();
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(2));

        drawRoute(g, map);

        g.dispose();
    }

    private void calcCenter() {
        double lonMin = border.stream().min(Comparator.comparing(PositionPOJO::getLon)).get().getLon();
        double lonMax = border.stream().max(Comparator.comparing(PositionPOJO::getLon)).get().getLon();

        double latMin = border.stream().min(Comparator.comparing(PositionPOJO::getLat)).get().getLat();
        double latMax = border.stream().max(Comparator.comparing(PositionPOJO::getLat)).get().getLat();

        double centerLon = (lonMax - lonMin) / 2.0 + lonMin;
        double centerLat = (latMax - latMin) / 2.0 + latMin;

        center = new GeoPosition(centerLat, centerLon);
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        g.setColor(Color.BLACK);
        Point2D ptCenter = map.getTileFactory().geoToPixel(center, map.getZoom());
        g.drawOval((int) ptCenter.getX(), (int) ptCenter.getY(), 3, 3);

        g.setColor(color);

        boolean first = true;
        GeneralPath path = new GeneralPath();
        for (int i = 0; i < border.size(); i++) {
            PositionPOJO pair = border.get(i);
            GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
            Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

            if (first) {
                path.moveTo(pt.getX(), pt.getY());
                first = false;
            } else {
                //if (i % 21 == 0 || i % 22 == 0) {
                    path.lineTo(pt.getX(), pt.getY());
                //}
            }
        }
        path.closePath();
        g.draw(path);
    }

    public List<PositionPOJO> getBorder() {
        return border;
    }

    public GeoPosition getCenter() {
        return center;
    }

    public Color getColor() {
        return color;
    }
}
