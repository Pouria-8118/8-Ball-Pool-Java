package model;

import java.awt.Color;

public class Ball {

    public static final int RADIUS = 14;

    private int number;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private Color color;
    private boolean isStripe;
    private boolean isCueBall;
    private boolean isBlack;
    private boolean isPocketed;

    public Ball(int number, double x, double y, Color color, boolean isStripe, boolean isCueBall, boolean isBlack) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.color = color;
        this.isStripe = isStripe;
        this.isCueBall = isCueBall;
        this.isBlack = isBlack;
        this.velocityX = 0;
        this.velocityY = 0;
        this.isPocketed = false;
    }

    public boolean isMoving() {
        return Math.abs(velocityX) > 0.01 || Math.abs(velocityY) > 0.01;
    }

    public void applyFriction(double friction) {
        velocityX *= friction;
        velocityY *= friction;
        if (Math.abs(velocityX) < 0.01) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < 0.01) {
            velocityY = 0;
        }
    }

    public void update() {
        if (!isPocketed) {
            x += velocityX;
            y += velocityY;
        }
    }

    public int getNumber() { 
        return number;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getVelocityX() {
        return velocityX;
    }
    public double getVelocityY() {
        return velocityY;
    }
    public Color getColor() {
        return color;
    }
    public boolean isStripe() {
        return isStripe;
    }
    public boolean isCueBall() {
        return isCueBall;
    }
    public boolean isBlack() {
        return isBlack;
    }
    public boolean isPocketed() {
        return isPocketed;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }
    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }
    public void setPocketed(boolean pocketed) {
        this.isPocketed = pocketed;
    }
}