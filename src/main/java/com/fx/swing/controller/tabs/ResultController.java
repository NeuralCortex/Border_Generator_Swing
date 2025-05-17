package com.fx.swing.controller.tabs;

import com.fx.swing.Globals;
import com.fx.swing.controller.MainController;
import com.fx.swing.controller.PopulateInterface;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.listener.MousePopupListener;
import com.fx.swing.listener.MousePositionListener;
import com.fx.swing.model.BorderTableModel;
import com.fx.swing.painter.LinePainter;
import com.fx.swing.painter.PosPainter;
import com.fx.swing.pojo.BorderPOJO;
import com.fx.swing.pojo.TableHeaderPOJO;
import com.fx.swing.renderer.ColorRenderer;
import com.fx.swing.thread.BorderThreadCSV;
import com.fx.swing.thread.BorderThreadHCM;
import com.fx.swing.thread.ExportThreadCSV;
import com.fx.swing.thread.ExportThreadHCM;
import com.fx.swing.tools.LayoutFunctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.MouseInputListener;
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

public class ResultController extends JPanel implements PopulateInterface, ActionListener, ItemListener {

    private MainController mainController;
    private ResourceBundle bundle;

    private final double lon = 10.671745101119196;
    private final double lat = 50.661742127393836;

    private JButton btnReset;
    private JButton btnCSV;
    private JButton btnHCM;
    private JButton btnCsvExport;
    private JButton btnHcmExport;

    private JCheckBox cbInvertCSV;
    private JCheckBox cbInvertHCM;

    private JTable tableCSV;
    private JTable tableHCM;

    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();

    public ResultController(MainController mainController) {
        this.mainController = mainController;
        this.bundle = mainController.getBundle();

        init();
    }

    private void init() {
        setLayout(new BorderLayout());

        btnReset = new JButton(bundle.getString("btn.reset"));
        btnReset.addActionListener(this);
        JPanel panelTop = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, new JLabel(""), btnReset);
        add(panelTop, BorderLayout.NORTH);

        cbInvertCSV = new JCheckBox(bundle.getString("lb.invert"));
        btnCSV = new JButton(bundle.getString("btn.csv"));
        btnHcmExport = new JButton(bundle.getString("btn.hcm.export"));

        cbInvertCSV.addItemListener(this);
        btnCSV.addActionListener(this);
        btnHcmExport.addActionListener(this);

