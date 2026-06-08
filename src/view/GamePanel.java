package view;

import controller.GameController;
import model.Ball;
import model.GameState;
import model.Player;
import model.Pocket;
import model.SpinType;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class GamePanel extends JPanel {

    private static final int TABLE_X      = 60;
    private static final int TABLE_Y      = 80;
    private static final int TABLE_WIDTH  = 800;
    private static final int TABLE_HEIGHT = 400;

    private static final Color PLAYER1_COLOR = new Color(80, 140, 255);
    private static final Color PLAYER2_COLOR = new Color(255, 90, 90);

    private GameController gameController;
    private PlayerInfoPanel playerInfoPanel;
    private JPanel          spinMenuPanel;

    private double aimAngle  = 0;
    private double aimPower  = 0;
    private int    mouseX    = 0;
    private int    mouseY    = 0;

    private String  announcementText  = "";
    private int     announcementAlpha = 0;
    private Timer   announcementTimer;

    public GamePanel() {
        setLayout(null);
        setBackground(new Color(25, 25, 25));
        initComponents();
        addEventListeners();
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    private void initComponents() {
        playerInfoPanel = new PlayerInfoPanel();
        playerInfoPanel.setBounds(0, 500, 960, 80);
        add(playerInfoPanel);

        spinMenuPanel = buildSpinMenu();
        spinMenuPanel.setVisible(false);
        add(spinMenuPanel);
    }

    private JPanel buildSpinMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panel.setBackground(new Color(40, 40, 40));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        panel.setBounds(330, 240, 300, 65);

        JLabel label = new JLabel("Select Spin:");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(label);

        panel.add(buildSpinButton("Stop",  SpinType.NONE));
        panel.add(buildSpinButton("Top",   SpinType.TOP));
        panel.add(buildSpinButton("Back",  SpinType.BACK));

        return panel;
    }

    private JButton buildSpinButton(String text, SpinType spin) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(70, 70, 90));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(70, 36));

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameController != null) {
                    gameController.onSpinSelected(spin);
                }
            }
        });

        return btn;
    }

    private void addEventListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                if (gameController != null) {
                    gameController.onMouseMoved(e.getX(), e.getY());
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameController != null) {
                    gameController.onMousePressed(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (gameController != null) {
                    gameController.onMouseReleased(e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTable(g2);

        if (gameController != null && gameController.getGameState() != null) {
            GameState state = gameController.getGameState();
            drawBalls(g2, state);
            drawCue(g2, state);
            drawAimLine(g2, state);
            drawPowerBar(g2, state);
            playerInfoPanel.update(state);
        }

        drawAnnouncement(g2);
    }

    private void drawTable(Graphics2D g) {
        g.setColor(new Color(100, 60, 20));
        g.fillRoundRect(TABLE_X - 20, TABLE_Y - 20, TABLE_WIDTH + 40, TABLE_HEIGHT + 40, 10, 10);

        g.setColor(new Color(34, 120, 34));
        g.fillRect(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);

        g.setColor(new Color(28, 100, 28));
        for (int i = 0; i < TABLE_WIDTH; i += 40) {
            g.setColor(i % 80 == 0 ? new Color(30, 110, 30) : new Color(34, 120, 34));
            g.fillRect(TABLE_X + i, TABLE_Y, 40, TABLE_HEIGHT);
        }

        g.setColor(new Color(0, 160, 0));
        g.setStroke(new BasicStroke(2));
        int breakX = TABLE_X + TABLE_WIDTH / 4;
        g.drawLine(breakX, TABLE_Y, breakX, TABLE_Y + TABLE_HEIGHT);

        g.setColor(new Color(0, 140, 0));
        int spotX = TABLE_X + TABLE_WIDTH * 3 / 4;
        int spotY = TABLE_Y + TABLE_HEIGHT / 2;
        g.fillOval(spotX - 4, spotY - 4, 8, 8);

        g.setStroke(new BasicStroke(1));

        for (double[] pocket : getPocketPositions()) {
            double px = pocket[0];
            double py = pocket[1];
            g.setColor(new Color(10, 10, 10));
            g.fillOval((int) px - Pocket.RADIUS, (int) py - Pocket.RADIUS,
                       Pocket.RADIUS * 2, Pocket.RADIUS * 2);
            g.setColor(new Color(50, 30, 10));
            g.setStroke(new BasicStroke(2));
            g.drawOval((int) px - Pocket.RADIUS, (int) py - Pocket.RADIUS,
                       Pocket.RADIUS * 2, Pocket.RADIUS * 2);
            g.setStroke(new BasicStroke(1));
        }
    }

    private double[][] getPocketPositions() {
        return new double[][] {
            {TABLE_X,                    TABLE_Y},
            {TABLE_X + TABLE_WIDTH / 2,  TABLE_Y},
            {TABLE_X + TABLE_WIDTH,      TABLE_Y},
            {TABLE_X,                    TABLE_Y + TABLE_HEIGHT},
            {TABLE_X + TABLE_WIDTH / 2,  TABLE_Y + TABLE_HEIGHT},
            {TABLE_X + TABLE_WIDTH,      TABLE_Y + TABLE_HEIGHT}
        };
    }

    private void drawBalls(Graphics2D g, GameState state) {
        List<Ball> balls = state.getBalls();

        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            drawSingleBall(g, ball, state);
        }
    }

    private void drawSingleBall(Graphics2D g, Ball ball, GameState state) {
        int bx   = (int) ball.getX() - Ball.RADIUS;
        int by   = (int) ball.getY() - Ball.RADIUS;
        int diam = Ball.RADIUS * 2;

        g.setColor(ball.getColor());
        g.fillOval(bx, by, diam, diam);

        if (ball.isStripe()) {
            g.setColor(Color.WHITE);
            g.fillRect(bx, (int) ball.getY() - 5, diam, 10);
            g.setColor(ball.getColor());
            g.fillOval(bx, by, diam, diam);
            g.setColor(Color.WHITE);
            g.fillRect(bx + 3, (int) ball.getY() - 5, diam - 6, 10);
            g.setColor(ball.getColor());
            g.fillRect(bx + 3, (int) ball.getY() - 5, diam - 6, 10);
        }

        if (ball.getNumber() > 0) {
            g.setColor(Color.WHITE);
            g.fillOval((int) ball.getX() - 7, (int) ball.getY() - 7, 14, 14);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 9));
            FontMetrics fm  = g.getFontMetrics();
            String      num = String.valueOf(ball.getNumber());
            int tx = (int) ball.getX() - fm.stringWidth(num) / 2;
            int ty = (int) ball.getY() + fm.getAscent() / 2 - 1;
            g.drawString(num, tx, ty);
        }

        g.setColor(new Color(255, 255, 255, 50));
        g.fillOval(bx + 3, by + 2, diam / 3, diam / 4);

        boolean shouldHighlight = checkShouldHighlight(ball, state);
        if (shouldHighlight) {
            g.setColor(new Color(255, 255, 0, 160));
            g.setStroke(new BasicStroke(3));
            g.drawOval(bx - 3, by - 3, diam + 6, diam + 6);
            g.setStroke(new BasicStroke(1));
        }

        if (state.getSelectedPocket() != null) {
            Pocket sel = state.getSelectedPocket();
            double dx   = ball.getX() - sel.getX();
            double dy   = ball.getY() - sel.getY();
            boolean isSelectedPocket = Math.sqrt(dx * dx + dy * dy) < Pocket.RADIUS * 2;
            if (isSelectedPocket) {
                g.setColor(new Color(0, 255, 100, 180));
                g.setStroke(new BasicStroke(2));
                g.drawOval((int) sel.getX() - Pocket.RADIUS - 3,
                           (int) sel.getY() - Pocket.RADIUS - 3,
                           (Pocket.RADIUS + 3) * 2,
                           (Pocket.RADIUS + 3) * 2);
                g.setStroke(new BasicStroke(1));
            }
        }
    }

    private boolean checkShouldHighlight(Ball ball, GameState state) {
        if (ball.isCueBall()) {
            return false;
        }
        if (state.getPhase() != GameState.Phase.AIMING
            && state.getPhase() != GameState.Phase.SELECT_POCKET) {
            return false;
        }
        if (state.isTableOpen()) {
            return false;
        }

        Player current = state.getCurrentPlayer();

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

    private void drawCue(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cueBall = state.getCueBall();
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        double cx = cueBall.getX();
        double cy = cueBall.getY();

        double dx   = mouseX - cx;
        double dy   = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) {
            return;
        }

        double nx = dx / dist;
        double ny = dy / dist;

        double pullBack  = Math.min(dist * 0.4, 60);
        double cueLength = 200;

        double tipX  = cx - nx * (Ball.RADIUS + 4);
        double tipY  = cy - ny * (Ball.RADIUS + 4);
        double baseX = tipX - nx * (cueLength + pullBack);
        double baseY = tipY - ny * (cueLength + pullBack);

        g.setColor(new Color(210, 170, 100));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) tipX, (int) tipY, (int) baseX, (int) baseY);

        g.setColor(new Color(240, 210, 140));
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) tipX, (int) tipY, (int) baseX, (int) baseY);

        g.setColor(new Color(180, 100, 40));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double wrapStart = 0.7;
        int wrapTipX  = (int) (tipX  + nx * cueLength * wrapStart);
        int wrapTipY  = (int) (tipY  + ny * cueLength * wrapStart);
        int wrapBaseX = (int) baseX;
        int wrapBaseY = (int) baseY;
        g.drawLine(wrapTipX, wrapTipY, wrapBaseX, wrapBaseY);

        g.setColor(new Color(60, 30, 10));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) tipX, (int) tipY,
                   (int) (tipX  - nx * cueLength * 0.15),
                   (int) (tipY  - ny * cueLength * 0.15));

        g.setStroke(new BasicStroke(1));
    }

    private void drawAimLine(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cueBall = state.getCueBall();
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        int cx      = (int) cueBall.getX();
        int cy      = (int) cueBall.getY();
        int lineLen = 180;
        int endX    = (int) (cx + Math.cos(aimAngle) * lineLen);
        int endY    = (int) (cy + Math.sin(aimAngle) * lineLen);

        g.setColor(new Color(255, 255, 255, 70));
        g.setStroke(new BasicStroke(1.2f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND,
            0, new float[]{5, 7}, 0));
        g.drawLine(cx, cy, endX, endY);
        g.setStroke(new BasicStroke(1));
    }

    private void drawPowerBar(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        int barX = TABLE_X + TABLE_WIDTH + 30;
        int barY = TABLE_Y;
        int barW = 22;
        int barH = TABLE_HEIGHT;

        g.setColor(new Color(40, 40, 40));
        g.fillRoundRect(barX, barY, barW, barH, 6, 6);

        int fillH = (int) (barH * aimPower / 18.0);
        int r     = (int) Math.min(255, aimPower * 14);
        int gr    = (int) Math.max(0, 255 - aimPower * 14);
        g.setColor(new Color(r, gr, 0));
        g.fillRoundRect(barX, barY + barH - fillH, barW, fillH, 6, 6);

        g.setColor(new Color(120, 120, 120));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(barX, barY, barW, barH, 6, 6);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("PWR", barX - 2, barY - 6);
    }

    private void drawAnnouncement(Graphics2D g) {
        if (announcementText.isEmpty() || announcementAlpha <= 0) {
            return;
        }

        int panelW = getWidth();
        int panelH = getHeight();

        g.setColor(new Color(0, 0, 0, Math.min(announcementAlpha / 2, 150)));
        g.fillRoundRect(panelW / 2 - 220, panelH / 2 - 40, 440, 80, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm    = g.getFontMetrics();
        int         textW = fm.stringWidth(announcementText);
        int         tx    = panelW / 2 - textW / 2;
        int         ty    = panelH / 2 + fm.getAscent() / 2 - 5;

        g.setColor(new Color(0, 0, 0, announcementAlpha));
        g.drawString(announcementText, tx + 2, ty + 2);

        g.setColor(new Color(255, 255, 255, announcementAlpha));
        g.drawString(announcementText, tx, ty);
    }

    public void showAnnouncement(String text) {
        announcementText  = text;
        announcementAlpha = 255;

        if (announcementTimer != null) {
            announcementTimer.stop();
        }

        announcementTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                announcementAlpha -= 5;
                if (announcementAlpha <= 0) {
                    announcementAlpha = 0;
                    announcementTimer.stop();
                }
                repaint();
            }
        });

        announcementTimer.setInitialDelay(1200);
        announcementTimer.start();
    }

    public void showSpinMenu() {
        spinMenuPanel.setVisible(true);
        repaint();
    }

    public void hideSpinMenu() {
        spinMenuPanel.setVisible(false);
        repaint();
    }

    public void showGameOver(String winnerName) {
        showAnnouncement(winnerName + " Wins!");

        Timer delayTimer = new Timer(2500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("menu");
            }
        });

        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void setAimAngle(double angle) {
        this.aimAngle = angle;
    }

    public void setAimPower(double power) {
        this.aimPower = power;
    }
}