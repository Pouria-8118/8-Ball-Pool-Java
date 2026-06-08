package physics;

import model.Ball;
import model.Table;
import model.Pocket;

public class CollisionHandler {

    private static final double FRICTION = 0.988;
    private static final double WALL_RESTITUTION = 0.65;
    private static final double BALL_RESTITUTION = 0.96;
    private static final double STOP_THRESHOLD = 0.08;

    public void updateBall(Ball ball, Table table) {
        ball.applyFriction(FRICTION, STOP_THRESHOLD);
        ball.update();
        handleWallCollision(ball, table);
    }

    private void handleWallCollision(Ball ball, Table table) {
        int r = Ball.RADIUS;
        double left = table.getLeftWall() + r;
        double right = table.getRightWall() - r;
        double top = table.getTopWall() + r;
        double bottom = table.getBottomWall() - r;

        if (ball.getX() < left) {
            ball.setX(left);
            ball.setVelocityX(Math.abs(ball.getVelocityX()) * WALL_RESTITUTION);
        }

        if (ball.getX() > right) {
            ball.setX(right);
            ball.setVelocityX(-Math.abs(ball.getVelocityX()) * WALL_RESTITUTION);
        }

        if (ball.getY() < top) {
            ball.setY(top);
            ball.setVelocityY(Math.abs(ball.getVelocityY()) * WALL_RESTITUTION);
        }

        if (ball.getY() > bottom) {
            ball.setY(bottom);
            ball.setVelocityY(-Math.abs(ball.getVelocityY()) * WALL_RESTITUTION);
        }
    }

    public boolean checkBallCollision(Ball a, Ball b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double distSq = dx * dx + dy * dy;
        double minDist = Ball.RADIUS * 2;
        return distSq < minDist * minDist;
    }

    public void resolveBallCollision(Ball a, Ball b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.0001) {
            dx = 1.0;
            dy = 0.0;
            dist = 1.0;
        }

        double nx = dx / dist;
        double ny = dy / dist;

        double minDist = Ball.RADIUS * 2.0;
        double overlap = minDist - dist;

        if (overlap > 0) {
            double pushX = nx * (overlap / 2.0 + 0.5);
            double pushY = ny * (overlap / 2.0 + 0.5);
            a.setX(a.getX() - pushX);
            a.setY(a.getY() - pushY);
            b.setX(b.getX() + pushX);
            b.setY(b.getY() + pushY);
        }

        double dvx = a.getVelocityX() - b.getVelocityX();
        double dvy = a.getVelocityY() - b.getVelocityY();
        double dvDotN = dvx * nx + dvy * ny;

        if (dvDotN >= 0) {
            return;
        }

        double impulse = dvDotN * BALL_RESTITUTION;

        a.setVelocityX(a.getVelocityX() - impulse * nx);
        a.setVelocityY(a.getVelocityY() - impulse * ny);
        b.setVelocityX(b.getVelocityX() + impulse * nx);
        b.setVelocityY(b.getVelocityY() + impulse * ny);
    }

    public boolean checkPocketCollision(Ball ball, Pocket pocket) {
        double dx = ball.getX() - pocket.getX();
        double dy = ball.getY() - pocket.getY();
        double distSq = dx * dx + dy * dy;
        return distSq < (double) Pocket.RADIUS * Pocket.RADIUS;
    }
}