        JPanel panelCSV = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, new JLabel(bundle.getString("lb.csv")), cbInvertCSV, btnCSV, btnHcmExport);

        List<TableHeaderPOJO> headerList = new ArrayList<>();
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.filename"), String.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.active"), Boolean.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.coord"), String.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.length"), String.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.color"), Color.class));

        BorderTableModel borderTableModelCSV = new BorderTableModel(headerList, new ArrayList<>());
        tableCSV = new JTable(borderTableModelCSV);
        tableCSV.setDefaultRenderer(Color.class, new ColorRenderer());

        cbInvertHCM = new JCheckBox(bundle.getString("lb.invert"));
        btnHCM = new JButton(bundle.getString("btn.hcm"));
        btnCsvExport = new JButton(bundle.getString("btn.csv.export"));

        cbInvertHCM.addItemListener(this);
        btnHCM.addActionListener(this);
        btnCsvExport.addActionListener(this);

        JPanel panelHCM = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, new JLabel(bundle.getString("lb.hcm")), cbInvertHCM, btnHCM, btnCsvExport);

        BorderTableModel borderTableModelHCM = new BorderTableModel(headerList, new ArrayList<>());
        tableHCM = new JTable(borderTableModelHCM);
        tableHCM.setDefaultRenderer(Color.class, new ColorRenderer());

        JPanel panelRight = LayoutFunctions.createVerticalGridbag(panelCSV, tableCSV, panelHCM, tableHCM);
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(panelRight, BorderLayout.EAST);

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
            mainController.getLabelStatus().setText(bundle.getString("col.lon") + ": " + geoPosition.getLongitude() + " " + bundle.getString("col.lat") + ": " + geoPosition.getLatitude());
        });
        mapViewer.addMouseMotionListener(mousePositionListener);

        //Popup
        MousePopupListener mousePopupListener = new MousePopupListener(mapViewer);
        mousePopupListener.setGeoClipboard((GeoPosition geoPosition) -> {
            PosPainter posPainter = new PosPainter(geoPosition);
            painters.add(posPainter);
            
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
            
            drawLine();
            
            deleteLine();
        });
        mapViewer.addMouseListener(mousePopupListener);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mapViewer, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

        add(panel, BorderLayout.CENTER);
    }

    private void drawLine() {
        List<GeoPosition> geoPositions = new ArrayList<>();
        for (Painter painter : painters) {
            if (painter instanceof PosPainter) {
                PosPainter posPainter = (PosPainter) painter;
                geoPositions.add(posPainter.getGeoPosition());
            }
        }
        if (geoPositions.size() == 2) {
            LinePainter linePainter = new LinePainter(geoPositions.get(0), geoPositions.get(1));
            painters.add(linePainter);

            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
        }
    }

    private void deleteLine() {
        boolean isPresent = false;
        for (int i = 0; i < painters.size(); i++) {
            if (painters.get(i) instanceof LinePainter) {
                isPresent = true;
                break;
            }
        }
        for (int i = painters.size() - 1; i >= 0; i--) {
            Painter painter = painters.get(i);
            if (painter instanceof PosPainter) {
                if (isPresent) {
                    painters.remove(i);
                }
            }
            if (painter instanceof LinePainter) {
                if (isPresent) {
                    painters.remove(i);
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
            if (e.getSource() == btnHCM) {
                openHCM(bundle);
            }
            if (e.getSource() == btnCSV) {
                openCSV(bundle);
            }
            if (e.getSource() == btnCsvExport) {
                exportCSV();
            }
            if (e.getSource() == btnHcmExport) {
                exportHCM();
            }
            if (e.getSource() == btnReset) {
                resetAll();
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() instanceof JCheckBox) {
            if (e.getSource() == cbInvertHCM) {
                invertHCM();
            }
            if (e.getSource() == cbInvertCSV) {
                invertCSV();
            }
        }
    }

    private void resetAll() {
        ((BorderTableModel) tableCSV.getModel()).getList().clear();
        ((BorderTableModel) tableHCM.getModel()).getList().clear();
        ((BorderTableModel) tableCSV.getModel()).fireTableDataChanged();
        ((BorderTableModel) tableHCM.getModel()).fireTableDataChanged();

        cbInvertCSV.setSelected(false);
        cbInvertHCM.setSelected(false);

        painters.clear();

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private void exportCSV() {
        List<BorderPOJO> list = ((BorderTableModel) tableHCM.getModel()).getList();
        ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.csv.export"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, list.size() - 1);
        ExportThreadCSV exportThreadCSV = new ExportThreadCSV(progressDialog, tableHCM);
        exportThreadCSV.start();
        progressDialog.setVisible(true);
    }

    private void exportHCM() {
        List<BorderPOJO> list = ((BorderTableModel) tableCSV.getModel()).getList();
        ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.hcm.export"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, list.size() - 1);
        ExportThreadHCM exportThreadHCM = new ExportThreadHCM(progressDialog, tableCSV);
        exportThreadHCM.start();
        progressDialog.setVisible(true);
    }

    private void invertCSV() {
        List<BorderPOJO> list = ((BorderTableModel) tableCSV.getModel()).getList();
        for (int i = 0; i < list.size(); i++) {
            BorderPOJO borderPOJO = list.get(i);
            if (borderPOJO.isActive()) {
                borderPOJO.setActive(false);
            } else {
                borderPOJO.setActive(true);
            }
        }
        ((BorderTableModel) tableCSV.getModel()).fireTableDataChanged();
    }

    private void invertHCM() {
        List<BorderPOJO> list = ((BorderTableModel) tableHCM.getModel()).getList();
        for (int i = 0; i < list.size(); i++) {
            BorderPOJO borderPOJO = list.get(i);
            if (borderPOJO.isActive()) {
                borderPOJO.setActive(false);
            } else {
                borderPOJO.setActive(true);
            }
        }
        ((BorderTableModel) tableHCM.getModel()).fireTableDataChanged();
    }

    private void openCSV(ResourceBundle bundle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(Globals.propman.getProperty(Globals.DIR_CSV_INPUT, System.getProperty("user.dir"))));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File files[] = fileChooser.getSelectedFiles();
            if (files.length > 0) {
                Globals.propman.put(Globals.DIR_CSV_INPUT, files[0].getParent());
                Globals.propman.save();
            }

            ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.csv"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, files.length - 1);
            BorderThreadCSV borderThreadCSV = new BorderThreadCSV(progressDialog, files, mapViewer, painters, tableCSV);
            borderThreadCSV.start();
            progressDialog.setVisible(true);

        }
    }

    private void openHCM(ResourceBundle bundle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(Globals.propman.getProperty(Globals.DIR_HCM_INPUT, System.getProperty("user.dir"))));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File files[] = fileChooser.getSelectedFiles();
            if (files.length > 0) {
                Globals.propman.put(Globals.DIR_HCM_INPUT, files[0].getParent());
                Globals.propman.save();
            }

            ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.hcm"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, files.length - 1);
            BorderThreadHCM borderThreadHCM = new BorderThreadHCM(progressDialog, files, mapViewer, painters, tableHCM);
            borderThreadHCM.start();
            progressDialog.setVisible(true);

        }
    }

}
