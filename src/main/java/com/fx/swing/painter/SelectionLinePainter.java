package com.fx.swing.painter;

import com.fx.swing.pojo.PositionPOJO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class SelectionLinePainter implements Painter<JXMapViewer> {

    private final List<PositionPOJO> border;
    private boolean invert = false;
    private GeoPosition geoPositionStart;
    private GeoPosition geoPositionEnd;
    private int cStart;
    private int cEnd;

    private List<PositionPOJO> list;
    private final HashMap<Integer, GeoPosition> map;

    public SelectionLinePainter(List<PositionPOJO> border, HashMap<Integer, GeoPosition> map) {
        this.border = border;
        this.map = map;

        List<Integer> keys = new ArrayList<>(map.keySet());

        int start = keys.get(0);
        int end = keys.get(1);

        geoPositionStart = map.get(start);
        geoPositionEnd = map.get(end);

        PositionPOJO s = border.get(start);
        PositionPOJO e = border.get(end);

        if (s.getLon() < geoPositionStart.getLongitude()) {
            cStart = start + 1;
        } else {
            cStart = start;
        }

        if (e.getLon() > geoPositionEnd.getLongitude()) {
            cEnd = end - 1;
        } else {
            cEnd = end;
        }
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(4));

        drawRoute(g, map);

        g.dispose();
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        g.setColor(Color.RED);

        list = new ArrayList<>();

        GeneralPath path = new GeneralPath();

        if (invert) {
            Point2D pt = map.getTileFactory().geoToPixel(geoPositionEnd, map.getZoom());
            path.moveTo(pt.getX(), pt.getY());

            list.add(new PositionPOJO(geoPositionEnd.getLongitude(), geoPositionEnd.getLatitude()));

            for (int i = cEnd + 2; i < border.size(); i++) {
                PositionPOJO pair = border.get(i);
                list.add(pair);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            for (int i = 1; i < cStart; i++) {
                PositionPOJO pair = border.get(i);
                list.add(pair);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            pt = map.getTileFactory().geoToPixel(geoPositionStart, map.getZoom());
            path.lineTo(pt.getX(), pt.getY());
            list.add(new PositionPOJO(geoPositionStart.getLongitude(), geoPositionStart.getLatitude()));

        } else {
            Point2D pt = map.getTileFactory().geoToPixel(geoPositionStart, map.getZoom());
            path.moveTo(pt.getX(), pt.getY());

            list.add(new PositionPOJO(geoPositionStart.getLongitude(), geoPositionStart.getLatitude()));

            for (int i = cStart + 1; i < cEnd; i++) {
                PositionPOJO pair = border.get(i);
                list.add(pair);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            pt = map.getTileFactory().geoToPixel(geoPositionEnd, map.getZoom());
            path.lineTo(pt.getX(), pt.getY());

            list.add(new PositionPOJO(geoPositionEnd.getLongitude(), geoPositionEnd.getLatitude()));
        }

        g.draw(path);
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public List<PositionPOJO> getConstructedBorder() {
        return list;
    }
}
