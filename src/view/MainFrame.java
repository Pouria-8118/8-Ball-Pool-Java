package view;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("8-Pool Billiard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        MainMenuPanel menuPanel = new MainMenuPanel(this);
        setContentPane(menuPanel);

        pack();
        setLocationRelativeTo(null);
    }

    public void showPanel(JPanel panel) {
        setContentPane(panel);
        setVisible(true);
        revalidate();
        repaint();
    }
}
