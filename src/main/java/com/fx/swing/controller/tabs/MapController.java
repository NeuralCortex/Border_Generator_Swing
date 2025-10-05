package com.fx.swing.controller.tabs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fx.swing.Globals;
import com.fx.swing.controller.MainController;
import com.fx.swing.controller.PopulateInterface;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.InfoTableModel;
import com.fx.swing.model.ScaleTableModel;
import com.fx.swing.painter.BorderPainter;
import com.fx.swing.painter.PosPainter;
import com.fx.swing.pojo.InfoPOJO;
import com.fx.swing.pojo.PositionPOJO;
import com.fx.swing.pojo.ScalePOJO;
import com.fx.swing.pojo.TableHeaderPOJO;
import com.fx.swing.renderer.ColorRenderer;
import com.fx.swing.thread.ScaleThread;
import com.fx.swing.adapter.GeoSelectionAdapter;
import com.fx.swing.listener.MousePositionListener;
import com.fx.swing.thread.ScaleExportThreadCSV;
import com.fx.swing.thread.ScaleExportThreadHCM;
import com.fx.swing.tools.LayoutFunctions;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

public class MapController extends JPanel implements PopulateInterface, ActionListener {

    private MainController mainController;
    private ResourceBundle bundle;

    private final double lon = 10.671745101119196;
    private final double lat = 50.661742127393836;

    private JButton btnReset;
    private JButton btnCsvExport;
    private JButton btnHcmExport;

    private JTable tableInfo;
    private JTable tableScale;

    private final JXMapViewer mapViewer = new JXMapViewer();
    private final List<Painter<JXMapViewer>> painters = new ArrayList<>();
    private final HashMap<Integer, List<PositionPOJO>> mapBorder = new HashMap<>();
    private HashMap<Double, List<PositionPOJO>> mapLoad = new HashMap<>();
    private InfoPOJO infoPOJO;
    private GeoPosition marker;

    public MapController(MainController mainController) {
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

        JPanel panelInfo = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, new JLabel(bundle.getString("lb.geoinfo")));

