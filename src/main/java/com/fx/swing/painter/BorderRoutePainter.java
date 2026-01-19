package com.fx.swing.painter;

import com.fx.swing.tools.HelperFunctions;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Locale;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class BorderRoutePainter implements Painter<JXMapViewer> {

    private Color color = Color.RED;
    private final boolean antiAlias = true;
    private final List<GeoPosition> track;

    public BorderRoutePainter(List<GeoPosition> track) {
        this.track = track;
        color = genRandomColor();
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
        g.setStroke(new BasicStroke(2));

        drawRoute(g, map);

        g.dispose();
    }

    private Color genRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        boolean first = true;

        GeneralPath path = new GeneralPath();
        int count = 0;
        for (int i = 0; i < track.size(); i++) {
            GeoPosition geoPosition = track.get(i);

            Point2D pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());

            if (first) {
                path.moveTo(pt.getX(), pt.getY());
                first = false;
            } else {
                path.lineTo(pt.getX(), pt.getY());
            }
            count++;
        }
        g.draw(path);
    }

    public Color getColor() {
        return color;
    }

    public int getSize() {
        return track.size();
    }

    public String getLength() {
        double length = 0;
        for (int i = 0; i < track.size() - 1; i++) {
            GeoPosition start = track.get(i);
            GeoPosition next = track.get(i + 1);
            length += HelperFunctions.getDistance(start.getLongitude(), start.getLatitude(), next.getLongitude(), next.getLatitude());
        }
        return String.format(Locale.US, "%.2f", length);
    }

    public List<GeoPosition> getTrack() {
        return track;
    }
}
