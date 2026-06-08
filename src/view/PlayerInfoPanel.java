package view;

import model.GameState;
import model.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    private JLabel player1NameLabel;
    private JLabel player1ScoreLabel;
    private JLabel player1GroupLabel;
    private JLabel player1BallInHandLabel;

    private JLabel player2NameLabel;
    private JLabel player2ScoreLabel;
    private JLabel player2GroupLabel;
    private JLabel player2BallInHandLabel;

    private JLabel turnLabel;
    private JLabel phaseLabel;

    public PlayerInfoPanel() {
        setBackground(new Color(20, 20, 20));
        setLayout(new GridLayout(1, 3));
        initComponents();
    }

    private void initComponents() {
        JPanel p1Panel = new JPanel();
        p1Panel.setBackground(new Color(20, 20, 20));
        p1Panel.setLayout(new BoxLayout(p1Panel, BoxLayout.Y_AXIS));
        p1Panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        player1NameLabel = makeLabel("Player 1", Color.WHITE, 14, Font.BOLD);
        player1ScoreLabel = makeLabel("Score: 0", Color.LIGHT_GRAY, 12, Font.PLAIN);
        player1GroupLabel = makeLabel("Group: -", Color.LIGHT_GRAY, 12, Font.PLAIN);
        player1BallInHandLabel = makeLabel("", Color.ORANGE, 12, Font.BOLD);

        p1Panel.add(player1NameLabel);
        p1Panel.add(player1ScoreLabel);
        p1Panel.add(player1GroupLabel);
        p1Panel.add(player1BallInHandLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(20, 20, 20));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        turnLabel = makeLabel("Turn: -", Color.YELLOW, 16, Font.BOLD);
        phaseLabel = makeLabel("Phase: -", new Color(180, 180, 180), 12, Font.PLAIN);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(turnLabel);
        centerPanel.add(phaseLabel);
        centerPanel.add(Box.createVerticalGlue());

        JPanel p2Panel = new JPanel();
        p2Panel.setBackground(new Color(20, 20, 20));
        p2Panel.setLayout(new BoxLayout(p2Panel, BoxLayout.Y_AXIS));
        p2Panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        player2NameLabel = makeLabel("Player 2", Color.WHITE, 14, Font.BOLD);
        player2ScoreLabel = makeLabel("Score: 0", Color.LIGHT_GRAY, 12, Font.PLAIN);
        player2GroupLabel = makeLabel("Group: -", Color.LIGHT_GRAY, 12, Font.PLAIN);
        player2BallInHandLabel = makeLabel("", Color.ORANGE, 12, Font.BOLD);

        p2Panel.add(player2NameLabel);
        p2Panel.add(player2ScoreLabel);
        p2Panel.add(player2GroupLabel);
        p2Panel.add(player2BallInHandLabel);

        add(p1Panel);
        add(centerPanel);
        add(p2Panel);
    }

    private JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Arial", style, size));
        return label;
    }

    public void update(GameState state) {
        if (state == null) {
            return;
        }

        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        Player current = state.getCurrentPlayer();

        player1NameLabel.setText(p1.getName());
        player1ScoreLabel.setText("Score: " + p1.getScore());
        player1GroupLabel.setText("Group: " + groupText(p1.getGroup()));
        player1BallInHandLabel.setText(p1.hasBallInHand() ? "Ball in Hand!" : "");

        player2NameLabel.setText(p2.getName());
        player2ScoreLabel.setText("Score: " + p2.getScore());
        player2GroupLabel.setText("Group: " + groupText(p2.getGroup()));
        player2BallInHandLabel.setText(p2.hasBallInHand() ? "Ball in Hand!" : "");

        if (current != null) {
            turnLabel.setText("Turn: " + current.getName());
        }

        phaseLabel.setText(phaseText(state.getPhase()));
    }

    private String groupText(Player.Group group) {
        if (group == Player.Group.SOLID) {
            return "Solid";
        }
        if (group == Player.Group.STRIPE) {
            return "Stripe";
        }
        return "-";
    }

    private String phaseText(GameState.Phase phase) {
        if (phase == GameState.Phase.PLACE_CUE_BALL) {
            return "Place cue ball behind line";
        }
        if (phase == GameState.Phase.SELECT_POCKET) {
            return "Click a pocket to select";
        }
        if (phase == GameState.Phase.SELECT_SPIN) {
            return "Select spin type";
        }
        if (phase == GameState.Phase.AIMING) {
            return "Aim and release to shoot";
        }
        if (phase == GameState.Phase.BALLS_MOVING) {
            return "Balls moving...";
        }
        if (phase == GameState.Phase.GAME_OVER) {
            return "Game Over";
        }
        return "";
    }
}