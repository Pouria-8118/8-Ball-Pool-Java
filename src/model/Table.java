package model;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private int          x;
    private int          y;
    private int          width;
    private int          height;
    private List<Pocket> pockets;

    public Table(int x, int y, int width, int height) {
        this.x       = x;
        this.y       = y;
        this.width   = width;
        this.height  = height;
        this.pockets = new ArrayList<>();
        initPockets();
    }

    private void initPockets() {
        pockets.add(new Pocket(x,                    y));
        pockets.add(new Pocket(x + width / 2.0,      y));
        pockets.add(new Pocket(x + width,             y));
        pockets.add(new Pocket(x,                    y + height));
        pockets.add(new Pocket(x + width / 2.0,      y + height));
        pockets.add(new Pocket(x + width,             y + height));
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