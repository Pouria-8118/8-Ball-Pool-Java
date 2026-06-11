package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenuPanel extends JPanel {

    private JButton startBtn;
    private JButton settingsBtn;
    private JButton recordsBtn;
    private JButton exitBtn;

    public MainMenuPanel() {
        setBackground(new Color(12, 12, 20));
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        add(buildTitle(),   BorderLayout.NORTH);
        add(buildButtons(), BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
        wireActions();
    }

    private JPanel buildTitle() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(12, 12, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 0, 20, 0));

        JLabel title = new JLabel("8-Ball Billiard", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 54));
        title.setForeground(new Color(220, 190, 90));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Classic Pool Game", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.ITALIC, 17));
        sub.setForeground(new Color(130, 130, 130));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(sub);
        return panel;
    }

    private JPanel buildButtons() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(12, 12, 20));

        JPanel inner = new JPanel();
        inner.setBackground(new Color(12, 12, 20));
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(35, 0, 35, 0));

        startBtn    = menuButton("Start Game",  new Color(40, 150, 70));
        settingsBtn = menuButton("Settings",    new Color(55, 55, 85));
        recordsBtn  = menuButton("Records",     new Color(55, 55, 85));
        exitBtn     = menuButton("Exit",        new Color(110, 35, 35));

        inner.add(startBtn);
        inner.add(Box.createRigidArea(new Dimension(0, 14)));
        inner.add(settingsBtn);
        inner.add(Box.createRigidArea(new Dimension(0, 14)));
        inner.add(recordsBtn);
        inner.add(Box.createRigidArea(new Dimension(0, 14)));
        inner.add(exitBtn);

        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private JButton menuButton(String text, Color base) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setForeground(Color.WHITE);
        btn.setBackground(base);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(290, 60));
        btn.setPreferredSize(new Dimension(290, 60));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(base.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });

        return btn;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(12, 12, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel lbl = new JLabel("v2.1");
        lbl.setForeground(new Color(60, 60, 60));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(lbl);
        return panel;
    }

    private void wireActions() {
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("game");
            }
        });

        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("settings");
            }
        });

        recordsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("records");
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ans = JOptionPane.showConfirmDialog(
                        MainMenuPanel.this, "Exit the game?",
                        "Exit", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}