package view;

import model.GameState;
import model.Player;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class PlayerInfoPanel extends JPanel {

    private static final Color PLAYER1_COLOR = new Color(80, 140, 255);
    private static final Color PLAYER2_COLOR = new Color(255, 90,  90);

    private JLabel p1NameLabel;
    private JLabel p1ScoreLabel;
    private JLabel p1GroupLabel;
    private JLabel p1BallInHandLabel;

    private JLabel p2NameLabel;
    private JLabel p2ScoreLabel;
    private JLabel p2GroupLabel;
    private JLabel p2BallInHandLabel;

    private JLabel turnLabel;
    private JLabel phaseLabel;

    public PlayerInfoPanel() {
        setBackground(new Color(18, 18, 18));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(60, 60, 60)));
        buildUI();
    }

    private void buildUI() {
        JPanel p1Panel = buildPlayerPanel(true);
        JPanel centerPanel = buildCenterPanel();
        JPanel p2Panel = buildPlayerPanel(false);

        add(p1Panel,     BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(p2Panel,     BorderLayout.EAST);
    }

    private JPanel buildPlayerPanel(boolean isPlayer1) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 18, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        panel.setPreferredSize(new Dimension(300, 80));

        Color nameColor = isPlayer1 ? PLAYER1_COLOR : PLAYER2_COLOR;

        if (isPlayer1) {
            p1NameLabel       = makeLabel("Player 1", nameColor, 16, Font.BOLD);
            p1ScoreLabel      = makeLabel("Score: 0", Color.LIGHT_GRAY, 13, Font.PLAIN);
            p1GroupLabel      = makeLabel("Group: -", Color.LIGHT_GRAY, 13, Font.PLAIN);
            p1BallInHandLabel = makeLabel("",          new Color(255, 200, 50), 12, Font.BOLD);

            panel.add(p1NameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(p1ScoreLabel);
            panel.add(p1GroupLabel);
            panel.add(p1BallInHandLabel);
        } else {
            p2NameLabel       = makeLabel("Player 2", nameColor, 16, Font.BOLD);
            p2ScoreLabel      = makeLabel("Score: 0", Color.LIGHT_GRAY, 13, Font.PLAIN);
            p2GroupLabel      = makeLabel("Group: -", Color.LIGHT_GRAY, 13, Font.PLAIN);
            p2BallInHandLabel = makeLabel("",          new Color(255, 200, 50), 12, Font.BOLD);

            panel.add(p2NameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(p2ScoreLabel);
            panel.add(p2GroupLabel);
            panel.add(p2BallInHandLabel);
        }

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 18, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        turnLabel  = makeLabel("Turn: -",   Color.YELLOW,              18, Font.BOLD);
        phaseLabel = makeLabel("Phase: -",  new Color(180, 180, 180),  12, Font.PLAIN);

        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(turnLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(phaseLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setForeground(color);
        label.setFont(new Font("Arial", style, size));
        return label;
    }

    public void update(GameState state) {
        if (state == null) {
            return;
        }

        Player p1      = state.getPlayer1();
        Player p2      = state.getPlayer2();
        Player current = state.getCurrentPlayer();

        p1NameLabel.setText(p1.getName());
        p1ScoreLabel.setText("Score: " + p1.getScore());
        p1GroupLabel.setText("Group: " + groupText(p1.getGroup()));
        p1BallInHandLabel.setText(p1.hasBallInHand() ? "⚑ Ball in Hand" : "");

        p2NameLabel.setText(p2.getName());
        p2ScoreLabel.setText("Score: " + p2.getScore());
        p2GroupLabel.setText("Group: " + groupText(p2.getGroup()));
        p2BallInHandLabel.setText(p2.hasBallInHand() ? "⚑ Ball in Hand" : "");

        if (current != null) {
            boolean isP1    = current == p1;
            Color   tColor  = isP1 ? PLAYER1_COLOR : PLAYER2_COLOR;
            turnLabel.setForeground(tColor);
            turnLabel.setText("▶  " + current.getName() + "'s Turn");
        }

        phaseLabel.setText(phaseText(state.getPhase()));
    }

    private String groupText(Player.Group group) {
        if (group == Player.Group.SOLID) {
            return "● Solid";
        }
        if (group == Player.Group.STRIPE) {
            return "◑ Stripe";
        }
        return "— Open";
    }

    private String phaseText(GameState.Phase phase) {
        if (phase == GameState.Phase.PLACE_CUE_BALL) {
            return "Place cue ball behind the line";
        }
        if (phase == GameState.Phase.SELECT_POCKET) {
            return "Click a pocket to aim for";
        }
        if (phase == GameState.Phase.SELECT_SPIN) {
            return "Choose your spin";
        }
        if (phase == GameState.Phase.AIMING) {
            return "Hold & release to shoot";
        }
        if (phase == GameState.Phase.BALLS_MOVING) {
            return "...";
        }
        if (phase == GameState.Phase.GAME_OVER) {
            return "Game Over";
        }
        return "";
    }
}