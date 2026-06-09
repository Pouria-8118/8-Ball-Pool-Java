package view;

import model.Ball;
import model.GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class CueStick {

    private static final int   CUE_LENGTH  = 230;
    private static final Color TIP_COLOR   = new Color(230, 220, 180);
    private static final Color SHAFT_COLOR = new Color(215, 175, 90);
    private static final Color WRAP_COLOR  = new Color(140, 80, 25);
    private static final Color BUTT_COLOR  = new Color(80, 40, 10);

    private double  strikeOffset;
    private boolean animating;
    private boolean goingForward;

    public CueStick() {
        this.strikeOffset  = 0;
        this.animating     = false;
        this.goingForward  = false;
    }

    public void startStrike() {
        this.animating    = true;
        this.goingForward = true;
        this.strikeOffset = 0;
    }

    public void tick() {
        if (!animating) {
            return;
        }

        if (goingForward) {
            strikeOffset += 5.0;
            if (strikeOffset >= 28) {
                strikeOffset  = 28;
                goingForward  = false;
            }
        } else {
            strikeOffset -= 2.5;
            if (strikeOffset <= 0) {
                strikeOffset = 0;
                animating    = false;
            }
        }
    }

    public boolean isAnimating() {
        return animating;
    }

    public void draw(Graphics2D g, GameState state, int mouseX, int mouseY) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cueBall = state.getCueBall();
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        double cx   = cueBall.getX();
        double cy   = cueBall.getY();
        double dx   = mouseX - cx;
        double dy   = mouseY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1) {
            return;
        }

        double nx = dx / dist;
        double ny = dy / dist;

        double pullDistance = Math.min(dist * 0.35, 60);
        double baseGap      = Ball.RADIUS + 6;
        double totalOffset  = baseGap + pullDistance + strikeOffset;

        double tipX = cx - nx * totalOffset;
        double tipY = cy - ny * totalOffset;
        double endX = tipX - nx * CUE_LENGTH;
        double endY = tipY - ny * CUE_LENGTH;

        double wrapRatio = 0.60;
        double wrapX     = tipX - nx * CUE_LENGTH * wrapRatio;
        double wrapY     = tipY - ny * CUE_LENGTH * wrapRatio;
        double midWrapX  = tipX - nx * CUE_LENGTH * 0.45;
        double midWrapY  = tipY - ny * CUE_LENGTH * 0.45;

        g.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(BUTT_COLOR);
        g.drawLine((int) wrapX, (int) wrapY, (int) endX, (int) endY);

        g.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(WRAP_COLOR);
        g.drawLine((int) wrapX, (int) wrapY, (int) midWrapX, (int) midWrapY);

        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(SHAFT_COLOR);
        g.drawLine((int) tipX, (int) tipY, (int) wrapX, (int) wrapY);

        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(TIP_COLOR);
        double shortEndX = tipX - nx * 18;
        double shortEndY = tipY - ny * 18;
        g.drawLine((int) tipX, (int) tipY, (int) shortEndX, (int) shortEndY);

        g.setStroke(new BasicStroke(1));
    }
}