package com.fx.swing.adapter;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class GeoSelectionAdapter extends MouseAdapter implements ActionListener {

    private static final Logger _log = LogManager.getLogger(GeoSelectionAdapter.class);
    private final JXMapViewer viewer;
    private final List<Painter<JXMapViewer>> painters;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemPos;

    private GeoPosition geoPosition;

    public interface GeoSelectionAdapterListener {

        public void setGeoPoint(GeoPosition geoPosition);
    }
    private GeoSelectionAdapterListener geoSelectionAdapterListener;

    public GeoSelectionAdapter(JXMapViewer viewer, List<Painter<JXMapViewer>> painters) {
        this.viewer = viewer;
        this.painters = painters;
        init();
    }

    private void init() {
        popupMenu = new JPopupMenu();
        menuItemPos = new JMenuItem("Set Geo Position");
        menuItemPos.addActionListener(this);

        try {
            ImageIcon iconAdd = new ImageIcon(ImageIO.read(new File(System.getProperty("user.dir") + "/images/plus.png")));
            menuItemPos.setIcon(iconAdd);
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }
        popupMenu.add(menuItemPos);
    }

    private void showPopup(MouseEvent e) {
        //if (e.isPopupTrigger() && e.isControlDown()) {
        if (e.isPopupTrigger()) {
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Rectangle rect = viewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();

        geoPosition = viewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), viewer.getZoom());

        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemPos) {
            geoSelectionAdapterListener.setGeoPoint(geoPosition);
        }
    }

    public void setGeoSelectionAdapterListener(GeoSelectionAdapterListener geoSelectionAdapterListener) {
        this.geoSelectionAdapterListener = geoSelectionAdapterListener;
    }
}
