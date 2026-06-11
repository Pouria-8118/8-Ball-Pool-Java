package view;

import model.SpinType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

public class SpinSelector {

    private static final int BALL_R = 150;
    private static final int DOT_R  = 14;

    private boolean  visible;
    private int      centerX;
    private int      centerY;
    private SpinType hovered;
    private SpinType selected;

    public SpinSelector() {
        this.visible  = false;
        this.hovered  = null;
        this.selected = SpinType.NONE;
    }

    public void show(int screenW, int screenH) {
        centerX = screenW / 2;
        centerY = screenH / 2 - 30;
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void reset() {
        selected = SpinType.NONE;
        hovered  = null;
    }

    public void draw(Graphics2D g) {
        if (!visible) {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int boxW = 520;
        int boxH = 420;
        int bx   = centerX - boxW / 2;
        int by   = centerY - boxH / 2;

        g.setColor(new Color(0, 0, 0, 230));
        g.fillRoundRect(bx, by, boxW, boxH, 30, 30);
        g.setColor(new Color(200, 180, 100));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(bx, by, boxW, boxH, 30, 30);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(240, 240, 255));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm    = g.getFontMetrics();
        String      title = "SELECT SPIN";
        g.drawString(title, centerX - fm.stringWidth(title) / 2, by + 45);

        g.setColor(new Color(250, 250, 250));
        int ballCY = centerY + 20;
        g.fillOval(centerX - BALL_R, ballCY - BALL_R, BALL_R * 2, BALL_R * 2);
        g.setColor(new Color(80, 80, 120));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(centerX - BALL_R, ballCY - BALL_R, BALL_R * 2, BALL_R * 2);
        g.setStroke(new BasicStroke(1));

        drawDot(g, centerX,                         ballCY - BALL_R / 2 - 15, SpinType.TOP,   "TOP");
        drawDot(g, centerX,                         ballCY + BALL_R / 2 + 15, SpinType.BACK,  "BACK");
        drawDot(g, centerX,                         ballCY,                    SpinType.NONE,  "STOP");
        drawDot(g, centerX - BALL_R / 2 - 15,       ballCY,                    SpinType.LEFT,  "LEFT");
        drawDot(g, centerX + BALL_R / 2 + 15,       ballCY,                    SpinType.RIGHT, "RIGHT");

        g.setColor(new Color(100, 100, 130));
        g.setFont(new Font("Arial", Font.PLAIN, 13));
        FontMetrics hfm  = g.getFontMetrics();
        String      hint = "Click to select";
        g.drawString(hint, centerX - hfm.stringWidth(hint) / 2, by + boxH - 18);
    }

    private void drawDot(Graphics2D g, int dotX, int dotY,
                          SpinType spin, String label) {
        boolean isHovered  = spin == hovered;

        Color fill;
        Color textColor;

        if (isHovered) {
            fill      = new Color(220, 20, 60);
            textColor = new Color(220, 20, 60);
        } else {
            fill      = new Color(25, 25, 112);
            textColor = new Color(25, 25, 112);
        }

        g.setColor(fill);
        g.fillOval(dotX - DOT_R, dotY - DOT_R, DOT_R * 2, DOT_R * 2);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(textColor);
        g.drawString(label, dotX + DOT_R + 6, dotY + fm.getAscent() / 2);
    }

    public SpinType handleClick(MouseEvent e) {
        if (!visible) {
            return null;
        }

        int      ballCY = centerY + 20;
        int[]    xs     = {centerX,                         centerX,
                           centerX,                         centerX - BALL_R / 2 - 15,
                           centerX + BALL_R / 2 + 15};
        int[]    ys     = {ballCY - BALL_R / 2 - 15,        ballCY + BALL_R / 2 + 15,
                           ballCY,                         ballCY,
                           ballCY};
        SpinType[] spins = {SpinType.TOP, SpinType.BACK, SpinType.NONE,
                            SpinType.LEFT, SpinType.RIGHT};

        for (int i = 0; i < spins.length; i++) {
            int dx = e.getX() - xs[i];
            int dy = e.getY() - ys[i];
            if (Math.sqrt(dx * dx + dy * dy) < DOT_R + 15) {
                selected = spins[i];
                return selected;
            }
        }

        return null;
    }

    public void handleMouseMove(MouseEvent e) {
        if (!visible) {
            return;
        }

        int      ballCY = centerY + 20;
        int[]    xs     = {centerX,                         centerX,
                           centerX,                         centerX - BALL_R / 2 - 15,
                           centerX + BALL_R / 2 + 15};
        int[]    ys     = {ballCY - BALL_R / 2 - 15,        ballCY + BALL_R / 2 + 15,
                           ballCY,                         ballCY,
                           ballCY};
        SpinType[] spins = {SpinType.TOP, SpinType.BACK, SpinType.NONE,
                            SpinType.LEFT, SpinType.RIGHT};

        hovered = null;
        for (int i = 0; i < spins.length; i++) {
            int dx = e.getX() - xs[i];
            int dy = e.getY() - ys[i];
            if (Math.sqrt(dx * dx + dy * dy) < DOT_R + 15) {
                hovered = spins[i];
                break;
            }
        }
    }
}