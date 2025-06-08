package com.fx.swing.controller;

import com.formdev.flatlaf.FlatLaf;
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
import java.util.HashMap;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainController extends JPanel implements ActionListener {
    
    private HashMap<String, FlatLaf> lafMap;
    private final JFrame frame;
    private final ResourceBundle bundle;
    
    private JMenuBar menuBar;
    
    private JMenu menuFile;
    private JMenu menuView;
    private JMenu menuHelp;
    
    private JMenuItem miClose;
    private JMenuItem miAbout;
    private JMenuItem miLight;
    private JMenuItem miDark;
    
    private JPanel panelStatus;
    private JLabel labelAbout;
    private JLabel labelStatus;
    
    private JTabbedPane tabbedPane;
    
    public MainController(HashMap<String, FlatLaf> lafMap, JFrame frame, ResourceBundle bundle) {
        this.lafMap = lafMap;
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
        menuView = new JMenu(bundle.getString("menu.view"));
        
        miClose = new JMenuItem(bundle.getString("mi.close"));
        miAbout = new JMenuItem(bundle.getString("mi.about"));
        miLight = new JMenuItem(bundle.getString("mi.light"));
        miDark = new JMenuItem(bundle.getString("mi.dark"));
        
        miClose.addActionListener(this);
        miAbout.addActionListener(this);
        miLight.addActionListener(this);
        miDark.addActionListener(this);
        
        menuFile.add(miClose);
        menuView.add(miLight);
        menuView.add(miDark);
        menuHelp.add(miAbout);
        
        menuBar.add(menuFile);
        menuBar.add(menuView);
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
            if (e.getSource() == miLight) {
                switchLaf(Globals.THEME_LIGHT);
            }
            if (e.getSource() == miDark) {
                switchLaf(Globals.THEME_DARK);
            }
        }
    }
    
    private void switchLaf(String laf) {
        try {
            UIManager.setLookAndFeel(lafMap.get(laf));
            SwingUtilities.updateComponentTreeUI(frame);
            Globals.propman.put(Globals.THEME, laf);
            Globals.propman.save();
        } catch (Exception ex) {
            ex.printStackTrace();
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
