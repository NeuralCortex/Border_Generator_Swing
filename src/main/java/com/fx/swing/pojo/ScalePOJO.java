package com.fx.swing.pojo;

import com.fx.swing.painter.BorderPainter;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ScalePOJO {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private boolean active;
    private final int distance;
    private final Color color;
    private BorderPainter borderPainter;

    public ScalePOJO(boolean active, int distance, Color color, BorderPainter borderPainter) {
        this.active = active;
        this.distance = distance;
        this.color = color;
        this.borderPainter = borderPainter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        boolean old = this.active;
        this.active = active;
        support.firePropertyChange("active", old, active);
    }

    public int getDistance() {
        return distance;
    }

    public Color getColor() {
        return color;
    }

    public BorderPainter getBorderPainter() {
        return borderPainter;
    }

    public void setBorderPainter(BorderPainter borderPainter) {
        this.borderPainter = borderPainter;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
