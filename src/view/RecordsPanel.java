package view;

import controller.DatabaseController;
import model.GameRecord;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class RecordsPanel extends JPanel {

    private DatabaseController dbController;
    private JPanel             listPanel;

    public RecordsPanel() {
        this.dbController = new DatabaseController();
        setBackground(new Color(12, 12, 20));
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JLabel title = new JLabel("Game Records", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 36));
        title.setForeground(new Color(200, 170, 80));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setBackground(new Color(12, 12, 20));
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBackground(new Color(12, 12, 20));
        scroll.getViewport().setBackground(new Color(12, 12, 20));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(12, 12, 20));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 26, 0));

        JButton back = new JButton("Back to Menu");
        back.setFont(new Font("Arial", Font.BOLD, 16));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(55, 55, 80));
        back.setFocusPainted(false);
        back.setBorderPainted(false);
        back.setPreferredSize(new Dimension(200, 50));
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.getInstance().showPanel("menu");
            }
        });

        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    public void refresh() {
        listPanel.removeAll();

        listPanel.add(buildRow("Player 1", "Player 2", "Winner", "Date", true));
        listPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        List<GameRecord> records = dbController.loadRecords();

        if (records.isEmpty()) {
            JLabel empty = new JLabel("No records yet.");
            empty.setForeground(new Color(110, 110, 110));
            empty.setFont(new Font("Arial", Font.ITALIC, 15));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createRigidArea(new Dimension(0, 22)));
            listPanel.add(empty);
        }

        for (GameRecord r : records) {
            listPanel.add(buildRow(
                    r.getPlayer1Name(), r.getPlayer2Name(),
                    r.getWinnerName(),  r.getDate(), false));
            listPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildRow(String p1, String p2, String winner,
                            String date, boolean header) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setMaximumSize(new Dimension(900, 44));
        row.setBackground(header ? new Color(38, 38, 58) : new Color(22, 22, 34));
        row.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));

        int   style  = header ? Font.BOLD  : Font.PLAIN;
        Color defCol = header ? Color.YELLOW : Color.WHITE;
        Color winCol = header ? Color.YELLOW : new Color(80, 220, 100);
        Color datCol = header ? Color.YELLOW : new Color(155, 155, 155);

        row.add(rowLabel(p1,     style, defCol));
        row.add(rowLabel(p2,     style, defCol));
        row.add(rowLabel(winner, style, winCol));
        row.add(rowLabel(date,   style, datCol));

        return row;
    }

    private JLabel rowLabel(String text, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", style, 14));
        lbl.setForeground(color);
        return lbl;
    }
}