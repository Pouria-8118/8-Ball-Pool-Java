package view;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private MainFrame frame;

    public MainMenuPanel(MainFrame frame) {
        this.frame = frame;

        setPreferredSize(new Dimension(400, 500));
        setBackground(new Color(20, 80, 40));
        setLayout(new GridBagLayout());

    }

}
