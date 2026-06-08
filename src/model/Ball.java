package model;

import java.awt.Color;

public class Ball {

    public static final int RADIUS = 12;

    public static final int TYPE_CUE = 0;
    public static final int TYPE_SOLID = 1;
    public static final int TYPE_STRIPE = 2;
    public static final int TYPE_EIGHT = 3;

    private int number;
    private int type;
    private Color color;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private boolean pocketed;

    public Ball(int number, int type, Color color, double x, double y) {
        this.number = number;
        this.type = type;
        this.color = color;
        this.x = x;
        this.y = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.pocketed = false;
    }

    public int getNumber() {
        return number;
    }

    public int getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public boolean isPocketed() {
        return pocketed;
    }

    public void setPocketed(boolean pocketed) {
        this.pocketed = pocketed;
    }

    public void reset() {
        this.velocityX = 0;
        this.velocityY = 0;
        this.pocketed = false;
    }
}