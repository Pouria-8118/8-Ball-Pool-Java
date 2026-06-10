package view;

import model.Ball;
import model.CueType;
import model.GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class CueStick {

    private static final int CUE_LENGTH = 230;

    private double  strikeOffset;
    private boolean animating;
    private boolean goingForward;
    private CueType cueType;

    public CueStick() {
        this.strikeOffset = 0;
        this.animating    = false;
        this.goingForward = false;
        this.cueType      = CueType.CLASSIC;
    }

    public void setCueType(CueType type) {
        this.cueType = type;
    }

    public CueType getCueType() {
        return cueType;
    }

    public void startStrike() {
        this.animating    = true;
        this.goingForward = true;
        this.strikeOffset = 0;
    }

    public void tick() {
        if (!animating) {
            return;
        }

        if (goingForward) {
            strikeOffset += 5.0;
            if (strikeOffset >= 28) {
                strikeOffset = 28;
                goingForward = false;
            }
        } else {
            strikeOffset -= 2.5;
            if (strikeOffset <= 0) {
                strikeOffset = 0;
                animating    = false;
            }
        }
    }

    public boolean isAnimating() {
        return animating;
    }

    public void draw(Graphics2D g, GameState state, int mouseX, int mouseY) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cueBall = state.getCueBall();
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        double cx   = cueBall.getX();
        double cy   = cueBall.getY();
        double dx   = mouseX - cx;
        double dy   = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1) {
            return;
        }

        double nx           = dx / dist;
        double ny           = dy / dist;
        double pullDistance = Math.min(dist * 0.35, 60);
        double baseGap      = Ball.RADIUS + 6;
        double totalOffset  = baseGap + pullDistance + strikeOffset;

        double tipX = cx - nx * totalOffset;
        double tipY = cy - ny * totalOffset;
        double endX = tipX - nx * CUE_LENGTH;
        double endY = tipY - ny * CUE_LENGTH;

        if (cueType == CueType.CLASSIC) {
            drawClassic(g, tipX, tipY, endX, endY, nx, ny);
        } else if (cueType == CueType.SNOOKER) {
            drawSnooker(g, tipX, tipY, endX, endY, nx, ny);
        } else if (cueType == CueType.FANCY) {
            drawFancy(g, tipX, tipY, endX, endY, nx, ny);
        } else if (cueType == CueType.DARK_WOOD) {
            drawDarkWood(g, tipX, tipY, endX, endY, nx, ny);
        } else if (cueType == CueType.IVORY) {
            drawIvory(g, tipX, tipY, endX, endY, nx, ny);
        }

        g.setStroke(new BasicStroke(1));
    }

    private void drawClassic(Graphics2D g,
                              double tipX, double tipY,
                              double endX, double endY,
                              double nx,   double ny) {
        double wrapX    = tipX - nx * CUE_LENGTH * 0.60;
        double wrapY    = tipY - ny * CUE_LENGTH * 0.60;
        double midWrapX = tipX - nx * CUE_LENGTH * 0.45;
        double midWrapY = tipY - ny * CUE_LENGTH * 0.45;

        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(80, 40, 10));
        g.drawLine((int) wrapX, (int) wrapY, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(140, 80, 25));
        g.drawLine((int) wrapX, (int) wrapY, (int) midWrapX, (int) midWrapY);

        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(215, 175, 90));
        g.drawLine((int) tipX, (int) tipY, (int) wrapX, (int) wrapY);

        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(230, 220, 180));
        double shortEndX = tipX - nx * 18;
        double shortEndY = tipY - ny * 18;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);
    }

    private void drawSnooker(Graphics2D g,
                              double tipX, double tipY,
                              double endX, double endY,
                              double nx,   double ny) {
        double midX = tipX - nx * CUE_LENGTH * 0.55;
        double midY = tipY - ny * CUE_LENGTH * 0.55;

        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(30, 15, 5));
        g.drawLine((int) midX, (int) midY, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(180, 130, 60));
        g.drawLine((int) tipX, (int) tipY, (int) midX, (int) midY);

        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(245, 235, 200));
        double shortEndX = tipX - nx * 20;
        double shortEndY = tipY - ny * 20;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);

        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(200, 160, 80, 120));
        for (int i = 1; i <= 4; i++) {
            double t   = 0.60 + i * 0.08;
            double lx  = tipX - nx * CUE_LENGTH * t;
            double ly  = tipY - ny * CUE_LENGTH * t;
            double px1 = lx - ny * 5;
            double py1 = ly + nx * 5;
            double px2 = lx + ny * 5;
            double py2 = ly - nx * 5;
            g.drawLine((int) px1, (int) py1, (int) px2, (int) py2);
        }
    }

    private void drawFancy(Graphics2D g,
                            double tipX, double tipY,
                            double endX, double endY,
                            double nx,   double ny) {
        double seg1X = tipX - nx * CUE_LENGTH * 0.35;
        double seg1Y = tipY - ny * CUE_LENGTH * 0.35;
        double seg2X = tipX - nx * CUE_LENGTH * 0.65;
        double seg2Y = tipY - ny * CUE_LENGTH * 0.65;

        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(60, 10, 10));
        g.drawLine((int) seg2X, (int) seg2Y, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(180, 30, 30));
        g.drawLine((int) seg1X, (int) seg1Y, (int) seg2X, (int) seg2Y);

        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(230, 200, 50));
        g.drawLine((int) tipX, (int) tipY, (int) seg1X, (int) seg1Y);

        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 245, 180));
        double shortEndX = tipX - nx * 16;
        double shortEndY = tipY - ny * 16;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);

        g.setColor(new Color(255, 215, 0, 160));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 5; i++) {
            double t   = i * 0.10;
            double lx  = tipX - nx * CUE_LENGTH * t;
            double ly  = tipY - ny * CUE_LENGTH * t;
            double px1 = lx - ny * 4;
            double py1 = ly + nx * 4;
            double px2 = lx + ny * 4;
            double py2 = ly - nx * 4;
            g.drawLine((int) px1, (int) py1, (int) px2, (int) py2);
        }
    }

    private void drawDarkWood(Graphics2D g,
                               double tipX, double tipY,
                               double endX, double endY,
                               double nx,   double ny) {
        double ringX = tipX - nx * CUE_LENGTH * 0.55;
        double ringY = tipY - ny * CUE_LENGTH * 0.55;

        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(25, 12, 4));
        g.drawLine((int) ringX, (int) ringY, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(55, 28, 8));
        g.drawLine((int) tipX, (int) tipY, (int) ringX, (int) ringY);

        g.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(200, 180, 100));
        double r1x = tipX - nx * CUE_LENGTH * 0.53;
        double r1y = tipY - ny * CUE_LENGTH * 0.53;
        double r2x = tipX - nx * CUE_LENGTH * 0.57;
        double r2y = tipY - ny * CUE_LENGTH * 0.57;
        g.drawLine((int) r1x, (int) r1y, (int) r2x, (int) r2y);

        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(240, 230, 210));
        double shortEndX = tipX - nx * 16;
        double shortEndY = tipY - ny * 16;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);

        g.setColor(new Color(80, 40, 15, 80));
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 6; i++) {
            double t  = 0.05 + i * 0.08;
            double lx = tipX - nx * CUE_LENGTH * t;
            double ly = tipY - ny * CUE_LENGTH * t;
            double ox = -ny * 3;
            double oy = nx * 3;
            g.drawLine((int)(lx + ox), (int)(ly + oy),
                       (int)(lx - nx * CUE_LENGTH * (t + 0.06) + ox),
                       (int)(ly - ny * CUE_LENGTH * (t + 0.06) + oy));
        }
    }

    private void drawIvory(Graphics2D g,
                            double tipX, double tipY,
                            double endX, double endY,
                            double nx,   double ny) {
        double mid1X = tipX - nx * CUE_LENGTH * 0.40;
        double mid1Y = tipY - ny * CUE_LENGTH * 0.40;
        double mid2X = tipX - nx * CUE_LENGTH * 0.65;
        double mid2Y = tipY - ny * CUE_LENGTH * 0.65;

        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(240, 235, 215));
        g.drawLine((int) mid2X, (int) mid2Y, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(210, 195, 160));
        g.drawLine((int) mid1X, (int) mid1Y, (int) mid2X, (int) mid2Y);

        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(245, 240, 225));
        g.drawLine((int) tipX, (int) tipY, (int) mid1X, (int) mid1Y);

        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(100, 80, 20));
        double shortEndX = tipX - nx * 14;
        double shortEndY = tipY - ny * 14;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);

        g.setColor(new Color(180, 160, 100, 90));
        g.setStroke(new BasicStroke(1.5f));
        double bx1 = tipX - nx * CUE_LENGTH * 0.38;
        double by1 = tipY - ny * CUE_LENGTH * 0.38;
        double bx2 = tipX - nx * CUE_LENGTH * 0.42;
        double by2 = tipY - ny * CUE_LENGTH * 0.42;
        g.drawLine((int) bx1, (int) by1, (int) bx2, (int) by2);

        double cx1 = tipX - nx * CUE_LENGTH * 0.63;
        double cy1 = tipY - ny * CUE_LENGTH * 0.63;
        double cx2 = tipX - nx * CUE_LENGTH * 0.67;
        double cy2 = tipY - ny * CUE_LENGTH * 0.67;
        g.drawLine((int) cx1, (int) cy1, (int) cx2, (int) cy2);
    }
}