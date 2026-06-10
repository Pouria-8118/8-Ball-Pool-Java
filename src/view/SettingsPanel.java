package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SettingsPanel extends JPanel {

    private static final String SETTINGS_FILE = "settings.properties";

    private JTextField p1Field;
    private JTextField p2Field;
    private JCheckBox  soundBox;

    public SettingsPanel() {
        setBackground(new Color(16, 16, 26));
        setLayout(new BorderLayout());
        buildUI();
        loadSettings();
    }

    private void buildUI() {
        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 36));
        title.setForeground(new Color(200, 170, 80));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 24, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(16, 16, 26));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 24, 12, 24);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        p1Field  = styledField("Player 1");
        p2Field  = styledField("Player 2");
        soundBox = new JCheckBox("Enable Sound");
        soundBox.setBackground(new Color(16, 16, 26));
        soundBox.setForeground(Color.WHITE);
        soundBox.setFont(new Font("Arial", Font.PLAIN, 15));
        soundBox.setSelected(true);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(styledLabel("Player 1 Name:"), gbc);
        gbc.gridx = 1;
        form.add(p1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(styledLabel("Player 2 Name:"), gbc);
        gbc.gridx = 1;
        form.add(p2Field, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(styledLabel("Sound:"), gbc);
        gbc.gridx = 1;
        form.add(soundBox, gbc);

        add(form, BorderLayout.CENTER);
        
        JButton cueBtn = styledBtn("Choose Cues", new Color(60, 80, 110));
        cueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showCueSelection();
            }
        });

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(styledLabel("Cue Sticks:"), gbc);
        gbc.gridx = 1;
        form.add(cueBtn, gbc);
        JPanel btns = new JPanel();
        btns.setBackground(new Color(16, 16, 26));
        btns.setBorder(BorderFactory.createEmptyBorder(0, 0, 34, 0));

        JButton saveBtn = styledBtn("Save", new Color(45, 130, 65));
        JButton backBtn = styledBtn("Back", new Color(55, 55, 75));

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                JOptionPane.showMessageDialog(SettingsPanel.this, "Settings saved.");
            }
        });

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("menu");
            }
        });

        btns.add(saveBtn);
        btns.add(backBtn);
        add(btns, BorderLayout.SOUTH);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        return lbl;
    }

    private JTextField styledField(String def) {
        JTextField field = new JTextField(def, 18);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(38, 38, 52));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 75, 105)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return field;
    }

    private JButton styledBtn(String text, Color base) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(base);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 50));
        return btn;
    }

    private void saveSettings() {
        Properties props = new Properties();
        props.setProperty("player1Name", p1Field.getText().trim());
        props.setProperty("player2Name", p2Field.getText().trim());
        props.setProperty("soundEnabled", String.valueOf(soundBox.isSelected()));
        try {
            FileOutputStream fos = new FileOutputStream(SETTINGS_FILE);
            props.store(fos, "Settings");
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
        p1Field.setText(props.getProperty("player1Name", "Player 1"));
        p2Field.setText(props.getProperty("player2Name", "Player 2"));
        soundBox.setSelected(Boolean.parseBoolean(
                props.getProperty("soundEnabled", "true")));
    }

    public String getPlayer1Name() {
        String name = p1Field.getText().trim();
        return name.isEmpty() ? "Player 1" : name;
    }

    public String getPlayer2Name() {
        String name = p2Field.getText().trim();
        return name.isEmpty() ? "Player 2" : name;
    }
}