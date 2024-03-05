package com.fx.swing.controller;

import com.fx.swing.Globals;
import com.fx.swing.controller.tabs.ConstController;
import com.fx.swing.controller.tabs.MapController;
import com.fx.swing.controller.tabs.ResultController;
import com.fx.swing.tools.HelperFunctions;
import com.fx.swing.tools.LayoutFunctions;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainController extends JPanel implements ActionListener {

    private final JFrame frame;
    private final ResourceBundle bundle;

    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuHelp;
    private JMenuItem miClose;
    private JMenuItem miAbout;

    private JPanel panelStatus;
    private JLabel labelAbout;
    private JLabel labelStatus;

    private JTabbedPane tabbedPane;

    public MainController(JFrame frame, ResourceBundle bundle) {
        this.frame = frame;
        this.bundle = bundle;

        init(bundle);
    }

    private void init(ResourceBundle bundle) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String about = MessageFormat.format(bundle.getString("lb.about"), String.format("%d", LocalDate.now().getYear()));
        labelAbout = new JLabel(about);
        labelStatus = new JLabel("");

        panelStatus = LayoutFunctions.createOptionPanelX(Globals.COLOR_BLUE, labelStatus, labelAbout);
        add(panelStatus, BorderLayout.SOUTH);

        frame.add(this);

        menuBar = new JMenuBar();

        menuFile = new JMenu(bundle.getString("menu.file"));
        menuHelp = new JMenu(bundle.getString("menu.help"));

        miClose = new JMenuItem(bundle.getString("mi.close"));
        miAbout = new JMenuItem(bundle.getString("mi.about"));

        miClose.addActionListener(this);
        miAbout.addActionListener(this);

        menuFile.add(miClose);
        menuHelp.add(miAbout);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        frame.setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(tabbedPane, BorderLayout.CENTER);

        HelperFunctions.addTab(tabbedPane, new MapController(this), bundle.getString("tab.border"));
        HelperFunctions.addTab(tabbedPane, new ConstController(this), bundle.getString("tab.const"));
        HelperFunctions.addTab(tabbedPane, new ResultController(this), bundle.getString("tab.result"));
        
        tabbedPane.addChangeListener(e -> {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            ((PopulateInterface) pane.getSelectedComponent()).clear();
            ((PopulateInterface) pane.getSelectedComponent()).reset();
            ((PopulateInterface) pane.getSelectedComponent()).populate();
        });

        tabbedPane.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            if (e.getSource() == miClose) {
                System.exit(0);
            }
            if (e.getSource() == miAbout) {
                showAboutDlg();
            }
        }
    }

    private void showAboutDlg() {
        String about = MessageFormat.format(bundle.getString("lb.about"), String.format("%d", LocalDate.now().getYear()));
        JOptionPane.showMessageDialog(frame, about, bundle.getString("lb.info"), JOptionPane.INFORMATION_MESSAGE);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JLabel getLabelStatus() {
        return labelStatus;
    }
}
