package com.fx.swing.controller.tabs;

import com.fx.swing.Globals;
import com.fx.swing.adapter.CircleSelectionAdapter;
import com.fx.swing.controller.MainController;
import com.fx.swing.controller.PopulateInterface;
import com.fx.swing.listener.MousePositionListener;
import com.fx.swing.painter.BorderLinePainter;
import com.fx.swing.painter.CirclePainter;
import com.fx.swing.painter.IntersectionPainter;
import com.fx.swing.painter.LinePainter;
import com.fx.swing.painter.SelectionLinePainter;
import com.fx.swing.pojo.LinePOJO;
import com.fx.swing.pojo.PositionPOJO;
import com.fx.swing.tools.HelperFunctions;
import com.fx.swing.tools.LayoutFunctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.locationtech.jts.algorithm.Intersection;
import org.locationtech.jts.geom.Coordinate;

public class ConstController extends JPanel implements PopulateInterface, ActionListener, ItemListener {

    private static final Logger _log = LogManager.getLogger(ConstController.class);
    private final MainController mainController;
    private final ResourceBundle bundle;

    private final double lon = 10.671745101119196;
    private final double lat = 50.661742127393836;

    private JLabel lbFrom;
    private JLabel lbTo;

    private JButton btnFrom;
    private JButton btnTo;
    private JButton btnReset;
    private JButton btnCSV;
    private JButton btnHCM;

    private JCheckBox cbInvert;

    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();
    private CircleSelectionAdapter cirlceSelectionAdapter;
    private Line2D lineFrom;

    public ConstController(MainController mainController) {
        this.mainController = mainController;
        this.bundle = mainController.getBundle();

        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        btnFrom = new JButton(bundle.getString("btn.from"));
        btnTo = new JButton(bundle.getString("btn.to"));
        cbInvert = new JCheckBox(bundle.getString("lb.invert"));
        btnCSV = new JButton(bundle.getString("btn.csv.export"));
        btnHCM = new JButton(bundle.getString("btn.hcm.export"));
        btnReset = new JButton(bundle.getString("btn.reset"));

        btnCSV.setEnabled(false);
        btnHCM.setEnabled(false);

        btnReset.addActionListener(this);
        btnCSV.addActionListener(this);
        btnFrom.addActionListener(this);
        btnTo.addActionListener(this);
        btnHCM.addActionListener(this);
        cbInvert.addItemListener(this);

        JSeparator separator0 = new JSeparator(SwingConstants.VERTICAL);
        separator0.setMaximumSize(new Dimension(0, 30));

        JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setMaximumSize(new Dimension(0, 30));

        JPanel panelDesc = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panelDesc, BoxLayout.X_AXIS);
        panelDesc.setLayout(boxLayout);
        panelDesc.setBackground(Globals.COLOR_BLUE);

        lbFrom = new JLabel();
        lbTo = new JLabel();

        lbFrom.setForeground(Color.WHITE);
        lbTo.setForeground(Color.WHITE);

        panelDesc.add(lbFrom);
        panelDesc.add(Box.createRigidArea(new Dimension(10, 0)));
        panelDesc.add(lbTo);

