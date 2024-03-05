package com.fx.swing.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.swing.JTabbedPane;
import org.apache.lucene.util.SloppyMath;

public class HelperFunctions {

    public static double SF = 180.0 / Math.PI;

    public static void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        double x = (dimension.getWidth() - frame.getWidth()) / 2.0;
        double y = (dimension.getHeight() - frame.getHeight()) / 2.0;
        frame.setLocation((int) x, (int) y);
    }

    public static Color getColorFromHex(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);

        return new Color(r, g, b);
    }

    public static void addTab(JTabbedPane tabbedPane, Component controller, String tabName) {
        long start = System.currentTimeMillis();
        tabbedPane.addTab(tabName, controller);
        long end = System.currentTimeMillis();
        System.out.println("Loadtime (" + controller.toString() + ") in ms: " + (end - start));
    }

    public static byte[] doubleToByte(double coord, ByteOrder byteOrder) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).order(byteOrder).putDouble(coord);
        return bytes;
    }

    public static double byteToDouble(byte[] bytes, ByteOrder byteOrder) {
        return ByteBuffer.wrap(bytes).order(byteOrder).getDouble();
    }

    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double dist = SloppyMath.haversinMeters(lat1, lon1, lat2, lon2) / 1000.0;
        return dist;
    }
}
