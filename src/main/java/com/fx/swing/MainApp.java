package com.fx.swing;

import com.formdev.flatlaf.FlatLaf;
import com.fx.swing.controller.MainController;
import com.fx.swing.themes.ThemeFX_Dark;
import com.fx.swing.themes.ThemeFX_Light;
import com.fx.swing.tools.HelperFunctions;
import java.io.File;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class MainApp {

    private static final Logger _log = LogManager.getLogger(MainApp.class);
    private final ResourceBundle bundle = ResourceBundle.getBundle(Globals.BUNDLE_PATH, Globals.DEFAULT_LOCALE);
    private final HashMap<String, FlatLaf> lafMap = new HashMap<>();

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.init();
    }

    private void init() {
        initLogger(Globals.LOG4J2_CONFIG_PATH);
        ThemeFX_Dark.setup();

        lafMap.put(Globals.THEME_LIGHT, new ThemeFX_Light());
        lafMap.put(Globals.THEME_DARK, new ThemeFX_Dark());

        try {
            UIManager.setLookAndFeel(lafMap.get(Globals.propman.getProperty(Globals.THEME, Globals.THEME_DARK)));
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        //Version is null in NetBeans not in the packaged JAR
        JFrame frame = new JFrame(bundle.getString("app.name") + " - " + MainApp.class.getPackage().getImplementationVersion());
        frame.setIconImage(new ImageIcon(MainApp.class.getResource(Globals.APP_LOGO_PATH)).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Globals.WIDTH, Globals.HEIGHT);

        MainController mainController = new MainController(lafMap, frame, bundle);

        HelperFunctions.centerWindow(frame);

        frame.setVisible(true);
    }

    private void initLogger(String path) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(new File(path).toURI());
    }
}
