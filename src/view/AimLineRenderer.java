package view;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.Table;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class AimLineRenderer {

    private static final double STEP       = 2.0;
    private static final int    MAX_STEPS  = 2000;
    private static final float  LINE_WIDTH = 1.2f;

    public void draw(Graphics2D g, GameState state, double aimAngle) {
        if (state.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cue = state.getCueBall();
        if (cue == null || cue.isPocketed()) {
            return;
        }

        double dirX = Math.cos(aimAngle);
        double dirY = Math.sin(aimAngle);

        RayResult result = castRay(cue.getX(), cue.getY(),
                                   dirX, dirY, state, cue);

        drawCuePath(g, cue.getX(), cue.getY(),
                    result.hitX, result.hitY);

        if (result.hitType == HitType.BALL) {
            drawBallHitResult(g, result, dirX, dirY, state, cue.getX(), cue.getY());
        } else if (result.hitType == HitType.WALL) {
            drawWallReflection(g, result, dirX, dirY, state);
        } else if (result.hitType == HitType.POCKET) {
            drawPocketIndicator(g, result.hitX, result.hitY);
        }
    }

    private void drawCuePath(Graphics2D g,
                              double startX, double startY,
                              double endX,   double endY) {
        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(LINE_WIDTH,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{5f, 7f}, 0));
        g.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
        g.setStroke(new BasicStroke(1));
    }

    private void drawBallHitResult(Graphics2D g, RayResult result,
                                    double inDirX, double inDirY,
                                    GameState state, double cueStartX, double cueStartY) {
        g.setColor(new Color(255, 220, 80, 130));
        g.setStroke(new BasicStroke(2f));
        int hr = Ball.RADIUS;
        g.drawOval((int) result.hitX - hr, (int) result.hitY - hr,
                   hr * 2, hr * 2);
        g.setStroke(new BasicStroke(1));

        double hitNx = result.normalX;
        double hitNy = result.normalY;

        double dot        = inDirX * hitNx + inDirY * hitNy;
        boolean isFullHit = dot > 0.92;

        double targetDirX = hitNx;
        double targetDirY = hitNy;

        double targetLineLen = 70;
        g.setColor(new Color(255, 180, 50, 120));
        g.setStroke(new BasicStroke(1.5f,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{4f, 5f}, 0));
        g.drawLine((int) result.hitX,
                   (int) result.hitY,
                   (int)(result.hitX + targetDirX * targetLineLen),
                   (int)(result.hitY + targetDirY * targetLineLen));
        g.setStroke(new BasicStroke(1));

        double cueContinueDirX;
        double cueContinueDirY;

        if (isFullHit) {
            cueContinueDirX = 0;
            cueContinueDirY = 0;
        } else {
            double tx = -hitNy;
            double ty =  hitNx;
            double tDot = inDirX * tx + inDirY * ty;
            if (tDot < 0) {
                tx = -tx;
                ty = -ty;
            }
            cueContinueDirX = tx;
            cueContinueDirY = ty;
        }

        if (cueContinueDirX != 0 || cueContinueDirY != 0) {
            double cueLineLen = 55;
            g.setColor(new Color(150, 220, 255, 100));
            g.setStroke(new BasicStroke(1.2f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{3f, 6f}, 0));
            g.drawLine((int) result.hitX,
                       (int) result.hitY,
                       (int)(result.hitX + cueContinueDirX * cueLineLen),
                       (int)(result.hitY + cueContinueDirY * cueLineLen));
            g.setStroke(new BasicStroke(1));
        }
    }

    private void drawWallReflection(Graphics2D g, RayResult result,
                                     double inDirX, double inDirY,
                                     GameState state) {
        double reflDirX;
        double reflDirY;

        if (Math.abs(result.normalX) > 0.5) {
            reflDirX = -inDirX;
            reflDirY =  inDirY;
        } else {
            reflDirX =  inDirX;
            reflDirY = -inDirY;
        }

        RayResult result2 = castRay(result.hitX, result.hitY,
                                    reflDirX, reflDirY, state, null);

        g.setColor(new Color(255, 255, 255, 35));
        g.setStroke(new BasicStroke(LINE_WIDTH,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                0, new float[]{4f, 8f}, 0));
        g.drawLine((int) result.hitX,  (int) result.hitY,
                   (int) result2.hitX, (int) result2.hitY);
        g.setStroke(new BasicStroke(1));
    }

    private void drawPocketIndicator(Graphics2D g, double px, double py) {
        g.setColor(new Color(50, 255, 100, 160));
        g.setStroke(new BasicStroke(2.5f));
        int r = Pocket.RADIUS + 4;
        g.drawOval((int) px - r, (int) py - r, r * 2, r * 2);
        g.setStroke(new BasicStroke(1));
    }

    private RayResult castRay(double startX, double startY,
                               double dirX,   double dirY,
                               GameState state, Ball ignoreBall) {
        Table  table   = state.getTable();
        double x       = startX;
        double y       = startY;
        double normLen = Math.sqrt(dirX * dirX + dirY * dirY);

        if (normLen < 0.001) {
            return new RayResult(x, y, HitType.NONE, 0, 0);
        }

        double ndx = dirX / normLen;
        double ndy = dirY / normLen;

        for (int step = 0; step < MAX_STEPS; step++) {
            x += ndx * STEP;
            y += ndy * STEP;

            for (Pocket pocket : table.getPockets()) {
                double dx   = x - pocket.getX();
                double dy   = y - pocket.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < Pocket.RADIUS) {
                    return new RayResult(pocket.getX(), pocket.getY(),
                                         HitType.POCKET, 0, 0);
                }
            }

            for (Ball ball : state.getBalls()) {
                if (ball.isPocketed()) {
                    continue;
                }
                if (ball == ignoreBall) {
                    continue;
                }

                double dx   = x - ball.getX();
                double dy   = y - ball.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < Ball.RADIUS * 2) {
                    double hnx = (x - ball.getX()) / Math.max(dist, 0.001);
                    double hny = (y - ball.getY()) / Math.max(dist, 0.001);
                    return new RayResult(x, y, HitType.BALL, hnx, hny);
                }
            }

            double wallNx = 0;
            double wallNy = 0;
            boolean wallHit = false;

            if (x - Ball.RADIUS < table.getLeftWall()) {
                x       = table.getLeftWall() + Ball.RADIUS;
                wallNx  = 1;
                wallHit = true;
            } else if (x + Ball.RADIUS > table.getRightWall()) {
                x       = table.getRightWall() - Ball.RADIUS;
                wallNx  = -1;
                wallHit = true;
            }

            if (y - Ball.RADIUS < table.getTopWall()) {
                y       = table.getTopWall() + Ball.RADIUS;
                wallNy  = 1;
                wallHit = true;
            } else if (y + Ball.RADIUS > table.getBottomWall()) {
                y       = table.getBottomWall() - Ball.RADIUS;
                wallNy  = -1;
                wallHit = true;
            }

            if (wallHit) {
                return new RayResult(x, y, HitType.WALL, wallNx, wallNy);
            }
        }

        return new RayResult(x, y, HitType.NONE, 0, 0);
    }

    private enum HitType {
        NONE, WALL, BALL, POCKET
    }

    private static class RayResult {

        double  hitX;
        double  hitY;
        HitType hitType;
        double  normalX;
        double  normalY;

        RayResult(double hitX, double hitY, HitType hitType,
                  double normalX, double normalY) {
            this.hitX    = hitX;
            this.hitY    = hitY;
            this.hitType = hitType;
            this.normalX = normalX;
            this.normalY = normalY;
        }
    }
}