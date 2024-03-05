package com.fx.swing.thread;

import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.BorderTableModel;
import com.fx.swing.painter.BorderRoutePainter;
import com.fx.swing.pojo.BorderPOJO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class BorderThreadCSV extends Thread implements ActionListener {

    private static final Logger _log = LogManager.getLogger(BorderThreadCSV.class);
    private final ProgressDialog progressDialog;
    private final File files[];
    private final JXMapViewer mapViewer;
    private final List<Painter<JXMapViewer>> painters;
    private boolean stop = false;
    private final JTable table;

    public BorderThreadCSV(ProgressDialog progressDialog, File files[], JXMapViewer mapViewer, List<Painter<JXMapViewer>> painters, JTable table) {
        this.progressDialog = progressDialog;
        this.files = files;
        this.mapViewer = mapViewer;
        this.painters = painters;
        this.table = table;
    }

    @Override
    public void run() {
        List<BorderPOJO> list = new ArrayList<>();
        BorderRoutePainter borderRoutePainter = null;

        for (int i = 0; i < files.length; i++) {
            if (stop) {
                break;
            }

            List<GeoPosition> track = new ArrayList<>();

            try {
                String borderFile = files[i].getAbsolutePath();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(borderFile));
                while (bufferedReader.ready()) {
                    String line[] = bufferedReader.readLine().trim().split("[\\s;]+");
                    double lonCsv = Double.valueOf(line[0]);
                    double latCsv = Double.valueOf(line[1]);
                    track.add(new GeoPosition(latCsv, lonCsv));
                }
                bufferedReader.close();
            } catch (Exception ex) {
                _log.error(ex.getMessage());
            }

            borderRoutePainter = new BorderRoutePainter(track);
            painters.add(borderRoutePainter);

            BorderPOJO borderPOJO = new BorderPOJO(files[i].getName(), true, borderRoutePainter, borderRoutePainter.getSize(), borderRoutePainter.getColor(), borderRoutePainter.getLength());

            borderPOJO.addPropertyChangeListener(e -> {
                if (e.getPropertyName().equalsIgnoreCase("active")) {
                    boolean newValue = (Boolean) e.getNewValue();
                    if (newValue == false) {
                        painters.remove(borderPOJO.getBorderRoutePainter());
                    } else {
                        painters.add(borderPOJO.getBorderRoutePainter());
                    }
                    CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
                    mapViewer.setOverlayPainter(painter);
                    mapViewer.repaint();
                }
            });

            ((BorderTableModel) table.getModel()).add(borderPOJO);
            progressDialog.getProgressBar().setValue(i);
        }

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);

        progressDialog.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }
}
