package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends JPanel {
    
    private JButton startButton;
    private JButton settingsButton;
    private JButton recordsButton;
    private JButton exitButton;
    
    public MainMenuPanel() {
        initializePanel();
        createComponents();
        layoutComponents();
        addEventHandlers();
    }
    
    private void initializePanel() {
        setBackground(new Color(20, 20, 40));
        setLayout(new BorderLayout());
    }
    
    private void createComponents() {
        startButton = createStyledButton("Start Game");
        settingsButton = createStyledButton("Settings");
        recordsButton = createStyledButton("Records");
        exitButton = createStyledButton("Exit");
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
        
        return button;
    }
    
    private void layoutComponents() {
        JLabel titleLabel = new JLabel("8-Ball Billiard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(220, 220, 220));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(20, 20, 40));
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Classic Pool Game");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(20, 20, 40));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));
        
        JPanel centerButtonPanel = new JPanel();
        centerButtonPanel.setBackground(new Color(20, 20, 40));
        centerButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JPanel innerButtonPanel = new JPanel();
        innerButtonPanel.setBackground(new Color(20, 20, 40));
        innerButtonPanel.setLayout(new BoxLayout(innerButtonPanel, BoxLayout.Y_AXIS));
        innerButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recordsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        innerButtonPanel.add(startButton);
        innerButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        innerButtonPanel.add(settingsButton);
        innerButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        innerButtonPanel.add(recordsButton);
        innerButtonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        innerButtonPanel.add(exitButton);
        
        centerButtonPanel.add(innerButtonPanel);
        buttonPanel.add(centerButtonPanel);
        
        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Version 1.0");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(footerLabel, BorderLayout.SOUTH);
    }
    
    private void addEventHandlers() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("game");
            }
        });
        
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("settings");
            }
        });
        
        recordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("records");
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(
                    MainMenuPanel.this,
                    "Are you sure you want to exit?",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}