package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.BorderTableModel;
import com.fx.swing.painter.BorderRoutePainter;
import com.fx.swing.pojo.BorderPOJO;
import com.fx.swing.tools.HelperFunctions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

public class BorderThreadHCM extends Thread implements ActionListener {
    
    private static final Logger _log = LogManager.getLogger(BorderThreadHCM.class);
    private final ProgressDialog progressDialog;
    private final File files[];
    private final JXMapViewer mapViewer;
    private final List<Painter<JXMapViewer>> painters;
    private boolean stop = false;
    private final JTable table;
    
    public BorderThreadHCM(ProgressDialog progressDialog, File files[], JXMapViewer mapViewer, List<Painter<JXMapViewer>> painters, JTable table) {
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
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(borderFile));
                
                byte[] stream = dataInputStream.readAllBytes();
                
                for (int k = 0; k < stream.length / 2; k += 8) {
                    
                    byte[] a = partition(stream, 0 + (k * 2), 8);
                    double lon = HelperFunctions.byteToDouble(a, Globals.BYTE_ORDER) * HelperFunctions.SF;
                    
                    byte[] b = partition(stream, 8 + (k * 2), 8);
                    double lat = HelperFunctions.byteToDouble(b, Globals.BYTE_ORDER) * HelperFunctions.SF;
                    
                    GeoPosition geoPosition = new GeoPosition(lat, lon);
                    track.add(geoPosition);
                }
                dataInputStream.close();
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
    
    private byte[] partition(byte[] a, int from, int length) {
        byte[] b = new byte[length];
        for (int k = 0; k < length; k++) {
            b[k] = a[from + k];
        }
        return b;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }
}
