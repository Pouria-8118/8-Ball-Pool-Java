package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class SettingsPanel extends JPanel {

    private JTextField player1NameField;
    private JTextField player2NameField;
    private JCheckBox soundCheckBox;
    private JButton saveButton;
    private JButton backButton;

    private static final String SETTINGS_FILE = "settings.properties";

    public SettingsPanel() {
        setBackground(new Color(30, 30, 40));
        setLayout(new BorderLayout());
        initComponents();
        loadSettings();
    }

    private void initComponents() {
        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(30, 30, 40));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel p1Label = makeLabel("Player 1 Name:");
        player1NameField = makeTextField("Player 1");

        JLabel p2Label = makeLabel("Player 2 Name:");
        player2NameField = makeTextField("Player 2");

        JLabel soundLabel = makeLabel("Sound:");
        soundCheckBox = new JCheckBox("Enable");
        soundCheckBox.setBackground(new Color(30, 30, 40));
        soundCheckBox.setForeground(Color.WHITE);
        soundCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        soundCheckBox.setSelected(true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(p1Label, gbc);
        gbc.gridx = 1;
        formPanel.add(player1NameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(p2Label, gbc);
        gbc.gridx = 1;
        formPanel.add(player2NameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(soundLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(soundCheckBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 40));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        saveButton = makeButton("Save Settings");
        backButton = makeButton("Back to Menu");

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                JOptionPane.showMessageDialog(SettingsPanel.this, "Settings saved!");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("menu");
            }
        });
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private JTextField makeTextField(String defaultText) {
        JTextField field = new JTextField(defaultText, 20);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(50, 50, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(60, 60, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        return btn;
    }

    private void saveSettings() {
        Properties props = new Properties();
        props.setProperty("player1Name", player1NameField.getText());
        props.setProperty("player2Name", player2NameField.getText());
        props.setProperty("soundEnabled", String.valueOf(soundCheckBox.isSelected()));
        try {
            FileOutputStream fos = new FileOutputStream(SETTINGS_FILE);
            props.store(fos, "Game Settings");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return;
        }
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        player1NameField.setText(props.getProperty("player1Name", "Player 1"));
        player2NameField.setText(props.getProperty("player2Name", "Player 2"));
        soundCheckBox.setSelected(Boolean.parseBoolean(props.getProperty("soundEnabled", "true")));
    }

    public String getPlayer1Name() {
        return player1NameField.getText();
    }
    public String getPlayer2Name() {
        return player2NameField.getText();
    }
}