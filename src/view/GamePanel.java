package view;

import controller.GameController;
import model.*;
import physics.PhysicsEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel {

    private GameController gameController;
    private PlayerInfoPanel playerInfoPanel;
    private JPanel spinMenuPanel;
    private JLabel gameOverLabel;

    private double aimAngle = 0;
    private double aimPower = 0;

    private static final int TABLE_X = 60;
    private static final int TABLE_Y = 60;
    private static final int TABLE_WIDTH = 800;
    private static final int TABLE_HEIGHT = 400;

    public GamePanel() {
        setLayout(null);
        setBackground(new Color(30, 30, 30));
        initComponents();
        addEventListeners();
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    private void initComponents() {
        playerInfoPanel = new PlayerInfoPanel();
        playerInfoPanel.setBounds(0, 480, 920, 80);
        add(playerInfoPanel);

        spinMenuPanel = createSpinMenu();
        spinMenuPanel.setVisible(false);
        add(spinMenuPanel);

        gameOverLabel = new JLabel("", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameOverLabel.setForeground(Color.YELLOW);
        gameOverLabel.setOpaque(true);
        gameOverLabel.setBackground(new Color(0, 0, 0, 180));
        gameOverLabel.setBounds(200, 200, 520, 80);
        gameOverLabel.setVisible(false);
        add(gameOverLabel);
    }

    private JPanel createSpinMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBounds(300, 200, 320, 70);

        JButton noneBtn = createSpinButton("Stop", SpinType.NONE);
        JButton topBtn = createSpinButton("Top", SpinType.TOP);
        JButton backBtn = createSpinButton("Back", SpinType.BACK);

        panel.add(noneBtn);
        panel.add(topBtn);
        panel.add(backBtn);

        return panel;
    }

    private JButton createSpinButton(String text, SpinType spin) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(80, 80, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(85, 40));
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTable(g2d);

        if (gameController != null && gameController.getGameState() != null) {
            GameState state = gameController.getGameState();
            drawBalls(g2d, state);
            drawAimLine(g2d, state);
            updatePlayerInfo(state);
        }
    }

    private void drawTable(Graphics2D g) {
        g.setColor(new Color(0, 90, 0));
        g.fillRect(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);

        g.setColor(new Color(100, 60, 20));
        g.setStroke(new BasicStroke(12));
        g.drawRect(TABLE_X - 12, TABLE_Y - 12, TABLE_WIDTH + 24, TABLE_HEIGHT + 24);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(0, 70, 0));
        g.setStroke(new BasicStroke(2));
        int breakX = TABLE_X + TABLE_WIDTH / 4;
        g.drawLine(breakX, TABLE_Y, breakX, TABLE_Y + TABLE_HEIGHT);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.BLACK);
        int[][] pocketPositions = {
            {TABLE_X, TABLE_Y},
            {TABLE_X + TABLE_WIDTH / 2, TABLE_Y},
            {TABLE_X + TABLE_WIDTH, TABLE_Y},
            {TABLE_X, TABLE_Y + TABLE_HEIGHT},
            {TABLE_X + TABLE_WIDTH / 2, TABLE_Y + TABLE_HEIGHT},
            {TABLE_X + TABLE_WIDTH, TABLE_Y + TABLE_HEIGHT}
        };

        for (int[] pos : pocketPositions) {
            g.fillOval(pos[0] - Pocket.RADIUS, pos[1] - Pocket.RADIUS, Pocket.RADIUS * 2, Pocket.RADIUS * 2);
        }
    }

    private void drawBalls(Graphics2D g, GameState state) {
        List<Ball> balls = state.getBalls();
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            drawBall(g, ball, state);
        }
    }

    private void drawBall(Graphics2D g, Ball ball, GameState state) {
        int bx = (int) ball.getX() - Ball.RADIUS;
        int by = (int) ball.getY() - Ball.RADIUS;
        int diam = Ball.RADIUS * 2;

        g.setColor(ball.getColor());
        g.fillOval(bx, by, diam, diam);

        if (ball.isStripe()) {
            g.setColor(Color.WHITE);
            g.fillRect(bx, (int) ball.getY() - 5, diam, 10);
            g.setColor(ball.getColor());
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        if (ball.getNumber() > 0) {
            String num = String.valueOf(ball.getNumber());
            FontMetrics fm = g.getFontMetrics();
            int tx = (int) ball.getX() - fm.stringWidth(num) / 2;
            int ty = (int) ball.getY() + fm.getAscent() / 2 - 2;
            g.setColor(ball.isStripe() ? ball.getColor() : Color.WHITE);
            g.drawString(num, tx, ty);
        }

        g.setColor(new Color(255, 255, 255, 60));
        g.drawOval(bx, by, diam, diam);

        if (state.getSelectedPocket() != null && !state.isTableOpen()) {
            Player current = state.getCurrentPlayer();
            boolean shouldHighlight = false;
            if (current.getGroup() == Player.Group.SOLID && !ball.isStripe() && !ball.isBlack() && !ball.isCueBall()) {
                shouldHighlight = true;
            }
            if (current.getGroup() == Player.Group.STRIPE && ball.isStripe()) {
                shouldHighlight = true;
            }
            if (state.getRemainingCount(current.getGroup()) == 0 && ball.isBlack()) {
                shouldHighlight = true;
            }
            if (shouldHighlight) {
                g.setColor(new Color(255, 255, 0, 120));
                g.setStroke(new BasicStroke(3));
                g.drawOval(bx - 3, by - 3, diam + 6, diam + 6);
                g.setStroke(new BasicStroke(1));
            }
        }
    }

    private void drawAimLine(Graphics2D g, GameState state) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }
        Ball cueBall = state.getCueBall();
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        int cx = (int) cueBall.getX();
        int cy = (int) cueBall.getY();
        int lineLen = 200;
        int endX = (int) (cx + Math.cos(aimAngle) * lineLen);
        int endY = (int) (cy + Math.sin(aimAngle) * lineLen);

        g.setColor(new Color(255, 255, 255, 80));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{6, 6}, 0));
        g.drawLine(cx, cy, endX, endY);
        g.setStroke(new BasicStroke(1));

        int barX = TABLE_X + TABLE_WIDTH + 20;
        int barY = TABLE_Y;
        int barH = TABLE_HEIGHT;
        int barW = 20;
        g.setColor(new Color(60, 60, 60));
        g.fillRect(barX, barY, barW, barH);
        int fillH = (int) (barH * aimPower / 18.0);
        Color powerColor = new Color(
                (int) Math.min(255, aimPower * 14),
                (int) Math.max(0, 255 - aimPower * 14),
                0
        );
        g.setColor(powerColor);
        g.fillRect(barX, barY + barH - fillH, barW, fillH);
        g.setColor(Color.GRAY);
        g.drawRect(barX, barY, barW, barH);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("PWR", barX, barY - 5);
    }

    private void updatePlayerInfo(GameState state) {
        if (playerInfoPanel != null) {
            playerInfoPanel.update(state);
        }
    }

    public void setAimAngle(double angle) {
        this.aimAngle = angle;
    }

    public void setAimPower(double power) {
        this.aimPower = power;
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
        gameOverLabel.setText(winnerName + " wins! Click anywhere to return to menu.");
        gameOverLabel.setVisible(true);
        repaint();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameOverLabel.setVisible(false);
                MainFrame.getInstance().showPanel("menu");
            }
        });
    }
}