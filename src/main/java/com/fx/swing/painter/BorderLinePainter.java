package com.fx.swing.painter;

import com.fx.swing.pojo.LinePOJO;
import com.fx.swing.pojo.PositionPOJO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author Neural Cortex
 */
public class BorderLinePainter implements Painter<JXMapViewer> {

    private final List<PositionPOJO> border;
    private List<LinePOJO> lineList = null;
    private int zoom;
    private Line2D selLine;
    private final Color color;
    private Color selColor;
    private GeoPosition geoPositionLine1;
    private GeoPosition geoPositionLine2;
    private boolean intersection;

    public static enum BORDER_TYPE {
        FROM, TO
    }
    private final BORDER_TYPE border_type;

    // Dashed line properties for TO borders
    private final float[] DASH_PATTERN = {10f, 10f}; // Dash length, gap length
    private final float DASH_PHASE = 0f;
    private final BasicStroke SOLID_STROKE = new BasicStroke(2);
    private final BasicStroke DASHED_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, DASH_PATTERN, DASH_PHASE);
    private final BasicStroke SELECTED_STROKE = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public BorderLinePainter(List<PositionPOJO> border, Color color, BORDER_TYPE border_type) {
        this.border = border;
        this.color = color;
        this.border_type = border_type;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Use appropriate stroke based on border type
        BasicStroke routeStroke = (border_type == BORDER_TYPE.TO) ? DASHED_STROKE : SOLID_STROKE;
        g.setStroke(routeStroke);

        if (lineList == null || zoom != map.getZoom()) {
            initLineList(map);
            zoom = map.getZoom();
        }

        g.setColor(color);
        drawRoute(g, map);

        // Draw selected line with highlight
        if (selLine != null && selColor != null) {
            g.setStroke(SELECTED_STROKE);
            g.setColor(selColor);
            g.draw(selLine);

            // Store geo positions for selected line
            geoPositionLine1 = map.getTileFactory().pixelToGeo(new Point2D.Double(selLine.getX1(), selLine.getY1()), map.getZoom());
            geoPositionLine2 = map.getTileFactory().pixelToGeo(new Point2D.Double(selLine.getX2(), selLine.getY2()), map.getZoom());

            // Reset to normal stroke for any additional drawing
            g.setStroke(routeStroke);
        }

        g.dispose();
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        GeneralPath path = new GeneralPath();

        // Start with first point
        PositionPOJO firstPoint = border.get(0);
        GeoPosition geoFirst = new GeoPosition(firstPoint.getLat(), firstPoint.getLon());
        Point2D ptFirst = map.getTileFactory().geoToPixel(geoFirst, map.getZoom());
        path.moveTo((float) ptFirst.getX(), (float) ptFirst.getY());

        // Add line segments
        for (int i = 1; i < border.size(); i++) {
            PositionPOJO pair0 = border.get(i);
            GeoPosition geoPosition0 = new GeoPosition(pair0.getLat(), pair0.getLon());
            Point2D pt0 = map.getTileFactory().geoToPixel(geoPosition0, map.getZoom());
            path.lineTo((float) pt0.getX(), (float) pt0.getY());
        }

        g.draw(path);
    }

    private void initLineList(JXMapViewer map) {
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

    public Color getSelColor() {
        return selColor;
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

    public BasicStroke getSelectedStroke() {
        return SELECTED_STROKE;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }
}
