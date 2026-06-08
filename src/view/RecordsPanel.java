package view;

import controller.DatabaseController;
import model.GameRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RecordsPanel extends JPanel {

    private DatabaseController databaseController;
    private JPanel recordsListPanel;
    private JButton backButton;

    public RecordsPanel() {
        this.databaseController = new DatabaseController();
        setBackground(new Color(20, 20, 30));
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JLabel title = new JLabel("Game Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        recordsListPanel = new JPanel();
        recordsListPanel.setBackground(new Color(20, 20, 30));
        recordsListPanel.setLayout(new BoxLayout(recordsListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(recordsListPanel);
        scrollPane.setBackground(new Color(20, 20, 30));
        scrollPane.getViewport().setBackground(new Color(20, 20, 30));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 20, 30));
        backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(60, 60, 80));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("menu");
            }
        });
        bottomPanel.add(backButton);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshRecords() {
        recordsListPanel.removeAll();

        JPanel headerRow = createRow("Player 1", "Player 2", "Winner", "Date", true);
        recordsListPanel.add(headerRow);
        recordsListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        List<GameRecord> records = databaseController.loadRecords();

        if (records.isEmpty()) {
            JLabel noRecords = new JLabel("No records yet.");
            noRecords.setForeground(new Color(150, 150, 150));
            noRecords.setFont(new Font("Arial", Font.ITALIC, 16));
            noRecords.setAlignmentX(Component.CENTER_ALIGNMENT);
            recordsListPanel.add(noRecords);
        }

        for (GameRecord record : records) {
            JPanel row = createRow(
                    record.getPlayer1Name(),
                    record.getPlayer2Name(),
                    record.getWinnerName(),
                    record.getDate(),
                    false
            );
            recordsListPanel.add(row);
            recordsListPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        recordsListPanel.revalidate();
        recordsListPanel.repaint();
    }

    private JPanel createRow(String p1, String p2, String winner, String date, boolean isHeader) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setMaximumSize(new Dimension(800, 40));
        row.setBackground(isHeader ? new Color(50, 50, 70) : new Color(35, 35, 50));
        row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        int style = isHeader ? Font.BOLD : Font.PLAIN;
        Color color = isHeader ? Color.YELLOW : Color.WHITE;

        row.add(makeRowLabel(p1, style, color));
        row.add(makeRowLabel(p2, style, color));
        row.add(makeRowLabel(winner, style, isHeader ? Color.YELLOW : new Color(100, 255, 100)));
        row.add(makeRowLabel(date, style, isHeader ? Color.YELLOW : new Color(180, 180, 180)));

        return row;
    }

    private JLabel makeRowLabel(String text, int style, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", style, 14));
        label.setForeground(color);
        return label;
    }
}