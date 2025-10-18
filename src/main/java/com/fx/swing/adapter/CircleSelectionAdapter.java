package com.fx.swing.adapter;

import com.fx.swing.Globals;
import com.fx.swing.painter.BorderLinePainter;
import com.fx.swing.painter.IntersectionPainter;
import com.fx.swing.painter.LinePainter;
import com.fx.swing.tools.HelperFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class CircleSelectionAdapter extends MouseAdapter {

    private static final Logger _log = LogManager.getLogger(CircleSelectionAdapter.class);
    private final JXMapViewer viewer;
    private final List<Painter<JXMapViewer>> painters;
    private int idx;
    private final BorderLinePainter.BORDER_TYPE border_type = BorderLinePainter.BORDER_TYPE.FROM;

    // Context menu
    private final JPopupMenu contextMenu;
    private final JMenuItem selectIntersectionItem;
    private final JMenuItem cutBorderItem;
    private boolean isStart = true;

    // Track intersection count for cut border enablement
    private int intersectionCount = 0;
    private BorderLinePainter fromPainter = null;
    private BorderLinePainter toPainter = null;

    public interface CircleSelectionAdapterListener {

        void drawCircle(GeoPosition geoPosition, BorderLinePainter.BORDER_TYPE border_type);

        void selectIntersection(boolean isStart, int idx) throws Exception;

        void cutBorder() throws Exception;

        void showErrorDlg();
    }

    private CircleSelectionAdapterListener circleSelectionAdapterListener;

    public CircleSelectionAdapter(JXMapViewer viewer, List<Painter<JXMapViewer>> painters,ResourceBundle bundle) {
        this.viewer = viewer;
        this.painters = painters;

        // Initialize context menu
        contextMenu = new JPopupMenu();
        selectIntersectionItem = new JMenuItem(bundle.getString("mi.select"),HelperFunctions.resizeIcon(new ImageIcon(CircleSelectionAdapter.class.getResource(Globals.PNG_SELECT)),16,16));
        cutBorderItem = new JMenuItem(bundle.getString("mi.cut"),HelperFunctions.resizeIcon(new ImageIcon(CircleSelectionAdapter.class.getResource(Globals.PNG_CUT)),16,16));

        selectIntersectionItem.addActionListener(this::handleSelectIntersection);
        cutBorderItem.addActionListener(this::handleCutBorder);

        contextMenu.add(selectIntersectionItem);
        contextMenu.add(cutBorderItem);

        // Initially disable cut border
        cutBorderItem.setEnabled(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Rectangle rect = viewer.getViewportBounds();
        double x = rect.getX() + e.getX();
        double y = rect.getY() + e.getY();

        GeoPosition pos = viewer.getTileFactory().pixelToGeo(new Point((int) x, (int) y), viewer.getZoom());
        circleSelectionAdapterListener.drawCircle(pos, border_type);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            // Show context menu on right-click
            contextMenu.show(viewer, e.getX(), e.getY());
        }
    }

    // Handle "Select intersection" menu item
    private void handleSelectIntersection(ActionEvent e) {
        try {
            if (idx >= 0) {
                circleSelectionAdapterListener.selectIntersection(isStart, idx);
                updateIntersectionState();
                isStart = !isStart;
            }
        } catch (Exception ex) {
            _log.error("Error selecting intersection: " + ex.getMessage(), ex);
            circleSelectionAdapterListener.showErrorDlg();
        }
    }

    // Handle "Cut border" menu item
    private void handleCutBorder(ActionEvent e) {
        try {
            if (intersectionCount == 2) {
                circleSelectionAdapterListener.cutBorder();
                // Reset after cut operation
                resetIntersectionState();
            }
        } catch (Exception ex) {
            _log.error("Error cutting border: " + ex.getMessage(), ex);
            circleSelectionAdapterListener.showErrorDlg();
        }
    }

    /**
     * Updates intersection state after selection Tracks FROM and TO painters to
     * determine if we have 2 intersections
     */
    private void updateIntersectionState() {
        // Find the selected painter at the current idx
        boolean isIntersection = false;
        for (int i = 0; i < painters.size(); i++) {
            Painter<JXMapViewer> painter = painters.get(i);
            if (painter instanceof BorderLinePainter borderLinePainter) {
                if (borderLinePainter.isIntersection() && borderLinePainter.getBorder_type() == BorderLinePainter.BORDER_TYPE.TO) {
                    isIntersection = true;
                }
            }
            if (painter instanceof IntersectionPainter intersectionPainter) {
                if (intersectionPainter.getLine_pos() == LinePainter.LINE_POS.START) {
                    intersectionCount = 1;
                } else if (intersectionPainter.getLine_pos() == LinePainter.LINE_POS.END) {
                    intersectionCount = 2;
                }
            }
        }

        // Enable cut border only when we have both FROM and TO intersections
        selectIntersectionItem.setEnabled(isIntersection && intersectionCount < 2);
        cutBorderItem.setEnabled(intersectionCount == 2);
    }

    /**
     * Reset intersection state after cut operation or when needed
     */
    private void resetIntersectionState() {
        intersectionCount = 0;
        fromPainter = null;
        toPainter = null;

        // Clear selected lines from painters
        if (fromPainter != null) {
            fromPainter.setSelLine(null);
            fromPainter.setSelColor(null);
        }
        if (toPainter != null) {
            toPainter.setSelLine(null);
            toPainter.setSelColor(null);
        }

        // Update menu state
        selectIntersectionItem.setEnabled(true);
        cutBorderItem.setEnabled(false);
    }

    public void setIdx(int idx) {
        this.idx = idx;
        updateIntersectionState();
    }

    public void setCircleSelectionAdapterListener(CircleSelectionAdapterListener circleSelectionAdapterListener) {
        this.circleSelectionAdapterListener = circleSelectionAdapterListener;
    }
}
