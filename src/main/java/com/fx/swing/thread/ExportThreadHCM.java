package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.BorderTableModel;
import com.fx.swing.pojo.BorderPOJO;
import com.fx.swing.tools.HelperFunctions;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.viewer.GeoPosition;

public class ExportThreadHCM extends Thread implements ActionListener {
    
    private static final Logger _log = LogManager.getLogger(ExportThreadHCM.class);
    private final ProgressDialog progressDialog;
    private boolean stop = false;
    private final JTable table;
    
    public ExportThreadHCM(ProgressDialog progressDialog, JTable table) {
        this.progressDialog = progressDialog;
        this.table = table;
    }
    
    @Override
    public void run() {
        List<BorderPOJO> list = ((BorderTableModel) table.getModel()).getList();
        
        for (int i = 0; i < list.size(); i++) {
            
            if (stop) {
                break;
            }
            
            BorderPOJO borderPOJO = list.get(i);
            
            if (borderPOJO.isActive()) {
                try {
                    String fileName = borderPOJO.getFileName().split("\\.")[0];
                    String dist = borderPOJO.getFileName().split("\\.")[1];
                    String hcmFileName = fileName + "." + dist;
                    
                    File dir = new File(Globals.HCM_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    
                    DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(Globals.HCM_PATH + hcmFileName));
                    for (GeoPosition geoPosition : borderPOJO.getBorderRoutePainter().getTrack()) {
                        double corLon = geoPosition.getLongitude() / (180.0 / Math.PI);
                        double corLat = geoPosition.getLatitude() / (180.0 / Math.PI);
                        
                        byte[] bytesLon = new byte[8];
                        byte[] bytesLat = new byte[8];
                        
                        dataOutputStream.write(HelperFunctions.doubleToByte(corLon, Globals.BYTE_ORDER));
                        dataOutputStream.write(HelperFunctions.doubleToByte(corLat, Globals.BYTE_ORDER));
                    }
                    dataOutputStream.close();
                } catch (Exception ex) {
                    _log.error(ex.getMessage());
                }
            }
            progressDialog.getProgressBar().setValue(i);
        }
        
        progressDialog.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }
}
