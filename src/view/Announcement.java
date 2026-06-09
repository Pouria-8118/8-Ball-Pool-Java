package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Announcement {

    private String   mainText;
    private String   subText;
    private Color    mainColor;
    private int      alpha;
    private Timer    fadeTimer;
    private Runnable onDone;

    public Announcement() {
        this.mainText = "";
        this.subText  = "";
        this.alpha    = 0;
    }

    public void show(String text, String sub, Color color, Runnable callback) {
        this.mainText  = text;
        this.subText   = sub;
        this.mainColor = color;
        this.onDone    = callback;
        this.alpha     = 255;

        if (fadeTimer != null) {
            fadeTimer.stop();
        }

        fadeTimer = new Timer(22, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 5;
                if (alpha <= 0) {
                    alpha = 0;
                    fadeTimer.stop();
                    if (onDone != null) {
                        onDone.run();
                    }
                }
            }
        });
        fadeTimer.setInitialDelay(1600);
        fadeTimer.start();
    }

    public void show(String text, Color color) {
        show(text, "", color, null);
    }

    public boolean isVisible() {
        return alpha > 0;
    }

    public void stop() {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        alpha = 0;
    }

    public void draw(Graphics2D g, int screenW, int screenH) {
        if (alpha <= 0 || mainText.isEmpty()) {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean hasSub = subText != null && !subText.isEmpty();
        int     boxW   = 440;
        int     boxH   = hasSub ? 100 : 72;
        int     bx     = screenW / 2 - boxW / 2;
        int     by     = screenH / 2 - boxH / 2 - 55;

        int bgA = Math.min(alpha, 210);
        g.setColor(new Color(8, 8, 8, bgA));
        g.fillRoundRect(bx, by, boxW, boxH, 20, 20);

        int borderA = Math.min(alpha, 200);
        g.setColor(new Color(mainColor.getRed(), mainColor.getGreen(),
                             mainColor.getBlue(), borderA));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(bx, by, boxW, boxH, 20, 20);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Arial", Font.BOLD, 27));
        FontMetrics fm = g.getFontMetrics();
        int mainY = hasSub ? by + 44 : by + boxH / 2 + fm.getAscent() / 2 - 4;

        g.setColor(new Color(0, 0, 0, Math.min(alpha, 200)));
        g.drawString(mainText,
                screenW / 2 - fm.stringWidth(mainText) / 2 + 2,
                mainY + 2);

        g.setColor(new Color(mainColor.getRed(), mainColor.getGreen(),
                             mainColor.getBlue(), Math.min(alpha, 255)));
        g.drawString(mainText, screenW / 2 - fm.stringWidth(mainText) / 2, mainY);

        if (hasSub) {
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            FontMetrics sfm = g.getFontMetrics();
            int         subA = Math.min(alpha, 180);
            g.setColor(new Color(200, 200, 200, subA));
            g.drawString(subText,
                    screenW / 2 - sfm.stringWidth(subText) / 2,
                    by + 74);
        }
    }
}