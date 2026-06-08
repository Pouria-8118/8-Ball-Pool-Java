package model;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private int x;
    private int y;
    private int width;
    private int height;
    private List<Pocket> pockets;

    public Table(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pockets = new ArrayList<>();
        initializePockets();
    }

    private void initializePockets() {
        int px = x;
        int py = y;
        int pw = width;
        int ph = height;

        pockets.add(new Pocket(px, py));
        pockets.add(new Pocket(px + pw / 2, py));
        pockets.add(new Pocket(px + pw, py));
        pockets.add(new Pocket(px, py + ph));
        pockets.add(new Pocket(px + pw / 2, py + ph));
        pockets.add(new Pocket(px + pw, py + ph));
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public List<Pocket> getPockets() {
        return pockets;
    }

    public int getLeftWall() {
        return x;
    }
    public int getRightWall() {
        return x + width;
    }
    public int getTopWall() {
        return y;
    }
    public int getBottomWall() {
        return y + height;
    }

    public int getBreakLineX() { 
        return x + width / 4;
    }
}