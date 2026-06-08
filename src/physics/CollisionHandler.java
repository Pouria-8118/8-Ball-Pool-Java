package physics;

import model.Ball;
import model.Table;
import model.Pocket;

public class CollisionHandler {

    private static final double FRICTION = 0.985;
    private static final double WALL_RESTITUTION = 0.75;
    private static final double BALL_RESTITUTION = 0.92;

    public void updateBall(Ball ball, Table table) {
        ball.applyFriction(FRICTION);
        ball.update();
        handleWallCollision(ball, table);
    }

    private void handleWallCollision(Ball ball, Table table) {
        int radius = Ball.RADIUS;
        int left = table.getLeftWall() + radius;
        int right = table.getRightWall() - radius;
        int top = table.getTopWall() + radius;
        int bottom = table.getBottomWall() - radius;

        if (ball.getX() < left) {
            ball.setX(left);
            ball.setVelocityX(-ball.getVelocityX() * WALL_RESTITUTION);
        }

        if (ball.getX() > right) {
            ball.setX(right);
            ball.setVelocityX(-ball.getVelocityX() * WALL_RESTITUTION);
        }

        if (ball.getY() < top) {
            ball.setY(top);
            ball.setVelocityY(-ball.getVelocityY() * WALL_RESTITUTION);
        }

        if (ball.getY() > bottom) {
            ball.setY(bottom);
            ball.setVelocityY(-ball.getVelocityY() * WALL_RESTITUTION);
        }
    }

    public boolean checkBallCollision(Ball a, Ball b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < Ball.RADIUS * 2;
    }

    public void resolveBallCollision(Ball a, Ball b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) {
            dist = 0.01;
        }

        double nx = dx / dist;
        double ny = dy / dist;

        double overlap = Ball.RADIUS * 2 - dist;
        a.setX(a.getX() - nx * overlap / 2);
        a.setY(a.getY() - ny * overlap / 2);
        b.setX(b.getX() + nx * overlap / 2);
        b.setY(b.getY() + ny * overlap / 2);

        double dvx = a.getVelocityX() - b.getVelocityX();
        double dvy = a.getVelocityY() - b.getVelocityY();
        double dot = dvx * nx + dvy * ny;

        if (dot > 0) {
            return;
        }

        double impulse = dot * BALL_RESTITUTION;
        a.setVelocityX(a.getVelocityX() - impulse * nx);
        a.setVelocityY(a.getVelocityY() - impulse * ny);
        b.setVelocityX(b.getVelocityX() + impulse * nx);
        b.setVelocityY(b.getVelocityY() + impulse * ny);
    }

    public boolean checkPocketCollision(Ball ball, Pocket pocket) {
        double dx = ball.getX() - pocket.getX();
        double dy = ball.getY() - pocket.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < Pocket.RADIUS;
    }

    public int countWallHits(Ball ball, Table table) {
        return 0;
    }
}