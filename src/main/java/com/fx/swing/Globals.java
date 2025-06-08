package com.fx.swing;

import com.fx.swing.tools.HelperFunctions;
import com.fx.swing.tools.XMLPropertyManager;
import java.awt.Color;
import java.nio.ByteOrder;
import java.util.Locale;

public class Globals {

    public static final int HEIGHT = 900;//900.0f;
    public static final int WIDTH = (int) (HEIGHT * 18.0 / 9.0);

    public static final boolean MAXIMIZED = false;

    //WGS84 SRID
    public static int WGS84_SRID = 4326;

    public static final Locale DEFAULT_LOCALE = Locale.US;
    public static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    public static final String DIR_HCM_INPUT = "DIR_HCM_INPUT";
    public static final String DIR_CSV_INPUT = "DIR_CSV_INPUT";

    public static final String BUNDLE_PATH = "com.fx.swing.bundle.swing";
    public static final String LOG4J2_CONFIG_PATH = System.getProperty("user.dir") + "/config/log4j2.xml";
    public static final String XML_CONFIG_PATH = System.getProperty("user.dir") + "/config/config.xml";
    public static final String CSV_PATH = System.getProperty("user.dir") + "/CSV/";
    public static final String HCM_PATH = System.getProperty("user.dir") + "/HCM/";

    public static XMLPropertyManager propman;

    static {
        propman = new XMLPropertyManager(XML_CONFIG_PATH);
    }

    //Images
    public static final String APP_LOGO_PATH = System.getProperty("user.dir") + "/images/kdf.png";

    //Colors
    public static final Color COLOR_BLUE = HelperFunctions.getColorFromHex("#2196F3");
    public static final Color COLOR_INDIGO = HelperFunctions.getColorFromHex("#3f51b5");

    //Properties
    public static final String THEME = "THEME";
    public static final String THEME_DARK = "THEME_DARK";
    public static final String THEME_LIGHT = "THEME_LIGHT";
}
