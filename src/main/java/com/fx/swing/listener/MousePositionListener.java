package com.fx.swing.listener;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;


public class MousePositionListener implements MouseMotionListener {

    private final JXMapViewer mapViewer;

    public interface GeoPosListener {

        public void getGeoPos(GeoPosition geoPosition);
    }

    public interface GeoBlockListener {

        public void getBlock(int block);
    }

    private GeoPosListener geoPosListener;
    private GeoBlockListener geoBlockListener;

    public MousePositionListener(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Rectangle rect = mapViewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();

        GeoPosition geoPosition = mapViewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), mapViewer.getZoom());
        geoPosListener.getGeoPos(geoPosition);
    }

    public void setGeoPosListener(GeoPosListener geoPosListener) {
        this.geoPosListener = geoPosListener;
    }

    public void setGeoBlockListener(GeoBlockListener geoBlockListener) {
        this.geoBlockListener = geoBlockListener;
    }
}
