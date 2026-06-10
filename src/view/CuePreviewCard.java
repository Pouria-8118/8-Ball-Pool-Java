package view;

import model.CueType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class CuePreviewCard extends JPanel {

    private static final int CARD_W = 340;
    private static final int CARD_H = 60;

    private CueType           cueType;
    private boolean           isP1;
    private CueSelectionPanel parent;
    private boolean           hovered;

    public CuePreviewCard(CueType type, boolean isP1, CueSelectionPanel parent) {
        this.cueType = type;
        this.isP1    = isP1;
        this.parent  = parent;
        this.hovered = false;

        setPreferredSize(new Dimension(CARD_W, CARD_H));
        setMaximumSize(new Dimension(CARD_W, CARD_H));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setSelection(isP1, cueType);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean selected = parent.isSelectedForPlayer(isP1, cueType);

        Color bgColor;
        if (selected) {
            bgColor = new Color(40, 70, 40);
        } else if (hovered) {
            bgColor = new Color(35, 35, 55);
        } else {
            bgColor = new Color(25, 25, 38);
        }

        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, CARD_W, CARD_H, 10, 10);

        if (selected) {
            g2.setColor(new Color(80, 220, 100));
            g2.setStroke(new BasicStroke(2f));
        } else {
            g2.setColor(new Color(60, 60, 80));
            g2.setStroke(new BasicStroke(1.2f));
        }
        g2.drawRoundRect(0, 0, CARD_W - 1, CARD_H - 1, 10, 10);
        g2.setStroke(new BasicStroke(1));

        drawCuePreview(g2);

        String name = cueName(cueType);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(selected ? Color.WHITE : new Color(190, 190, 190));
        g2.drawString(name, CARD_W - 110, CARD_H / 2 + 5);

        if (selected) {
            g2.setColor(new Color(80, 220, 100));
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Selected", CARD_W - 110, CARD_H / 2 + 20);
        }
    }

    private void drawCuePreview(Graphics2D g) {
        int previewX1 = 14;
        int previewX2 = CARD_W - 130;
        int midY      = CARD_H / 2;

        if (cueType == CueType.CLASSIC) {
            int wrapX = previewX1 + (previewX2 - previewX1) * 40 / 100;
            int midX  = previewX1 + (previewX2 - previewX1) * 55 / 100;

            g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(80, 40, 10));
            g.drawLine(wrapX, midY, previewX2, midY);

            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(140, 80, 25));
            g.drawLine(wrapX, midY, midX, midY);

            g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(215, 175, 90));
            g.drawLine(previewX1 + 14, midY, wrapX, midY);

            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(230, 220, 180));
            g.drawLine(previewX1, midY, previewX1 + 14, midY);

        } else if (cueType == CueType.SNOOKER) {
            int midX = previewX1 + (previewX2 - previewX1) * 45 / 100;

            g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(30, 15, 5));
            g.drawLine(midX, midY, previewX2, midY);

            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(180, 130, 60));
            g.drawLine(previewX1 + 16, midY, midX, midY);

            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(245, 235, 200));
            g.drawLine(previewX1, midY, previewX1 + 16, midY);

            g.setColor(new Color(200, 160, 80, 140));
            g.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i < 4; i++) {
                int lx = midX + (previewX2 - midX) * i / 4 + 8;
                g.drawLine(lx, midY - 4, lx, midY + 4);
            }

        } else if (cueType == CueType.FANCY) {
            int seg1X = previewX1 + (previewX2 - previewX1) * 30 / 100;
            int seg2X = previewX1 + (previewX2 - previewX1) * 60 / 100;

            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(60, 10, 10));
            g.drawLine(seg2X, midY, previewX2, midY);

            g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(180, 30, 30));
            g.drawLine(seg1X, midY, seg2X, midY);

            g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(230, 200, 50));
            g.drawLine(previewX1 + 12, midY, seg1X, midY);

            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(255, 245, 180));
            g.drawLine(previewX1, midY, previewX1 + 12, midY);

            g.setColor(new Color(255, 215, 0, 160));
            g.setStroke(new BasicStroke(1.5f));
            for (int i = 1; i <= 4; i++) {
                int lx = previewX1 + (seg1X - previewX1) * i / 5;
                g.drawLine(lx, midY - 3, lx, midY + 3);
            }

        } else if (cueType == CueType.DARK_WOOD) {
            int ringX = previewX1 + (previewX2 - previewX1) * 50 / 100;

            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(25, 12, 4));
            g.drawLine(ringX, midY, previewX2, midY);

            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(55, 28, 8));
            g.drawLine(previewX1 + 12, midY, ringX, midY);

            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(200, 180, 100));
            g.drawLine(ringX - 3, midY, ringX + 3, midY);

            g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(240, 230, 210));
            g.drawLine(previewX1, midY, previewX1 + 12, midY);

        } else if (cueType == CueType.IVORY) {
            int mid1X = previewX1 + (previewX2 - previewX1) * 38 / 100;
            int mid2X = previewX1 + (previewX2 - previewX1) * 62 / 100;

            g.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(240, 235, 215));
            g.drawLine(mid2X, midY, previewX2, midY);

            g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(210, 195, 160));
            g.drawLine(mid1X, midY, mid2X, midY);

            g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(245, 240, 225));
            g.drawLine(previewX1 + 10, midY, mid1X, midY);

            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(100, 80, 20));
            g.drawLine(previewX1, midY, previewX1 + 10, midY);

            g.setColor(new Color(180, 160, 100, 100));
            g.setStroke(new BasicStroke(2f));
            g.drawLine(mid1X - 2, midY - 4, mid1X - 2, midY + 4);
            g.drawLine(mid2X - 2, midY - 4, mid2X - 2, midY + 4);
        }

        g.setStroke(new BasicStroke(1));
    }

    private String cueName(CueType type) {
        if (type == CueType.CLASSIC) {
            return "Classic";
        }
        if (type == CueType.SNOOKER) {
            return "Snooker";
        }
        if (type == CueType.FANCY) {
            return "Fancy";
        }
        if (type == CueType.DARK_WOOD) {
            return "Dark Wood";
        }
        if (type == CueType.IVORY) {
            return "Ivory";
        }
        return "Unknown";
    }
} 