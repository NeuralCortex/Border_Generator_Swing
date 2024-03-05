package com.fx.swing.painter;

import com.fx.swing.pojo.PositionPOJO;
import com.fx.swing.tools.HelperFunctions;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class LinePainter implements Painter<JXMapViewer> {

    private Color color;
    private final boolean antiAlias = true;
    private int idx = 0;
    private List<PositionPOJO> posList;
    private LINE_POS line_pos;
    private BorderLinePainter.BORDER_TYPE border_type;
    //21.06.2023
    private GeoPosition start;
    private GeoPosition end;
    private boolean onlyLine = false;

    public static enum LINE_POS {
        START, END
    };

    public LinePainter(GeoPosition start, GeoPosition end) {
        this.start = start;
        this.end = end;
        onlyLine = true;
    }

    public LinePainter(List<PositionPOJO> posList, LINE_POS line_pos, Color color, BorderLinePainter.BORDER_TYPE border_type) {
        this.posList = posList;
        this.line_pos = line_pos;
        this.color = color;
        this.border_type = border_type;
    }

    public LinePainter(List<PositionPOJO> posList, int idx, LINE_POS line_pos, Color color, BorderLinePainter.BORDER_TYPE border_type) {
        this.posList = posList;
        this.idx = idx;
        this.line_pos = line_pos;
        this.color = color;
        this.border_type = border_type;
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

        if (onlyLine) {
            g.setStroke(new BasicStroke(2));
            drawLineSimple(g, map);
        } else {
            g.setStroke(new BasicStroke(10));
            drawLine(g, map);
        }

        g.dispose();
    }

    private void drawLine(Graphics2D g, JXMapViewer map) {
        PositionPOJO pair0 = posList.get(idx);
        PositionPOJO pair1 = posList.get(idx + 1);

        GeoPosition geoPosition0 = new GeoPosition(pair0.getLat(), pair0.getLon());
        GeoPosition geoPosition1 = new GeoPosition(pair1.getLat(), pair1.getLon());

        Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
        Point2D pt1 = map.getTileFactory().geoToPixel(geoPosition1, map.getZoom());

        Line2D line2D = new Line2D.Double(pt0, pt1);

        g.draw(line2D);
    }

    private void drawLineSimple(Graphics2D g, JXMapViewer map) {
        Point2D pt0 = map.getTileFactory().geoToPixel(start, map.getZoom());
        Point2D pt1 = map.getTileFactory().geoToPixel(end, map.getZoom());

        Line2D line2D = new Line2D.Double(pt0, pt1);

        g.draw(line2D);

        double length = HelperFunctions.getDistance(start.getLongitude(), start.getLatitude(), end.getLongitude(), end.getLatitude());
        String lStr = String.format("%.2f", length) + " km";
        int halfLine = 20;

        g.setFont(new Font("Arial", Font.PLAIN, 10));
        int width = g.getFontMetrics().stringWidth(lStr);

        double mx = (pt0.getX() + pt1.getX()) / 2.0;
        double my = (pt0.getY() + pt1.getY()) / 2.0;

        g.setColor(Color.ORANGE);
        Rectangle2D rectangle2D = new Rectangle2D.Double(mx, my - halfLine, (double) (width + 10), (double) halfLine);
        g.fill(rectangle2D);

        g.setColor(Color.BLACK);
        g.drawString(lStr, (int) mx + 5, (int) my - 5);
    }

    public int getIdx() {
        return idx;
    }

    public LINE_POS getLine_pos() {
        return line_pos;
    }

    public BorderLinePainter.BORDER_TYPE getBorder_type() {
        return border_type;
    }

    public Color getColor() {
        return color;
    }
}
