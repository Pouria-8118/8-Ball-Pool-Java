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

    private static final int BALL_R  = 54;
    private static final int DOT_R   = 9;

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

        int boxW = 280;
        int boxH = 240;
        int bx   = centerX - boxW / 2;
        int by   = centerY - boxH / 2;

        g.setColor(new Color(15, 15, 15, 220));
        g.fillRoundRect(bx, by, boxW, boxH, 18, 18);
        g.setColor(new Color(90, 90, 90));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, by, boxW, boxH, 18, 18);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("Arial", Font.BOLD, 15));
        FontMetrics fm    = g.getFontMetrics();
        String      title = "Choose Spin Point";
        g.drawString(title, centerX - fm.stringWidth(title) / 2, by + 26);

        g.setColor(new Color(230, 230, 230));
        g.fillOval(centerX - BALL_R, centerY - BALL_R + 20, BALL_R * 2, BALL_R * 2);
        g.setColor(new Color(180, 180, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(centerX - BALL_R, centerY - BALL_R + 20, BALL_R * 2, BALL_R * 2);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(255, 255, 255, 70));
        g.fillOval(centerX - BALL_R + 8, centerY - BALL_R + 22, BALL_R / 2, BALL_R / 3);

        int ballCY = centerY + 20;

        drawDot(g, centerX,          ballCY - BALL_R / 2, SpinType.TOP,  "Top spin");
        drawDot(g, centerX,          ballCY + BALL_R / 2, SpinType.BACK, "Back spin");
        drawDot(g, centerX,          ballCY,              SpinType.NONE, "Stop");

        g.setColor(new Color(120, 120, 120));
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        FontMetrics hfm  = g.getFontMetrics();
        String      hint = "Click to select";
        g.drawString(hint, centerX - hfm.stringWidth(hint) / 2, by + boxH - 10);
    }

    private void drawDot(Graphics2D g, int dotX, int dotY,
                          SpinType spin, String label) {
        boolean isHovered  = spin == hovered;
        boolean isSelected = spin == selected;

        Color fill;
        if (isSelected) {
            fill = new Color(255, 50, 50);
        } else if (isHovered) {
            fill = new Color(255, 160, 40);
        } else {
            fill = new Color(200, 50, 50, 200);
        }

        if (isSelected || isHovered) {
            g.setColor(new Color(255, 255, 255, 60));
            g.fillOval(dotX - DOT_R - 4, dotY - DOT_R - 4,
                       (DOT_R + 4) * 2, (DOT_R + 4) * 2);
        }

        g.setColor(fill);
        g.fillOval(dotX - DOT_R, dotY - DOT_R, DOT_R * 2, DOT_R * 2);

        if (isSelected) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(dotX - DOT_R, dotY - DOT_R, DOT_R * 2, DOT_R * 2);
            g.setStroke(new BasicStroke(1));
        }

        g.setFont(new Font("Arial", isSelected ? Font.BOLD : Font.PLAIN, 11));
        FontMetrics fm   = g.getFontMetrics();
        Color       lblC = isSelected ? Color.YELLOW : new Color(200, 200, 200);
        g.setColor(lblC);
        g.drawString(label, dotX + DOT_R + 6, dotY + fm.getAscent() / 2 - 1);
    }

    public SpinType handleClick(MouseEvent e) {
        if (!visible) {
            return null;
        }

        int    ballCY  = centerY + 20;
        int[]  ys      = {ballCY - BALL_R / 2, ballCY + BALL_R / 2, ballCY};
        SpinType[] spins = {SpinType.TOP, SpinType.BACK, SpinType.NONE};

        for (int i = 0; i < spins.length; i++) {
            int dx = e.getX() - centerX;
            int dy = e.getY() - ys[i];
            if (Math.sqrt(dx * dx + dy * dy) < DOT_R + 10) {
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

        int    ballCY  = centerY + 20;
        int[]  ys      = {ballCY - BALL_R / 2, ballCY + BALL_R / 2, ballCY};
        SpinType[] spins = {SpinType.TOP, SpinType.BACK, SpinType.NONE};

        hovered = null;
        for (int i = 0; i < spins.length; i++) {
            int dx = e.getX() - centerX;
            int dy = e.getY() - ys[i];
            if (Math.sqrt(dx * dx + dy * dy) < DOT_R + 10) {
                hovered = spins[i];
                break;
            }
        }
    }
}