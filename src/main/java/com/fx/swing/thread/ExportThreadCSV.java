package com.fx.swing.thread;

import com.fx.swing.Globals;
import com.fx.swing.dialog.ProgressDialog;
import com.fx.swing.model.BorderTableModel;
import com.fx.swing.pojo.BorderPOJO;
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

public class ExportThreadCSV extends Thread implements ActionListener {

    private static final Logger _log = LogManager.getLogger(ExportThreadCSV.class);
    private final ProgressDialog progressDialog;
    private boolean stop = false;
    private final JTable table;

    public ExportThreadCSV(ProgressDialog progressDialog, JTable table) {
        this.progressDialog = progressDialog;
        this.table = table;
    }

    @Override
    public void run() {
        List<BorderPOJO> list=((BorderTableModel) table.getModel()).getList();;
                
        for (int i = 0; i < list.size(); i++) {

            if (stop) {
                break;
            }

            BorderPOJO borderPOJO = list.get(i);

            if (borderPOJO.isActive()) {
                try {
                    String csvFileName = borderPOJO.getFileName() + ".csv";

                    File dir = new File(Globals.CSV_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(Globals.CSV_PATH + csvFileName));
                    borderPOJO.getBorderRoutePainter().getTrack().forEach(c -> {
                        try {
                            writer.write(c.getLongitude() + ";" + c.getLatitude() + "\n");
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == progressDialog.getButtonAbort()) {
            stop = true;
        }
    }
}
