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

public class ShotTimer {

    private static final int TOTAL_SECONDS = 30;
    private static final int WARNING_SECONDS = 10;
    private static final int CRITICAL_SECONDS = 5;

    private int     secondsLeft;
    private boolean running;
    private Timer   ticker;
    private Runnable onTimeUp;

    private double  shakeOffset;
    private boolean shakeDir;

    public ShotTimer() {
        this.secondsLeft = TOTAL_SECONDS;
        this.running     = false;
        this.shakeOffset = 0;
        this.shakeDir    = false;
    }

    public void start(Runnable callback) {
        this.secondsLeft = TOTAL_SECONDS;
        this.running     = true;
        this.onTimeUp    = callback;

        if (ticker != null) {
            ticker.stop();
        }

        ticker = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    return;
                }
                secondsLeft--;
                if (secondsLeft <= 0) {
                    secondsLeft = 0;
                    running     = false;
                    ticker.stop();
                    if (onTimeUp != null) {
                        onTimeUp.run();
                    }
                }
            }
        });
        ticker.start();
    }

    public void stop() {
        running = false;
        if (ticker != null) {
            ticker.stop();
        }
    }

    public void reset() {
        stop();
        secondsLeft = TOTAL_SECONDS;
        shakeOffset = 0;
    }

    public boolean isRunning() {
        return running;
    }

    public void draw(Graphics2D g, int screenW) {
        if (!running) {
            return;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double ratio      = (double) secondsLeft / TOTAL_SECONDS;
        boolean warning   = secondsLeft <= WARNING_SECONDS;
        boolean critical  = secondsLeft <= CRITICAL_SECONDS;

        if (critical) {
            shakeDir    = !shakeDir;
            shakeOffset = shakeDir ? 2.5 : -2.5;
        } else {
            shakeOffset = 0;
        }

        int barW    = 200;
        int barH    = 14;
        int barX    = screenW / 2 - barW / 2 + (int) shakeOffset;
        int barY    = 10;
        int fillW   = (int)(barW * ratio);

        g.setColor(new Color(30, 30, 30, 200));
        g.fillRoundRect(barX, barY, barW, barH, 7, 7);

        Color fillColor;
        if (critical) {
            long t    = System.currentTimeMillis();
            float pulse = (float)(0.5 + 0.5 * Math.sin(t / 120.0));
            int   red   = (int)(200 + 55 * pulse);
            fillColor = new Color(red, 20, 20);
        } else if (warning) {
            fillColor = new Color(230, 130, 20);
        } else {
            fillColor = new Color(60, 180, 60);
        }

        g.setColor(fillColor);
        g.fillRoundRect(barX, barY, fillW, barH, 7, 7);

        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, barY, barW, barH, 7, 7);
        g.setStroke(new BasicStroke(1));

        String timeText = String.valueOf(secondsLeft);
        g.setFont(new Font("Arial", Font.BOLD, critical ? 12 : 10));
        FontMetrics fm  = g.getFontMetrics();

        Color textColor = critical ? new Color(255, 80, 80) : new Color(220, 220, 220);
        g.setColor(textColor);
        g.drawString(timeText,
                screenW / 2 - fm.stringWidth(timeText) / 2 + (int) shakeOffset,
                barY + barH + 18);
    }
}