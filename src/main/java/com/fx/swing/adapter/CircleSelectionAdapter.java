package com.fx.swing.adapter;

import com.fx.swing.painter.BorderLinePainter;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class CircleSelectionAdapter extends MouseAdapter implements ActionListener, KeyListener {

    private static final Logger _log = LogManager.getLogger(CircleSelectionAdapter.class);
    private final JXMapViewer viewer;
    private List<Painter<JXMapViewer>> painters;
    private int idx;
    private BorderLinePainter.BORDER_TYPE border_type = BorderLinePainter.BORDER_TYPE.FROM;

    public interface CirlceSelectionAdapterListener {

        public void drawCircle(GeoPosition geoPosition, BorderLinePainter.BORDER_TYPE border_type);

        public void drawStartLine1(int start) throws Exception;

        public void drawStartLine2() throws Exception;

        public void drawEndLine1(int end) throws Exception;

        public void drawEndLine2() throws Exception;

        public void drawFullResBorder() throws Exception;

        public void showErrorDlg();
    }

    private CirlceSelectionAdapterListener cirlceSelectionAdapterListener;

    public CircleSelectionAdapter(JXMapViewer viewer, List<Painter<JXMapViewer>> painters) {
        this.viewer = viewer;
        this.painters = painters;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Rectangle rect = viewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();

        GeoPosition pos = viewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), viewer.getZoom());
        cirlceSelectionAdapterListener.drawCircle(pos, border_type);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_1) {
            try {
                border_type = BorderLinePainter.BORDER_TYPE.FROM;
                cirlceSelectionAdapterListener.drawStartLine1(idx);
                border_type = BorderLinePainter.BORDER_TYPE.TO;
                cirlceSelectionAdapterListener.drawStartLine2();
            } catch (Exception ex) {
                _log.error(ex.getMessage());
                cirlceSelectionAdapterListener.showErrorDlg();
            }
        }
        if (ke.getKeyCode() == KeyEvent.VK_2) {
            try {
                border_type = BorderLinePainter.BORDER_TYPE.FROM;
                cirlceSelectionAdapterListener.drawEndLine1(idx);
                border_type = BorderLinePainter.BORDER_TYPE.TO;
                cirlceSelectionAdapterListener.drawEndLine2();
            } catch (Exception ex) {
                _log.error(ex.getMessage());
                cirlceSelectionAdapterListener.showErrorDlg();
            }
        }
        if (ke.getKeyCode() == KeyEvent.VK_3) {
            try {
                cirlceSelectionAdapterListener.drawFullResBorder();
            } catch (Exception ex) {
                _log.error(ex.getMessage());
                cirlceSelectionAdapterListener.showErrorDlg();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setBorder_type(BorderLinePainter.BORDER_TYPE border_type) {
        this.border_type = border_type;
    }

    public void setCirlceSelectionAdapterListener(CirlceSelectionAdapterListener cirlceSelectionAdapterListener) {
        this.cirlceSelectionAdapterListener = cirlceSelectionAdapterListener;
    }
}
