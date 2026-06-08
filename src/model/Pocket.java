package model;

public class Pocket {

    public static final int RADIUS = 20;

    private double x;
    private double y;

    public Pocket(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}