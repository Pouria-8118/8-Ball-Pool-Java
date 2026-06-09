package view;

import controller.GameController;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    private static MainFrame instance;

    private CardLayout    cardLayout;
    private JPanel        mainPanel;
    private GamePanel     gamePanel;
    private SettingsPanel settingsPanel;
    private RecordsPanel  recordsPanel;

    private MainFrame() {
        setTitle("8-Ball Billiard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildPanels();
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private void buildPanels() {
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(Color.BLACK);
        add(mainPanel);

        mainPanel.add(new MainMenuPanel(), "menu");

        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, "game");

        settingsPanel = new SettingsPanel();
        mainPanel.add(settingsPanel, "settings");

        recordsPanel = new RecordsPanel();
        mainPanel.add(recordsPanel, "records");

        cardLayout.show(mainPanel, "menu");
        setSize(960, 630);
        setMinimumSize(new Dimension(960, 630));
        setLocationRelativeTo(null);
    }

    public void showPanel(String name) {
        if (name.equals("game")) {
            String p1         = settingsPanel.getPlayer1Name();
            String p2         = settingsPanel.getPlayer2Name();
            GameController gc = new GameController(gamePanel);
            gamePanel.setGameController(gc);
            gc.startNewGame(p1, p2);
        }
        if (name.equals("records")) {
            recordsPanel.refresh();
        }
        cardLayout.show(mainPanel, name);
    }
}