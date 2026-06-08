package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Table {

    public static final int X = 80;
    public static final int Y = 60;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;
    public static final int POCKET_RADIUS = 18;
    public static final int RAIL_THICKNESS = 20;

    private List<Point> pockets;

    public Table() {
        pockets = new ArrayList<Point>();
    }

    public List<Point> getPockets() {
        return pockets;
    }

    public int getLeft() {
        return X + RAIL_THICKNESS;
    }

    public int getRight() {
        return X + WIDTH - RAIL_THICKNESS;
    }

    public int getTop() {
        return Y + RAIL_THICKNESS;
    }

    public int getBottom() {
        return Y + HEIGHT - RAIL_THICKNESS;
    }

    public int getBreakLineX() {
        return X + WIDTH / 4;
    }
}