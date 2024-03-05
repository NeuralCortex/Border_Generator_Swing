package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.ScaleTableModel;
import com.fx.swing.pojo.InfoPOJO;
import com.fx.swing.pojo.PositionPOJO;
import com.fx.swing.pojo.ScalePOJO;
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

public class ScaleExportThreadHCM extends Thread implements ActionListener {

    private static final Logger _log = LogManager.getLogger(ScaleExportThreadHCM.class);
    private final ProgressDialog progressDialog;
    private boolean stop = false;
    private final JTable table;
    private final InfoPOJO infoPOJO;

    public ScaleExportThreadHCM(ProgressDialog progressDialog, JTable table, InfoPOJO infoPOJO) {
        this.progressDialog = progressDialog;
        this.table = table;
        this.infoPOJO = infoPOJO;
    }

    @Override
    public void run() {
        if (infoPOJO != null) {

            List<ScalePOJO> list = ((ScaleTableModel) table.getModel()).getList();

            for (int i = 0; i < list.size(); i++) {

                if (stop) {
                    break;
                }

                ScalePOJO scalePOJO = list.get(i);

                if (scalePOJO.isActive()) {
                    try {
                        String hcmFileName = infoPOJO.getValue() + "." + String.format("%03d", scalePOJO.getDistance());

                        File dir = new File(Globals.HCM_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(Globals.HCM_PATH + hcmFileName));
                        for (PositionPOJO position : scalePOJO.getBorderPainter().getBorder()) {
                            double corLon = position.getLon() / (180.0 / Math.PI);
                            double corLat = position.getLat() / (180.0 / Math.PI);

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }
}
