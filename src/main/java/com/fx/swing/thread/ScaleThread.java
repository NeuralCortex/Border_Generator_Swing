package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.pojo.PositionPOJO;
import com.fx.swing.pojo.ScalePOJO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.buffer.OffsetCurve;

public class ScaleThread extends Thread implements ActionListener {

    private final ProgressDialog progressDialog;
    private final ScalePOJO borderData0;
    private final ScalePOJO borderData;
    private boolean stop = false;

    public interface ScaleTaskListener {

        public void getScaledBorder(List<PositionPOJO> list);
    }
    private ScaleTaskListener scaleTaskListener;

    public ScaleThread(ProgressDialog progressDialog, ScalePOJO borderData0, ScalePOJO borderData) {
        this.progressDialog = progressDialog;
        this.borderData0 = borderData0;
        this.borderData = borderData;
    }

    @Override
    public void run() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue), Globals.WGS84_SRID);

        List<Coordinate> posList = new ArrayList<>();
        for (int i = 0; i < borderData0.getBorderPainter().getBorder().size(); i++) {
            PositionPOJO position = borderData0.getBorderPainter().getBorder().get(i);
            posList.add(new Coordinate(position.getLon(), position.getLat()));

            progressDialog.getProgressBar().setValue(i);
        }
        PositionPOJO position = borderData0.getBorderPainter().getBorder().get(0);
        posList.add(new Coordinate(position.getLon(), position.getLat()));

        Polygon polygon = geometryFactory.createPolygon(posList.stream().toArray(Coordinate[]::new));
        double dist = borderData.getDistance() / 100.0;

        Geometry geometry = OffsetCurve.getCurve(polygon, dist);
        //System.out.println("" + geometry.getNumGeometries());
        geometry = geometry.getGeometryN(0);

        List<PositionPOJO> scaleList = new ArrayList<>();
        Coordinate coordinates[] = geometry.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            if (stop) {
                break;
            }

            scaleList.add(new PositionPOJO(coordinates[i].x, coordinates[i].y));
        }

        scaleTaskListener.getScaledBorder(scaleList);

        progressDialog.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }

    public void setScaleTaskListener(ScaleTaskListener scaleTaskListener) {
        this.scaleTaskListener = scaleTaskListener;
    }
}
