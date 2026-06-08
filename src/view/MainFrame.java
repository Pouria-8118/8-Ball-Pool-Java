package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private static MainFrame instance;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private SettingsPanel settingsPanel;
    private RecordsPanel recordsPanel;

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
        setSize(960, 620);
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

        gamePanel = new GamePanel();
        GameController gameController = new GameController(gamePanel);
        gamePanel.setGameController(gameController);
        mainPanel.add(gamePanel, "game");

        settingsPanel = new SettingsPanel();
        mainPanel.add(settingsPanel, "settings");

        recordsPanel = new RecordsPanel();
        mainPanel.add(recordsPanel, "records");

        cardLayout.show(mainPanel, "menu");
    }

    public void showPanel(String panelName) {
        if (panelName.equals("game")) {
            SettingsPanel sp = (SettingsPanel) getSettingsPanel();
            String p1 = sp.getPlayer1Name();
            String p2 = sp.getPlayer2Name();
            GameController controller = new GameController(gamePanel);
            gamePanel.setGameController(controller);
            controller.startNewGame(p1, p2);
        }
        if (panelName.equals("records")) {
            recordsPanel.refreshRecords();
        }
        cardLayout.show(mainPanel, panelName);
    }

    private SettingsPanel getSettingsPanel() {
        return settingsPanel;
    }
}