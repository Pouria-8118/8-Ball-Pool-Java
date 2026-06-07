package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private static MainFrame instance;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private MainFrame() {
        initializeFrame();
        setupMainPanel();
        addPanels();
    }
    
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }
    
    private void initializeFrame() {
        setTitle("8-Ball Billiard Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void setupMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.DARK_GRAY);
        add(mainPanel);
    }
    
    private void addPanels() {
        MainMenuPanel menuPanel = new MainMenuPanel();
        mainPanel.add(menuPanel, "menu");
        
        JPanel gamePanel = new JPanel();
        gamePanel.setBackground(new Color(0, 100, 0));
        gamePanel.add(new JLabel("Game Panel - Coming Soon"));
        mainPanel.add(gamePanel, "game");
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(new Color(50, 50, 50));
        settingsPanel.add(new JLabel("Settings Panel - Coming Soon"));
        mainPanel.add(settingsPanel, "settings");
        
        JPanel recordsPanel = new JPanel();
        recordsPanel.setBackground(new Color(30, 30, 30));
        recordsPanel.add(new JLabel("Records Panel - Coming Soon"));
        mainPanel.add(recordsPanel, "records");
        
        cardLayout.show(mainPanel, "menu");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
}