        JPanel panelTop = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, panelDesc, btnFrom, btnTo, separator0, btnReset, cbInvert, separator1, btnCSV, btnHCM);
        add(panelTop, BorderLayout.NORTH);

        initOSM();
    }

    private void initOSM() {
        TileFactoryInfo tileFactoryInfo = new OSMTileFactoryInfo();
        DefaultTileFactory defaultTileFactory = new DefaultTileFactory(tileFactoryInfo);
        defaultTileFactory.setThreadPoolSize(Runtime.getRuntime().availableProcessors());
        mapViewer.setTileFactory(defaultTileFactory);

        final JLabel labelAttr = new JLabel();
        mapViewer.setLayout(new BorderLayout());
        mapViewer.add(labelAttr, BorderLayout.SOUTH);
        labelAttr.setText(defaultTileFactory.getInfo().getAttribution() + " - " + defaultTileFactory.getInfo().getLicense());

        // Set the focus
        GeoPosition city = new GeoPosition(lat, lon);

        mapViewer.setZoom(14);
        mapViewer.setAddressLocation(city);

        // Add interactions
        MouseInputListener mil = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mil);
        mapViewer.addMouseMotionListener(mil);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        MousePositionListener mousePositionListener = new MousePositionListener(mapViewer);
        mousePositionListener.setGeoPosListener((GeoPosition geoPosition) -> {
            String lat = String.format("%.5f", geoPosition.getLatitude());
            String lon = String.format("%.5f", geoPosition.getLongitude());
            mainController.getLabelStatus().setText(bundle.getString("col.lat") + ": " + lat + " " + bundle.getString("col.lon") + ": " + lon);
        });
        mapViewer.addMouseMotionListener(mousePositionListener);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mapViewer, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(panel, BorderLayout.CENTER);

        initPainter();
    }

    private void openBorderFile(ResourceBundle bundle, boolean from) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        FileFilter filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        fileChooser.setFileFilter(filter);
        String borderDir = Globals.CSV_PATH;

        File dir = new File(Globals.CSV_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        fileChooser.setCurrentDirectory(new File(borderDir));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File borderFile = fileChooser.getSelectedFile();

            if (borderFile != null) {

                List<PositionPOJO> posList = new ArrayList<>();

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(borderFile))) {
                    String line;
                    int idx = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] parts = line.split(";", 2); // Limit split to 2 parts
                        double lonCsv = Double.parseDouble(parts[0].trim());
                        double latCsv = Double.parseDouble(parts[1].trim());
                        posList.add(new PositionPOJO(lonCsv, latCsv, idx++));
                    }
                } catch (IOException | NumberFormatException ex) {
                    _log.error(ex.getLocalizedMessage());
                }

                if (from) {
                    lbFrom.setText(bundle.getString("btn.from") + ": " + borderFile.getName());
                    BorderLinePainter borderLinePainter = new BorderLinePainter(posList, Color.BLACK, BorderLinePainter.BORDER_TYPE.FROM);
                    painters.add(borderLinePainter);
                } else {
                    lbTo.setText(bundle.getString("btn.to") + ": " + borderFile.getName());
                    // Remove TO painters safely
                    Iterator<Painter<JXMapViewer>> iterator = painters.iterator();
                    while (iterator.hasNext()) {
                        Painter painter = iterator.next();
                        if (painter instanceof BorderLinePainter blp) {
                            if (blp.getBorder_type() == BorderLinePainter.BORDER_TYPE.TO) {
                                iterator.remove();
                            }
                        }
                    }
                    BorderLinePainter borderLinePainter = new BorderLinePainter(posList, Color.BLACK, BorderLinePainter.BORDER_TYPE.TO);
                    painters.add(borderLinePainter);
                }

                CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                mapViewer.setOverlayPainter(painter);
                mapViewer.repaint();
            }
        }

        mapViewer.requestFocusInWindow();
    }

    private void initPainter() {
        CirclePainter circlePainter = new CirclePainter();
        circlePainter.setCirclePainterListener((Ellipse2D ellipse2D) -> {
            // Reset previous selections first
            resetPreviousSelections();

            // First pass: Find FROM lines by middle point containment
            for (int i = 0; i < painters.size(); i++) {
                Painter painter = painters.get(i);
                if (painter instanceof BorderLinePainter borderLinePainter) {
                    if (borderLinePainter.getLineList() != null && borderLinePainter.getBorder_type() == BorderLinePainter.BORDER_TYPE.FROM) {
                        for (LinePOJO line : borderLinePainter.getLineList()) {
                            Point2D middlePoint = line.getMiddle();
                            if (ellipse2D.contains(middlePoint.getX(), middlePoint.getY())) {
                                // Select FROM line (RED)
                                borderLinePainter.setSelLine(line.getLine2D());
                                borderLinePainter.setSelColor(Color.RED);
                                lineFrom = line.getLine2D();
                                cirlceSelectionAdapter.setIdx(line.getIdx());
                                break; // Only select one FROM line
                            }
                        }
                    }
                }
            }

            // Second pass: Find TO lines that intersect with selected lineFrom
            if (lineFrom != null) {
                for (int i = 0; i < painters.size(); i++) {
                    Painter painter = painters.get(i);
                    if (painter instanceof BorderLinePainter borderLinePainter) {
                        if (borderLinePainter.getLineList() != null && borderLinePainter.getBorder_type() == BorderLinePainter.BORDER_TYPE.TO) {
                            for (LinePOJO line : borderLinePainter.getLineList()) {
                                if (lineFrom.intersectsLine(line.getLine2D())) {
                                    // Select TO line that intersects (BLUE)
                                    borderLinePainter.setSelLine(line.getLine2D());
                                    borderLinePainter.setSelColor(Color.BLUE);
                                    borderLinePainter.setIntersection(true);
                                    break; // Only select one intersecting TO line
                                } else {
                                    borderLinePainter.setIntersection(false);
                                }
                            }
                        }
                    }
                }
            }
        });

        painters.add(circlePainter);

        cirlceSelectionAdapter = new CircleSelectionAdapter(mapViewer, painters, bundle);
        cirlceSelectionAdapter.setCircleSelectionAdapterListener(new CircleSelectionAdapter.CircleSelectionAdapterListener() {
            @Override
            public void drawCircle(GeoPosition geoPosition, BorderLinePainter.BORDER_TYPE border_type) {

                for (int i = 0; i < painters.size(); i++) {
                    Painter painter = painters.get(i);
                    if (painter instanceof CirclePainter circlePainter) {
                        circlePainter.setGeoPosition(geoPosition);
                        mapViewer.repaint();
                    }
                }
            }

            @Override
            public void showErrorDlg() {
                //TODO Error-Dlg
            }

            @Override
            public void selectIntersection(boolean isStart, int idx) throws Exception {
                drawIntersection(isStart, idx);
            }

            @Override
            public void cutBorder() throws Exception {
                int start = 0;
                int end = 0;

                GeoPosition geoPositionStart = null;
                GeoPosition geoPositionEnd = null;

                List<PositionPOJO> posList = null;

                for (int i = 0; i < painters.size(); i++) {
                    Painter painter = painters.get(i);

                    if (painter instanceof IntersectionPainter intersectionPainter) {
                        switch (intersectionPainter.getLine_pos()) {
                            case START -> {
                                geoPositionStart = intersectionPainter.getGeoPosition();
                                start = intersectionPainter.getIdx();
                            }
                            case END -> {
                                geoPositionEnd = intersectionPainter.getGeoPosition();
                                end = intersectionPainter.getIdx();
                            }
                            default ->
                                throw new AssertionError();
                        }
                    }

                    if (painter instanceof BorderLinePainter borderLinePainter) {
                        switch (borderLinePainter.getBorder_type()) {
                            case FROM ->
                                posList = borderLinePainter.getBorder();
                            case TO -> {
                            }
                            default -> {
                            }
                        }
                        //throw new AssertionError();
                    }
                }

                for (int i = painters.size() - 1; i >= 0; i--) {
                    if (!(painters.get(i) instanceof CirclePainter)) {
                        painters.remove(i);
                    }
                }

                SelectionLinePainter selectionLinePainter = new SelectionLinePainter(posList, start, end, posList, geoPositionStart, geoPositionEnd);
                painters.add(selectionLinePainter);

                CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                mapViewer.setOverlayPainter(painter);
                mapViewer.repaint();

                btnCSV.setEnabled(true);
                btnHCM.setEnabled(true);
            }
        });

        cbInvert.setSelected(false);
        btnCSV.setEnabled(false);
        btnHCM.setEnabled(false);

        mapViewer.setFocusable(true);
        mapViewer.addMouseListener(cirlceSelectionAdapter);
        mapViewer.addMouseMotionListener(cirlceSelectionAdapter);

        // Update map
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();

    }

    private void resetPreviousSelections() {
        for (Painter painter : painters) {
            if (painter instanceof BorderLinePainter borderLinePainter) {
                borderLinePainter.setSelLine(null);
                borderLinePainter.setSelColor(null); // or default color
            }
        }
        lineFrom = null;
        cirlceSelectionAdapter.setIdx(-1); // or appropriate default
    }

    private void drawIntersection(boolean isStart, int idx) {
        GeoPosition geoPosition1 = null;
        GeoPosition geoPosition2 = null;
        GeoPosition geoPosition3 = null;
        GeoPosition geoPosition4 = null;

        for (int i = painters.size() - 1; i >= 0; i--) {
            Painter painter = painters.get(i);
            if (painter instanceof BorderLinePainter borderLinePainter) {
                switch (borderLinePainter.getBorder_type()) {
                    case FROM -> {
                        geoPosition1 = borderLinePainter.getGeoPositionLine1();
                        geoPosition2 = borderLinePainter.getGeoPositionLine2();
                    }
                    case TO -> {
                        geoPosition3 = borderLinePainter.getGeoPositionLine1();
                        geoPosition4 = borderLinePainter.getGeoPositionLine2();
                    }
                    default ->
                        throw new AssertionError();
                }
            }
        }

        Coordinate c1 = new Coordinate(geoPosition1.getLongitude(), geoPosition1.getLatitude());
        Coordinate c2 = new Coordinate(geoPosition2.getLongitude(), geoPosition2.getLatitude());
        Coordinate c3 = new Coordinate(geoPosition3.getLongitude(), geoPosition3.getLatitude());
        Coordinate c4 = new Coordinate(geoPosition4.getLongitude(), geoPosition4.getLatitude());

        Coordinate inter1 = Intersection.intersection(c1, c2, c3, c4);

        IntersectionPainter intersectionPainter;
        if (isStart) {
            intersectionPainter = new IntersectionPainter(new GeoPosition(inter1.getY(), inter1.getX()), LinePainter.LINE_POS.START, idx);
        } else {
            intersectionPainter = new IntersectionPainter(new GeoPosition(inter1.getY(), inter1.getX()), LinePainter.LINE_POS.END, idx);
        }

        painters.add(intersectionPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private void openCsvSaveDialog(ResourceBundle bundle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        FileFilter filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
        fileChooser.setFileFilter(filter);
        String borderDir = Globals.CSV_PATH;

        File dir = new File(Globals.CSV_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        fileChooser.setCurrentDirectory(new File(borderDir));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File borderFile = fileChooser.getSelectedFile();
            if (borderFile != null) {
                try {
                    for (int i = 0; i < painters.size(); i++) {
                        Painter painter = painters.get(i);
                        if (painter instanceof SelectionLinePainter selectionLinePainter) {

                            List<PositionPOJO> list = selectionLinePainter.getFullBorder();

                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(borderFile));
                            for (PositionPOJO position : list) {
                                bufferedWriter.write(position.getLon() + ";" + position.getLat() + "\n");
                            }
                            bufferedWriter.close();
                        }
                    }
                } catch (Exception ex) {
                    _log.error(ex.getMessage());
                }
            }
        }
    }

    private void openHcmSaveDialog(ResourceBundle bundle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);

        String borderDir = Globals.HCM_PATH;

        File dir = new File(Globals.HCM_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        fileChooser.setCurrentDirectory(new File(borderDir));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File borderFile = fileChooser.getSelectedFile();
            if (borderFile != null) {
                try {
                    for (int i = 0; i < painters.size(); i++) {
                        Painter painter = painters.get(i);
                        if (painter instanceof SelectionLinePainter selectionLinePainter) {

                            List<PositionPOJO> list = selectionLinePainter.getFullBorder();

                            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(borderFile));
                            for (PositionPOJO position : list) {
                                double corLon = position.getLon() / (180.0 / Math.PI);
                                double corLat = position.getLat() / (180.0 / Math.PI);

                                byte[] bytesLon = new byte[8];
                                byte[] bytesLat = new byte[8];

                                dataOutputStream.write(HelperFunctions.doubleToByte(corLon, Globals.BYTE_ORDER));
                                dataOutputStream.write(HelperFunctions.doubleToByte(corLat, Globals.BYTE_ORDER));
                            }
                            dataOutputStream.close();
                        }
                    }
                } catch (Exception ex) {
                    _log.error(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void populate() {

    }

    @Override
    public void reset() {
        resetAll();
    }

    @Override
    public void clear() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            if (e.getSource() == btnReset) {
                resetAll();
            }
            if (e.getSource() == btnFrom) {
                openBorderFile(bundle, true);
            }
            if (e.getSource() == btnTo) {
                openBorderFile(bundle, false);
            }
            if (e.getSource() == btnCSV) {
                openCsvSaveDialog(bundle);
            }
            if (e.getSource() == btnHCM) {
                openHcmSaveDialog(bundle);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof JCheckBox) {
            if (e.getSource() == cbInvert) {
                invert(cbInvert.isSelected());
            }
        }
    }

    private void invert(boolean n) {
        for (int i = painters.size() - 1; i >= 0; i--) {
            Painter painter = painters.get(i);
            if (painter instanceof SelectionLinePainter) {
                SelectionLinePainter selectionLinePainter = (SelectionLinePainter) painter;
                selectionLinePainter.setInvert(n);
            }
        }
        mapViewer.repaint();
    }

    private void resetAll() {
        cbInvert.setSelected(false);

        btnCSV.setEnabled(false);
        btnHCM.setEnabled(false);

        lbFrom.setText("");
        lbTo.setText("");

        for (int i = painters.size() - 1; i >= 0; i--) {
            if (!(painters.get(i) instanceof CirclePainter)) {
                painters.remove(i);
            }
        }

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }
}
