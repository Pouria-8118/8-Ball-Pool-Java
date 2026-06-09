package model;

import java.awt.Color;

public class Ball {

    public static final int RADIUS = 14;

    private int     number;
    private double  x;
    private double  y;
    private double  velocityX;
    private double  velocityY;
    private Color   color;
    private boolean stripe;
    private boolean cueBall;
    private boolean black;
    private boolean pocketed;
    private double  pocketAnimScale;

    public Ball(int number, double x, double y, Color color,
                boolean stripe, boolean cueBall, boolean black) {
        this.number          = number;
        this.x               = x;
        this.y               = y;
        this.color           = color;
        this.stripe          = stripe;
        this.cueBall         = cueBall;
        this.black           = black;
        this.velocityX       = 0;
        this.velocityY       = 0;
        this.pocketed        = false;
        this.pocketAnimScale = 1.0;
    }

    public boolean isMoving() {
        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        return speed > 0.10;
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
        return stripe;
    }

    public boolean isCueBall() {
        return cueBall;
    }

    public boolean isBlack() {
        return black;
    }

    public boolean isPocketed() {
        return pocketed;
    }

    public double getPocketAnimScale() {
        return pocketAnimScale;
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
        this.pocketed = pocketed;
    }

    public void setPocketAnimScale(double scale) {
        this.pocketAnimScale = scale;
    }
}