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
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class SelectionLinePainter implements Painter<JXMapViewer> {

    private final List<PositionPOJO> border;
    private int start;
    private int end;
    private boolean invert = false;
    private final List<PositionPOJO> borderFull;
    private GeoPosition geoPositionStart;
    private GeoPosition geoPositionEnd;

    public SelectionLinePainter(List<PositionPOJO> border, int start, int end, List<PositionPOJO> borderFull, GeoPosition geoPositionStart, GeoPosition geoPositionEnd) {
        this.start = start;
        this.end = end;
        this.borderFull = borderFull;
        this.geoPositionStart = geoPositionStart;
        this.geoPositionEnd = geoPositionEnd;
        this.border = cutEndOff(border);
    }

    private List<PositionPOJO> cutEndOff(List<PositionPOJO> border) {

        int help = start;
        if (end < start) {
            start = end;
            end = help;
        }

        List<PositionPOJO> posList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            posList.add(border.get(i));
        }
        posList.remove(0);
        posList.remove(posList.size() - 1);
        return posList;
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

        int help = start;
        if (end < start) {
            start = end;
            end = help;
        }

        GeoPosition helpGeo = geoPositionStart;
        if (geoPositionEnd.getLongitude() < geoPositionStart.getLongitude()) {
            geoPositionStart = geoPositionEnd;
            geoPositionEnd = helpGeo;
        }

        GeneralPath path = new GeneralPath();

        if (invert) {
            Point2D pt = map.getTileFactory().geoToPixel(geoPositionEnd, map.getZoom());
            path.moveTo(pt.getX(), pt.getY());

            for (int i = start; i >= 0; i--) {
                PositionPOJO pair = borderFull.get(i);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            for (int i = borderFull.size() - 1; i > end; i--) {
                PositionPOJO pair = borderFull.get(i);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            pt = map.getTileFactory().geoToPixel(geoPositionStart, map.getZoom());
            path.lineTo(pt.getX(), pt.getY());

        } else {
            Point2D pt = map.getTileFactory().geoToPixel(geoPositionStart, map.getZoom());
            path.moveTo(pt.getX(), pt.getY());

            for (int i = border.size() - 1; i >= 0; i--) {
                PositionPOJO pair = border.get(i);
                GeoPosition geoPosition = new GeoPosition(pair.getLat(), pair.getLon());
                pt = map.getTileFactory().geoToPixel(geoPosition, map.getZoom());
                path.lineTo(pt.getX(), pt.getY());
            }

            pt = map.getTileFactory().geoToPixel(geoPositionEnd, map.getZoom());
            path.lineTo(pt.getX(), pt.getY());
        }

        g.draw(path);
    }

    public List<PositionPOJO> getBorder() {
        return border;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public List<PositionPOJO> getFullBorder() {
        List<PositionPOJO> fullList = new ArrayList<>();

        int help = start;
        if (end < start) {
            start = end;
            end = help;
        }

        GeoPosition helpGeo = geoPositionStart;
        if (geoPositionEnd.getLongitude() < geoPositionStart.getLongitude()) {
            geoPositionStart = geoPositionEnd;
            geoPositionEnd = helpGeo;
        }

        if (invert) {
            fullList.add(new PositionPOJO(geoPositionEnd.getLongitude(), geoPositionEnd.getLatitude()));

            for (int i = start - 1; i >= 0; i--) {
                PositionPOJO position = borderFull.get(i);
                fullList.add(new PositionPOJO(position.getLon(), position.getLat(), i));
            }
            for (int i = borderFull.size() - 1; i > end; i--) {
                PositionPOJO position = borderFull.get(i);
                fullList.add(new PositionPOJO(position.getLon(), position.getLat(), i));
            }

            fullList.add(new PositionPOJO(geoPositionStart.getLongitude(), geoPositionStart.getLatitude()));
        } else {
            fullList.add(new PositionPOJO(geoPositionStart.getLongitude(), geoPositionStart.getLatitude()));

            for (int i = border.size() - 1; i >= 0; i--) {
                PositionPOJO position = border.get(i);
                fullList.add(new PositionPOJO(position.getLon(), position.getLat(), i));
            }

            fullList.add(new PositionPOJO(geoPositionEnd.getLongitude(), geoPositionEnd.getLatitude()));
        }

        return fullList;
    }
}
