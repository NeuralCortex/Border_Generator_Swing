package com.fx.swing.painter;

import com.fx.swing.pojo.LinePOJO;
import com.fx.swing.pojo.PositionPOJO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class BorderLinePainter implements Painter<JXMapViewer> {

    private final List<PositionPOJO> border;
    private List<LinePOJO> lineList;
    private Line2D selLine;
    private final Color color;
    private Color selColor;
    private GeoPosition geoPositionLine1;
    private GeoPosition geoPositionLine2;

    public static enum BORDER_TYPE {
        FROM, TO
    }
    private final BORDER_TYPE border_type;

    public BorderLinePainter(List<PositionPOJO> border, Color color, BORDER_TYPE border_type) {
        this.border = border;
        this.color = color;
        this.border_type = border_type;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int i, int i1) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(2));

        drawRoute(g, map);

        if (selLine != null) {
            g.setStroke(new BasicStroke(10));
            g.setColor(selColor);
            g.draw(selLine);
            g.setStroke(new BasicStroke(2));

            geoPositionLine1 = map.getTileFactory().pixelToGeo(new Point2D.Double(selLine.getX1(), selLine.getY1()), map.getZoom());
            geoPositionLine2 = map.getTileFactory().pixelToGeo(new Point2D.Double(selLine.getX2(), selLine.getY2()), map.getZoom());
        }

        g.dispose();
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        g.setColor(color);

        lineList = new ArrayList<>();

        for (int i = 0; i < border.size() - 1; i++) {
            PositionPOJO pair0 = border.get(i);
            PositionPOJO pair1 = border.get(i + 1);

            GeoPosition geoPosition0 = new GeoPosition(pair0.getLat(), pair0.getLon());
            GeoPosition geoPosition1 = new GeoPosition(pair1.getLat(), pair1.getLon());

            Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
            Point2D pt1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());

            Line2D line2D = new Line2D.Double(pt0, pt1);
            lineList.add(new LinePOJO(i, line2D));
            g.draw(line2D);
        }
    }

    public List<PositionPOJO> getBorder() {
        return border;
    }

    public List<LinePOJO> getLineList() {
        return lineList;
    }

    public Line2D getSelLine() {
        return selLine;
    }

    public void setSelLine(Line2D selLine) {
        this.selLine = selLine;
    }

    public Color getColor() {
        return color;
    }

    public void setSelColor(Color selColor) {
        this.selColor = selColor;
    }

    public BORDER_TYPE getBorder_type() {
        return border_type;
    }

    public GeoPosition getGeoPositionLine1() {
        return geoPositionLine1;
    }

    public GeoPosition getGeoPositionLine2() {
        return geoPositionLine2;
    }

    public void setGeoPositionLine1(GeoPosition geoPositionLine1) {
        this.geoPositionLine1 = geoPositionLine1;
    }

    public void setGeoPositionLine2(GeoPosition geoPositionLine2) {
        this.geoPositionLine2 = geoPositionLine2;
    }
}
