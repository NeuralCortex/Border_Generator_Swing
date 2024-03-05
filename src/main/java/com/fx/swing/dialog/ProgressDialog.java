package com.fx.swing.dialog;

import com.fx.swing.tools.HelperFunctions;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {

    private JProgressBar progressBar;
    private JLabel labelStart;
    private JLabel labelEnd;
    private JButton buttonAbort;

    public ProgressDialog(Window owner, String title, Dialog.ModalityType modalityType, String strAbort, int min, int max) {
        super(owner, title, modalityType);
        init(min, max, strAbort);
    }

    private void init(int min, int max, String strAbort) {
        setSize(400, 120);
        setResizable(false);
        HelperFunctions.centerWindow(this);
        setLayout(new BorderLayout());

        createProgressBar(min, max);
        createButtons(strAbort);
    }

    private void createProgressBar(int min, int max) {
        progressBar = new JProgressBar(min, max);
        progressBar.setStringPainted(true);
        labelStart = new JLabel(min + "");
        labelEnd = new JLabel(max + "");

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(labelStart);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(labelEnd);

        add(panel, BorderLayout.NORTH);
    }

    private void createButtons(String strAbort) {
        buttonAbort = new JButton(strAbort);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(Box.createHorizontalGlue());
        panel.add(buttonAbort);

        add(panel, BorderLayout.SOUTH);
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JButton getButtonAbort() {
        return buttonAbort;
    }
}
