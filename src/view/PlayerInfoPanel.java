package view;

import model.GameState;
import model.Player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlayerInfoPanel extends JPanel {

    public static final Color PLAYER1_COLOR = new Color(80, 140, 255);
    public static final Color PLAYER2_COLOR = new Color(255, 90, 90);

    private JLabel p1NameLabel;
    private JLabel p1ScoreLabel;
    private JLabel p1GroupLabel;
    private JLabel p1BihLabel;

    private JLabel p2NameLabel;
    private JLabel p2ScoreLabel;
    private JLabel p2GroupLabel;
    private JLabel p2BihLabel;

    private JLabel turnLabel;
    private JLabel phaseLabel;

    public PlayerInfoPanel() {
        setBackground(new Color(13, 13, 13));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(45, 45, 45)));
        buildLayout();
    }

    private void buildLayout() {
        add(buildPlayerSide(true),  BorderLayout.WEST);
        add(buildCenter(),          BorderLayout.CENTER);
        add(buildPlayerSide(false), BorderLayout.EAST);
    }

    private JPanel buildPlayerSide(boolean isP1) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(13, 13, 13));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        panel.setPreferredSize(new Dimension(310, 90));

        Color nameColor = isP1 ? PLAYER1_COLOR : PLAYER2_COLOR;

        if (isP1) {
            p1NameLabel  = makeLabel("Player 1",    nameColor,                18, Font.BOLD);
            p1ScoreLabel = makeLabel("Score: 0",    new Color(210, 210, 210), 14, Font.PLAIN);
            p1GroupLabel = makeLabel("Group: Open", new Color(155, 155, 155), 13, Font.PLAIN);
            p1BihLabel   = makeLabel("",            new Color(255, 210, 50),  13, Font.BOLD);

            panel.add(p1NameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 2)));
            panel.add(p1ScoreLabel);
            panel.add(p1GroupLabel);
            panel.add(p1BihLabel);
        } else {
            p2NameLabel  = makeLabel("Player 2",    nameColor,                18, Font.BOLD);
            p2ScoreLabel = makeLabel("Score: 0",    new Color(210, 210, 210), 14, Font.PLAIN);
            p2GroupLabel = makeLabel("Group: Open", new Color(155, 155, 155), 13, Font.PLAIN);
            p2BihLabel   = makeLabel("",            new Color(255, 210, 50),  13, Font.BOLD);

            panel.add(p2NameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 2)));
            panel.add(p2ScoreLabel);
            panel.add(p2GroupLabel);
            panel.add(p2BihLabel);
        }

        return panel;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(13, 13, 13));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        turnLabel  = makeLabel("Turn: -", Color.WHITE,              18, Font.BOLD);
        phaseLabel = makeLabel("",        new Color(165, 165, 165), 12, Font.PLAIN);

        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phaseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(turnLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(phaseLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel lbl = new JLabel(text, SwingConstants.LEFT);
        lbl.setForeground(color);
        lbl.setFont(new Font("Arial", style, size));
        return lbl;
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
        p1BihLabel.setText(p1.hasBallInHand() ? "* Ball in Hand" : "");

        p2NameLabel.setText(p2.getName());
        p2ScoreLabel.setText("Score: " + p2.getScore());
        p2GroupLabel.setText("Group: " + groupText(p2.getGroup()));
        p2BihLabel.setText(p2.hasBallInHand() ? "* Ball in Hand" : "");

        if (current != null) {
            boolean isP1 = (current == p1);
            turnLabel.setForeground(isP1 ? PLAYER1_COLOR : PLAYER2_COLOR);
            turnLabel.setText(current.getName() + "'s Turn");
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
        return "Open";
    }

    private String phaseText(GameState.Phase phase) {
        if (phase == GameState.Phase.PLACE_CUE_BALL) {
            return "Place cue ball behind the line";
        }
        if (phase == GameState.Phase.SELECT_POCKET) {
            return "Click a pocket to select";
        }
        if (phase == GameState.Phase.SELECT_SPIN) {
            return "Choose your spin";
        }
        if (phase == GameState.Phase.AIMING) {
            return "Pull back and release to shoot";
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