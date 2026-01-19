package com.fx.swing.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

    public static Icon resizeIcon(Icon icon, int newWidth, int newHeight) {
        if (!(icon instanceof ImageIcon)) {
            throw new IllegalArgumentException("Icon must be an ImageIcon");
        }
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        Image image = ((ImageIcon) icon).getImage();
        Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static Color getColorFromHex(String hexColor) {
        // Remove the '#' if present
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        // Ensure the hex string is valid (6 characters for RGB)
        if (hexColor.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color code. Must be in format #RRGGBB or RRGGBB");
        }

        try {
            // Parse the hex values for red, green, and blue
            int red = Integer.parseInt(hexColor.substring(0, 2), 16);
            int green = Integer.parseInt(hexColor.substring(2, 4), 16);
            int blue = Integer.parseInt(hexColor.substring(4, 6), 16);

            // Return the Color object
            return new Color(red, green, blue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color code. Must contain valid hexadecimal values");
        }
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

    public static String alpha2ToAlpha3(String alpha2) {
        if (alpha2 == null || alpha2.length() != 2) {
            return null;
        }

        // Locale expects uppercase
        String upper = alpha2.toUpperCase(Locale.ROOT);

        // Special case: United Kingdom has inconsistent behavior in some JDKs
        if ("GB".equals(upper)) {
            return "GBR";
        }

        try {
            Locale locale = new Locale("", upper);
            String alpha3 = locale.getISO3Country();

            return alpha3.isEmpty() ? null : alpha3;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidCodeFileName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        return filename.matches("[A-Z]{3}\\.\\d{3}\\.csv");
    }
}