        List<TableHeaderPOJO> headerList = new ArrayList<>();
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.param"), String.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.value"), String.class));

        InfoTableModel infoTableModel = new InfoTableModel(headerList, new ArrayList<>());
        tableInfo = new JTable(infoTableModel);
        tableInfo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableInfo.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int idx = tableInfo.getSelectedRow();
                if (idx != -1) {
                    infoPOJO = ((InfoTableModel) tableInfo.getModel()).getList().get(idx);

                    ((ScaleTableModel) tableScale.getModel()).getList().clear();
                    ((ScaleTableModel) tableScale.getModel()).fireTableDataChanged();
                    painters.clear();

                    mapBorder.clear();
                    mapLoad.clear();

                    CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                    mapViewer.setOverlayPainter(painter);
                    mapViewer.repaint();

                    loadPolygon(infoPOJO);
                }
            }
        });

        btnCsvExport = new JButton(bundle.getString("btn.csv.export"));
        btnHcmExport = new JButton(bundle.getString("btn.hcm.export"));
        btnCsvExport.addActionListener(this);
        btnHcmExport.addActionListener(this);

        JPanel panelScale = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, new JLabel(bundle.getString("lb.scale")), btnCsvExport, btnHcmExport);

        headerList = new ArrayList<>();
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.active"), Boolean.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.length"), Integer.class));
        headerList.add(new TableHeaderPOJO(bundle.getString("lb.color"), Color.class));

        ScaleTableModel scaleTableModel = new ScaleTableModel(headerList, new ArrayList<>());
        tableScale = new JTable(scaleTableModel);
        tableScale.setDefaultRenderer(Color.class, new ColorRenderer());

        JPanel panelRight = LayoutFunctions.createVerticalGridbag(panelInfo, tableInfo, panelScale, tableScale);
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(panelRight, BorderLayout.EAST);

        initOSM();
    }

    public static Color genRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mapViewer, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

        add(panel, BorderLayout.CENTER);

        initPainter();
    }

    private void loadPolygon(InfoPOJO address) {
        if (address != null) {
            mapBorder.clear();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String state = URLEncoder.encode(address.getValue(), StandardCharsets.UTF_8);
            String baseURL = "https://nominatim.openstreetmap.org/search?" + address.getParam().toLowerCase() + "=" + state + "&polygon_geojson=1&format=geojson";

            JsonNode root;
            try {
                root = objectMapper.readTree(new URL(baseURL));
                //System.out.println(root.toPrettyString());

                JsonNode feat = root.get("features");
                JsonNode geo = feat.get(0).get("geometry");
                JsonNode type = geo.get("type");

                //System.out.println("type: " + type.toString());
                if (type.toString().replace("\"", "").startsWith("Multi")) {
                    MultiPolygon multiPolygon = MultiPolygon.fromJson(geo.toString());
                    int idx = 0;
                    for (List<List<Point>> points : multiPolygon.coordinates()) {
                        int size = points.size();
                        //System.out.println("size: "+size);
                        for (int i = 0; i < size; i++) {
                            List<PositionPOJO> posList = new ArrayList<>();
                            //System.out.println("idx: "+idx+" size: "+points.get(i).size());
                            for (int j = 0; j < points.get(i).size(); j++) {
                                Point p = points.get(i).get(j);
                                posList.add(new PositionPOJO(p.longitude(), p.latitude()));
                            }
                            mapBorder.put(idx, posList);

                            idx++;
                        }
                    }
                } else {
                    Polygon polygon = Polygon.fromJson(geo.toString());
                    int idx = 0;
                    for (List<Point> points : polygon.coordinates()) {
                        List<PositionPOJO> posList = new ArrayList<>();
                        for (Point point : points) {
                            posList.add(new PositionPOJO(point.longitude(), point.latitude()));
                        }
                        mapBorder.put(idx, posList);

                        idx++;
                    }
                }
                int idx = 0;

                for (Integer key : mapBorder.keySet()) {
                    List<PositionPOJO> list = mapBorder.get(key);

                    if (isGeoPositionInsidePolygon(marker, list)) {
                        idx = key;
                        break;
                    }
                }

                initScaleTable(mapBorder.get(idx));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isGeoPositionInsidePolygon(GeoPosition point, List<PositionPOJO> polygonPoints) {
        // Validate input
        if (polygonPoints == null || polygonPoints.size() < 3) {
            System.out.println("Error: Invalid polygon (needs at least 3 points).");
            return false;
        }

        // Create JTS GeometryFactory
        GeometryFactory factory = new GeometryFactory();

        // Convert List<PositionPOJO> to JTS Coordinate array
        Coordinate[] coordinates = new Coordinate[polygonPoints.size() + 1];
        for (int i = 0; i < polygonPoints.size(); i++) {
            PositionPOJO pojo = polygonPoints.get(i);
            coordinates[i] = new Coordinate(pojo.getLon(), pojo.getLat());
        }
        // Close the polygon by repeating the first point
        coordinates[polygonPoints.size()] = coordinates[0];

        // Create a LinearRing and Polygon
        LinearRing ring = factory.createLinearRing(coordinates);
        org.locationtech.jts.geom.Polygon polygon = factory.createPolygon(ring, null);

        // Convert GeoPosition to JTS Point
        org.locationtech.jts.geom.Point jtsPoint = factory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));

        // Check if the point is inside the polygon
        return jtsPoint.within(polygon);
    }

    private void initScaleTable(List<PositionPOJO> border) {
        for (ScalePOJO scalePOJO : ((ScaleTableModel) tableScale.getModel()).getList()) {
            painters.remove(scalePOJO.getBorderPainter());
        }

        ((ScaleTableModel) tableScale.getModel()).getList().clear();
        ((ScaleTableModel) tableScale.getModel()).fireTableDataChanged();

        List<ScalePOJO> list = new ArrayList<>();
        Color color = genRandomColor();
        BorderPainter borderPainter = new BorderPainter(border, color);
        painters.add(borderPainter);
        list.add(new ScalePOJO(true, 0, color, borderPainter));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 6, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 15, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 30, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 40, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 50, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 60, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 80, color, null));
        color = genRandomColor();
        list.add(new ScalePOJO(false, 100, color, null));

        ((ScaleTableModel) tableScale.getModel()).setList(list);

        for (ScalePOJO scalePOJO : list) {
            scalePOJO.addPropertyChangeListener(e -> {
                if (e.getPropertyName().equalsIgnoreCase("active")) {
                    boolean newValue = (Boolean) e.getNewValue();
                    scale(newValue, scalePOJO);
                }
            });
        }

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private void scale(boolean n, ScalePOJO borderData) {
        if (n) {
            ScalePOJO border0 = null;
            ScaleTableModel scaleTableModel = (ScaleTableModel) tableScale.getModel();
            for (int i = 0; i < scaleTableModel.getList().size(); i++) {
                ScalePOJO data = scaleTableModel.getList().get(i);
                if (data.getDistance() == 0) {
                    border0 = data;
                }
            }

            if (borderData.getDistance() > 0) {
                if (mapLoad.containsKey((double) borderData.getDistance())) {
                    BorderPainter borderPainter = new BorderPainter(mapLoad.get((double) borderData.getDistance()), borderData.getColor());
                    borderData.setBorderPainter(borderPainter);
                    painters.add(borderPainter);
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("lb.scale"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, border0.getBorderPainter().getBorder().size() - 1);
                    ScaleThread scaleThread = new ScaleThread(progressDialog, border0, borderData);
                    scaleThread.setScaleTaskListener((List<PositionPOJO> list) -> {
                        BorderPainter borderPainter = new BorderPainter(list, borderData.getColor());
                        borderData.setBorderPainter(borderPainter);

                        painters.add(borderPainter);
                        mapLoad.put((double) borderData.getDistance(), list);

                        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                        mapViewer.setOverlayPainter(painter);
                        mapViewer.repaint();

                    });
                    new Thread(scaleThread).start();

                    progressDialog.setVisible(true);
                }
            }
            if (borderData.getDistance() == 0) {
                painters.add(borderData.getBorderPainter());
                mapLoad.put(0.0, borderData.getBorderPainter().getBorder());

            }
        } else {
            painters.remove(borderData.getBorderPainter());
        }

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }

    private void initPainter() {
        GeoSelectionAdapter geoSelectionAdapter = new GeoSelectionAdapter(mapViewer, painters);
        geoSelectionAdapter.setGeoSelectionAdapterListener((GeoPosition geoPosition) -> {
            PosPainter posPainter = new PosPainter(geoPosition);
            painters.clear();
            painters.add(posPainter);
            mapLoad.clear();

            ((InfoTableModel) tableInfo.getModel()).getList().clear();
            ((ScaleTableModel) tableScale.getModel()).getList().clear();
            ((InfoTableModel) tableInfo.getModel()).fireTableDataChanged();
            ((ScaleTableModel) tableScale.getModel()).fireTableDataChanged();

            getGeoInfos(geoPosition);
            marker = geoPosition;

            CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
            mapViewer.setOverlayPainter(painter);
            mapViewer.repaint();
        });
        mapViewer.addMouseListener(geoSelectionAdapter);
    }

    private void getGeoInfos(GeoPosition geoPosition) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String baseURL = "https://nominatim.openstreetmap.org/reverse?lat=" + geoPosition.getLatitude() + "&lon=" + geoPosition.getLongitude() + "&format=json&addressdetails=1&accept-language=en";
        JsonNode info;
        try {
            info = objectMapper.readTree(new URL(baseURL));
            JsonNode address = info.get("address");

            List<InfoPOJO> list = new ArrayList<>();
            ((InfoTableModel) tableInfo.getModel()).getList().clear();
            ((InfoTableModel) tableInfo.getModel()).fireTableDataChanged();

            for (Iterator<Map.Entry<String, JsonNode>> iter = address.fields(); iter.hasNext();) {
                Map.Entry entry = iter.next();
                String key = String.valueOf(entry.getKey());
                key = key.substring(0, 1).toUpperCase() + key.substring(1);
                String value = String.valueOf(entry.getValue());
                if (key.equalsIgnoreCase("country") || key.equalsIgnoreCase("state")) {
                    list.add(new InfoPOJO(key, value.replace("\"", "")));
                }
            }

            ((InfoTableModel) tableInfo.getModel()).setList(list);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private void exportCSV() {
        List<ScalePOJO> list = ((ScaleTableModel) tableScale.getModel()).getList();
        ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.csv.export"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, list.size() - 1);
        ScaleExportThreadCSV scaleExportThreadCSV = new ScaleExportThreadCSV(progressDialog, tableScale, infoPOJO);
        scaleExportThreadCSV.start();
        progressDialog.setVisible(true);
    }

    private void exportHCM() {
        List<ScalePOJO> list = ((ScaleTableModel) tableScale.getModel()).getList();
        ProgressDialog progressDialog = new ProgressDialog(mainController.getFrame(), bundle.getString("btn.hcm.export"), Dialog.ModalityType.APPLICATION_MODAL, bundle.getString("lb.cancel"), 0, list.size() - 1);
        ScaleExportThreadHCM scaleExportThreadHCM = new ScaleExportThreadHCM(progressDialog, tableScale, infoPOJO);
        scaleExportThreadHCM.start();
        progressDialog.setVisible(true);
    }

    private void resetAll() {
        ((InfoTableModel) tableInfo.getModel()).getList().clear();
        ((ScaleTableModel) tableScale.getModel()).getList().clear();
        ((InfoTableModel) tableInfo.getModel()).fireTableDataChanged();
        ((ScaleTableModel) tableScale.getModel()).fireTableDataChanged();

        painters.clear();
        mapLoad.clear();

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);
        mapViewer.repaint();
    }
}
