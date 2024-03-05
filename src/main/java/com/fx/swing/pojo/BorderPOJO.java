package com.fx.swing.pojo;

import com.fx.swing.painter.BorderRoutePainter;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BorderPOJO {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String fileName;
    private boolean active;
    private BorderRoutePainter borderRoutePainter;
    private int countCoord;
    private String length;
    private Color color;

    public BorderPOJO(String fileName, boolean active, BorderRoutePainter borderRoutePainter, int countCoord, Color color, String length) {
        this.fileName = fileName;
        this.active = active;
        this.borderRoutePainter = borderRoutePainter;
        this.countCoord = countCoord;
        this.color = color;
        this.length = length;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        boolean old = this.active;
        this.active = active;
        support.firePropertyChange("active", old, active);
    }

    public BorderRoutePainter getBorderRoutePainter() {
        return borderRoutePainter;
    }

    public void setBorderRoutePainter(BorderRoutePainter borderRoutePainter) {
        this.borderRoutePainter = borderRoutePainter;
    }

    public int getCountCoord() {
        return countCoord;
    }

    public void setCountCoord(int countCoord) {
        this.countCoord = countCoord;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
