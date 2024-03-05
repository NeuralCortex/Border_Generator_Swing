package com.fx.swing;

import com.fx.swing.controller.MainController;
import com.fx.swing.themes.ThemeFX;
import com.fx.swing.tools.HelperFunctions;
import java.io.File;
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

    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.init();
    }

    private void init() {
        initLogger(Globals.LOG4J2_CONFIG_PATH);
        ThemeFX.setup();
        try {
            UIManager.setLookAndFeel(new ThemeFX());
        } catch (Exception ex) {
            _log.error(ex.getMessage());
        }

        JFrame frame = new JFrame(bundle.getString("app.name") + " - " + bundle.getString("app.version"));
        frame.setIconImage(new ImageIcon(Globals.APP_LOGO_PATH).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Globals.WIDTH, Globals.HEIGHT);

        MainController mainController = new MainController(frame, bundle);

        HelperFunctions.centerWindow(frame);

        frame.setVisible(true);
    }

    private void initLogger(String path) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(new File(path).toURI());
    }
}
