package physics;

public class Vector {

    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { 
        return x;
    }
    public double getY() {
        return y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalize() {
        double len = length();
        if (len == 0) {
            return new Vector(0, 0);
        }
        return new Vector(x / len, y / len);
    }

    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector scale(double factor) {
        return new Vector(this.x * factor, this.y * factor);
    }
}