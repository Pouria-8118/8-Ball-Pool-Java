package view;

import model.CueType;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CueSelectionPanel extends JPanel {

    private CueType selectedP1 = CueType.CLASSIC;
    private CueType selectedP2 = CueType.CLASSIC;

    private String  player1Name;
    private String  player2Name;

    private JButton saveBtn;
    private JButton backBtn;

    public CueSelectionPanel() {
        this.player1Name = "Player 1";
        this.player2Name = "Player 2";
        setBackground(new Color(14, 14, 22));
        setLayout(new BorderLayout());
        buildUI();
    }

    public void setPlayerNames(String p1, String p2) {
        this.player1Name = p1;
        this.player2Name = p2;
        repaint();
    }

    private void buildUI() {
        JLabel title = new JLabel("Choose Your Cue", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 36));
        title.setForeground(new Color(200, 170, 80));
        title.setBorder(BorderFactory.createEmptyBorder(36, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel twoSides = new JPanel(new GridLayout(1, 2, 20, 0));
        twoSides.setBackground(new Color(14, 14, 22));
        twoSides.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        twoSides.add(buildPlayerSide(true));
        twoSides.add(buildPlayerSide(false));

        add(twoSides, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(14, 14, 22));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 28, 0));

        saveBtn = styledBtn("Confirm Selection", new Color(45, 130, 65));
        backBtn = styledBtn("Back",              new Color(55, 55, 80));

        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainFrame.getInstance().onCueSelectionDone(selectedP1, selectedP2);
            }
        });

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainFrame.getInstance().showPanel("settings");
            }
        });

        bottom.add(saveBtn);
        bottom.add(Box.createRigidArea(new Dimension(16, 0)));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildPlayerSide(boolean isP1) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 18, 28));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 70)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        Color  nameColor = isP1 ? PlayerInfoPanel.PLAYER1_COLOR
                                : PlayerInfoPanel.PLAYER2_COLOR;
        String labelText = isP1 ? player1Name : player2Name;

        JLabel nameLabel = new JLabel(labelText, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(nameColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        for (CueType type : CueType.values()) {
            CuePreviewCard card = new CuePreviewCard(type, isP1, this);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(card);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JButton styledBtn(String text, Color base) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(base);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(220, 50));
        return btn;
    }

    public void setSelection(boolean isP1, CueType type) {
        if (isP1) {
            selectedP1 = type;
        } else {
            selectedP2 = type;
        }
        repaint();
    }

    public CueType getSelectedP1() {
        return selectedP1;
    }

    public CueType getSelectedP2() {
        return selectedP2;
    }

    public boolean isSelectedForPlayer(boolean isP1, CueType type) {
        if (isP1) {
            return selectedP1 == type;
        }
        return selectedP2 == type;
    }
}