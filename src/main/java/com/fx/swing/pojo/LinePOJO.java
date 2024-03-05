package com.fx.swing.pojo;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class LinePOJO {

    private int idx;
    private Line2D line2D;

    public LinePOJO(int idx, Line2D line2D) {
        this.idx = idx;
        this.line2D = line2D;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Line2D getLine2D() {
        return line2D;
    }

    public void setLine2D(Line2D line2D) {
        this.line2D = line2D;
    }

    public Point2D getMiddle() {
        Point2D start = line2D.getP1();
        Point2D end = line2D.getP2();

        double mx = (end.getX() + start.getX()) / 2.0;
        double my = (end.getY() + start.getY()) / 2.0;

        return new Point2D.Double(mx, my);
    }
}
