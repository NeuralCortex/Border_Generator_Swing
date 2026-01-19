package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.ScaleTableModel;
import com.fx.swing.pojo.InfoPOJO;
import com.fx.swing.pojo.ScalePOJO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScaleExportThreadCSV extends Thread implements ActionListener {

    private static final Logger _log = LogManager.getLogger(ScaleExportThreadCSV.class);
    private final ProgressDialog progressDialog;
    private boolean stop = false;
    private final JTable table;
    private final InfoPOJO infoPOJO;

    public ScaleExportThreadCSV(ProgressDialog progressDialog, JTable table, InfoPOJO infoPOJO) {
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
                        String csvFileName = infoPOJO.getValue() + "." + String.format("%03d", scalePOJO.getDistance()) + ".csv";
                        if (infoPOJO.getParam().equalsIgnoreCase("country")) {
                            csvFileName = infoPOJO.getCode() + "." + String.format("%03d", scalePOJO.getDistance()) + ".csv";
                        }

                        File dir = new File(Globals.CSV_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        BufferedWriter writer = new BufferedWriter(new FileWriter(Globals.CSV_PATH + csvFileName));
                        scalePOJO.getBorderPainter().getBorder().forEach(c -> {
                            try {
                                writer.write(c.getLon() + ";" + c.getLat() + "\n");
                            } catch (IOException ex) {
                                _log.error(ex.getMessage());

                            }
                        });
                        writer.close();
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
