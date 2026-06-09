package view;

import controller.GameController;
import model.Ball;
import model.GameState;
import model.Player;
import model.Pocket;
import model.SpinType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    public static final int TABLE_X      = 70;
    public static final int TABLE_Y      = 65;
    public static final int TABLE_WIDTH  = 780;
    public static final int TABLE_HEIGHT = 390;

    private GameController  controller;
    private PlayerInfoPanel playerInfoPanel;
    private CueStick        cueStick;
    private SpinSelector    spinSelector;
    private Announcement    announcement;

    private double  aimAngle         = 0;
    private double  aimPower         = 0;
    private int     mouseX           = 400;
    private int     mouseY           = 300;
    private boolean blockNextRelease = false;

    private List<double[]> trail;

    public GamePanel() {
        setLayout(null);
        setBackground(new Color(20, 20, 20));
        setPreferredSize(new Dimension(960, 600));

        cueStick     = new CueStick();
        spinSelector = new SpinSelector();
        announcement = new Announcement();
        trail        = new ArrayList<>();

        playerInfoPanel = new PlayerInfoPanel();
        playerInfoPanel.setBounds(0, 500, 960, 95);
        add(playerInfoPanel);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                spinSelector.handleMouseMove(e);
                if (controller != null) {
                    controller.onMouseMoved(e.getX(), e.getY());
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (spinSelector.isVisible()) {
                    SpinType chosen = spinSelector.handleClick(e);
                    if (chosen != null && controller != null) {
                        blockNextRelease = true;
                        controller.onSpinSelected(chosen);
                    }
                    return;
                }
                if (controller != null) {
                    controller.onMousePressed(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (blockNextRelease) {
                    blockNextRelease = false;
                    return;
                }
                if (controller != null) {
                    controller.onMouseReleased(e.getX(), e.getY());
                }
            }
        });
    }

    public void setGameController(GameController gc) {
        this.controller = gc;
    }

    public void notifyShot() {
        cueStick.startStrike();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        drawTable(g2);

        if (controller != null && controller.getGameState() != null) {
            GameState state = controller.getGameState();
            updateTrail(state);
            drawTrail(g2);
            drawBalls(g2, state);
            drawSelectedPocket(g2, state);
            drawAimLine(g2, state);
            cueStick.tick();
            cueStick.draw(g2, state, mouseX, mouseY);
            drawPowerBar(g2, state);
            drawPlacementHint(g2, state);
            playerInfoPanel.update(state);
        }

        spinSelector.draw(g2);
        announcement.draw(g2, getWidth(), getHeight());
    }

    private void drawTable(Graphics2D g) {
        g.setColor(new Color(88, 50, 12));
        g.fillRoundRect(TABLE_X - 22, TABLE_Y - 22,
                        TABLE_WIDTH + 44, TABLE_HEIGHT + 44, 14, 14);

        g.setColor(new Color(0, 100, 0));
        g.fillRect(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);

        for (int col = 0; col < TABLE_WIDTH; col += 28) {
            int shade = (col / 28) % 2 == 0 ? 100 : 106;
            g.setColor(new Color(0, shade, 0));
            g.fillRect(TABLE_X + col, TABLE_Y, 28, TABLE_HEIGHT);
        }

        g.setColor(new Color(0, 75, 0, 110));
        g.setStroke(new BasicStroke(1.5f));
        int breakX = TABLE_X + TABLE_WIDTH / 4;
        g.drawLine(breakX, TABLE_Y + 2, breakX, TABLE_Y + TABLE_HEIGHT - 2);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(0, 120, 0, 160));
        int spotX = TABLE_X + TABLE_WIDTH * 3 / 4;
        int spotY = TABLE_Y + TABLE_HEIGHT / 2;
        g.fillOval(spotX - 4, spotY - 4, 8, 8);

        for (Pocket p : buildPocketList()) {
            int px = (int) p.getX();
            int py = (int) p.getY();
            int r  = Pocket.RADIUS;

            g.setColor(new Color(6, 6, 6));
            g.fillOval(px - r, py - r, r * 2, r * 2);

            g.setColor(new Color(50, 28, 5));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(px - r, py - r, r * 2, r * 2);
            g.setStroke(new BasicStroke(1));
        }
    }

    private List<Pocket> buildPocketList() {
        List<Pocket> list = new ArrayList<>();
        list.add(new Pocket(TABLE_X,                      TABLE_Y));
        list.add(new Pocket(TABLE_X + TABLE_WIDTH / 2.0,  TABLE_Y));
        list.add(new Pocket(TABLE_X + TABLE_WIDTH,         TABLE_Y));
        list.add(new Pocket(TABLE_X,                      TABLE_Y + TABLE_HEIGHT));
        list.add(new Pocket(TABLE_X + TABLE_WIDTH / 2.0,  TABLE_Y + TABLE_HEIGHT));
        list.add(new Pocket(TABLE_X + TABLE_WIDTH,         TABLE_Y + TABLE_HEIGHT));
        return list;
    }

    private void drawSelectedPocket(Graphics2D g, GameState state) {
        Pocket sel = state.getSelectedPocket();
        if (sel == null) {
            return;
        }

        long  t     = System.currentTimeMillis();
        float pulse = (float)(0.5 + 0.5 * Math.sin(t / 180.0));
        int   a1    = (int)(130 + 110 * pulse);
        int   a2    = (int)(60  + 50  * pulse);
        int   px    = (int) sel.getX();
        int   py    = (int) sel.getY();
        int   r     = Pocket.RADIUS;

        g.setColor(new Color(40, 255, 110, a1));
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(px - r - 5, py - r - 5, (r + 5) * 2, (r + 5) * 2);

        g.setColor(new Color(40, 255, 110, a2));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(px - r - 11, py - r - 11, (r + 11) * 2, (r + 11) * 2);

        g.setStroke(new BasicStroke(1));
    }

    private void updateTrail(GameState state) {
        Ball cue = state.getCueBall();
        if (cue == null || cue.isPocketed()) {
            trail.clear();
            return;
        }

        if (state.getPhase() == GameState.Phase.BALLS_MOVING && cue.isMoving()) {
            trail.add(new double[]{cue.getX(), cue.getY()});
            if (trail.size() > 22) {
                trail.remove(0);
            }
        } else if (state.getPhase() != GameState.Phase.BALLS_MOVING) {
            trail.clear();
        }
    }

    private void drawTrail(Graphics2D g) {
        if (trail.size() < 2) {
            return;
        }

        for (int i = 1; i < trail.size(); i++) {
            double[] prev = trail.get(i - 1);
            double[] curr = trail.get(i);
            float    t    = (float) i / trail.size();
            int      a    = (int)(t * 90);
            float    w    = t * 5;
            g.setColor(new Color(255, 255, 255, a));
            g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine((int) prev[0], (int) prev[1], (int) curr[0], (int) curr[1]);
        }
        g.setStroke(new BasicStroke(1));
    }

    private void drawBalls(Graphics2D g, GameState state) {
        for (Ball ball : state.getBalls()) {
            if (ball.getPocketAnimScale() > 0) {
                drawOneBall(g, ball, state);
            }
        }
    }

    private void drawOneBall(Graphics2D g, Ball ball, GameState state) {
        double scale = ball.getPocketAnimScale();
        int    cx    = (int) ball.getX();
        int    cy    = (int) ball.getY();
        int    r     = (int)(Ball.RADIUS * scale);

        if (r < 1) {
            return;
        }

        int bx   = cx - r;
        int by   = cy - r;
        int diam = r * 2;

        if (ball.isStripe()) {
            g.setColor(Color.WHITE);
            g.fillOval(bx, by, diam, diam);

            g.setColor(ball.getColor());
            g.fillOval(bx, by, diam, diam);

            g.setColor(Color.WHITE);
            int stripH = (int)(diam * 0.30);
            g.fillRect(bx, cy - stripH / 2, diam, stripH);

            g.setColor(ball.getColor());
            int innerR = (int)(r * 0.55);
            g.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);
        } else {
            g.setColor(ball.getColor());
            g.fillOval(bx, by, diam, diam);
        }

        if (ball.getNumber() > 0) {
            int labelR = (int)(r * 0.5);
            g.setColor(Color.WHITE);
            g.fillOval(cx - labelR, cy - labelR, labelR * 2, labelR * 2);
            g.setColor(Color.BLACK);
            int fontSize = Math.max(6, (int)(r * 0.65));
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm  = g.getFontMetrics();
            String      num = String.valueOf(ball.getNumber());
            g.drawString(num,
                    cx - fm.stringWidth(num) / 2,
                    cy + fm.getAscent() / 2 - 1);
        }

        g.setColor(new Color(255, 255, 255, 50));
        g.fillOval(cx - r + 3, cy - r + 2, r - 2, r / 2);

        g.setColor(new Color(0, 0, 0, 35));
        g.setStroke(new BasicStroke(1f));
        g.drawOval(bx, by, diam, diam);
        g.setStroke(new BasicStroke(1));

        if (shouldHighlight(ball, state) && scale >= 1.0) {
            long  t2    = System.currentTimeMillis();
            float pulse = (float)(0.55 + 0.45 * Math.sin(t2 / 280.0));
            int   ha    = (int)(130 * pulse);
            g.setColor(new Color(255, 230, 40, ha));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval(bx - 3, by - 3, diam + 6, diam + 6);
            g.setStroke(new BasicStroke(1));
        }
    }

    private boolean shouldHighlight(Ball ball, GameState state) {
        if (ball.isCueBall()) {
            return false;
        }

        GameState.Phase phase = state.getPhase();
        boolean inAim = phase == GameState.Phase.AIMING
                     || phase == GameState.Phase.SELECT_POCKET;

        if (!inAim) {
            return false;
        }

        if (state.isTableOpen()) {
            return false;
        }

        Player current = state.getCurrentPlayer();
        if (current == null || current.getGroup() == Player.Group.NONE) {
            return false;
        }

        if (state.getRemainingCount(current.getGroup()) == 0 && ball.isBlack()) {
            return true;
        }

        if (current.getGroup() == Player.Group.SOLID && !ball.isStripe() && !ball.isBlack()) {
            return true;
        }

        if (current.getGroup() == Player.Group.STRIPE && ball.isStripe()) {
            return true;
        }

        return false;
    }

    private void drawAimLine(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cue = state.getCueBall();
        if (cue == null || cue.isPocketed()) {
            return;
        }

        int cx  = (int) cue.getX();
        int cy  = (int) cue.getY();
        int len = 170;
        int ex  = (int)(cx + Math.cos(aimAngle) * len);
        int ey  = (int)(cy + Math.sin(aimAngle) * len);

        g.setColor(new Color(255, 255, 255, 60));
        g.setStroke(new BasicStroke(1.3f,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{5f, 7f}, 0));
        g.drawLine(cx, cy, ex, ey);
        g.setStroke(new BasicStroke(1));
    }

    private void drawPowerBar(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        int barX = TABLE_X + TABLE_WIDTH + 28;
        int barY = TABLE_Y;
        int barW = 20;
        int barH = TABLE_HEIGHT;

        g.setColor(new Color(30, 30, 30));
        g.fillRoundRect(barX, barY, barW, barH, 6, 6);

        int fillH = (int)(barH * aimPower / 22.0);
        int red   = (int) Math.min(255, aimPower * 12);
        int grn   = (int) Math.max(0,   255 - aimPower * 12);
        g.setColor(new Color(red, grn, 0));
        g.fillRoundRect(barX, barY + barH - fillH, barW, fillH, 6, 6);

        g.setColor(new Color(90, 90, 90));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, barY, barW, barH, 6, 6);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(170, 170, 170));
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("PWR", barX - 1, barY - 6);
    }

    private void drawPlacementHint(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.PLACE_CUE_BALL) {
            return;
        }

        int breakX = TABLE_X + TABLE_WIDTH / 4;
        g.setColor(new Color(100, 180, 255, 28));
        g.fillRect(TABLE_X + 1, TABLE_Y + 1,
                   breakX - TABLE_X - 1, TABLE_HEIGHT - 2);

        g.setColor(new Color(100, 180, 255, 90));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(breakX, TABLE_Y, breakX, TABLE_Y + TABLE_HEIGHT);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(140, 200, 255, 160));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Place here", TABLE_X + 16, TABLE_Y + TABLE_HEIGHT / 2 + 4);
    }

    public void showSpinSelector() {
        spinSelector.reset();
        spinSelector.show(getWidth(), getHeight());
        repaint();
    }

    public void hideSpinSelector() {
        spinSelector.hide();
        repaint();
    }

    public void showAnnouncement(String text, String sub, Color color) {
        announcement.show(text, sub, color, null);
        repaint();
    }

    public void showAnnouncement(String text, Color color) {
        announcement.show(text, "", color, null);
        repaint();
    }

    public void showGameOver(String winnerName) {
        announcement.show(
                winnerName + " Wins!",
                "Returning to menu...",
                new Color(255, 215, 0),
                new Runnable() {
                    @Override
                    public void run() {
                        MainFrame.getInstance().showPanel("menu");
                    }
                });
        repaint();
    }

    public void setAimAngle(double angle) {
        this.aimAngle = angle;
    }

    public void setAimPower(double power) {
        this.aimPower = power;
    }
}