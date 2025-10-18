package com.fx.swing.listener;

import com.fx.swing.Globals;
import com.fx.swing.adapter.GeoSelectionAdapter;
import com.fx.swing.tools.HelperFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

public class MousePopupListener implements MouseListener, ActionListener {

    private static final Logger _log = LogManager.getLogger(MousePopupListener.class);
    private final JXMapViewer mapViewer;
    private JPopupMenu popupMenu;
    private JMenuItem menuItemA;
    private GeoPosition geoPosition;

    public interface GeoClipboardListener {

        public void setPoint(GeoPosition geoPosition);
    }
    private GeoClipboardListener geoClipboard;

    public MousePopupListener(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        init();
    }

    private void init() {
        popupMenu = new JPopupMenu();
        menuItemA = new JMenuItem("Set Point");

        try {
            Icon iconAdd = HelperFunctions.resizeIcon(new ImageIcon(GeoSelectionAdapter.class.getResource(Globals.PNG_PLUS)), 16, 16);
            menuItemA.setIcon(iconAdd);
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        menuItemA.addActionListener(this);
        popupMenu.add(menuItemA);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(menuItemA)) {
            geoClipboard.setPoint(geoPosition);
        }
    }

    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Rectangle rect = mapViewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();
        geoPosition = mapViewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), mapViewer.getZoom());

        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void setGeoClipboard(GeoClipboardListener geoClipboard) {
        this.geoClipboard = geoClipboard;
    }
}
