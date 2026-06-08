package physics;

import model.Ball;
import model.Table;
import java.awt.Point;
import java.util.List;

public class PhysicsEngine {

    private static final double FRICTION = 0.988;
    private static final double MIN_SPEED = 0.05;

    private CollisionHandler collisionHandler;
    private Table table;

    public PhysicsEngine(Table table) {
        this.table = table;
        this.collisionHandler = new CollisionHandler();
    }

    public void update(List<Ball> balls) {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            if (!ball.isPocketed() && ball.isMoving()) {
                moveBall(ball);
                handleWallCollision(ball);
            }
        }

        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball a = balls.get(i);
                Ball b = balls.get(j);
                if (!a.isPocketed() && !b.isPocketed()) {
                    collisionHandler.handleBallCollision(a, b);
                }
            }
        }

        checkPockets(balls);
    }

    private void moveBall(Ball ball) {
        ball.setX(ball.getX() + ball.getVelocityX());
        ball.setY(ball.getY() + ball.getVelocityY());

        ball.setVelocityX(ball.getVelocityX() * FRICTION);
        ball.setVelocityY(ball.getVelocityY() * FRICTION);

        double speed = Math.sqrt(ball.getVelocityX() * ball.getVelocityX() + ball.getVelocityY() * ball.getVelocityY());
        if (speed < MIN_SPEED) {
            ball.setVelocityX(0);
            ball.setVelocityY(0);
        }
    }

    private void handleWallCollision(Ball ball) {
        int r = Ball.RADIUS;

        if (ball.getX() - r < table.getLeft()) {
            ball.setX(table.getLeft() + r);
            ball.setVelocityX(-ball.getVelocityX() * 0.85);
        }

        if (ball.getX() + r > table.getRight()) {
            ball.setX(table.getRight() - r);
            ball.setVelocityX(-ball.getVelocityX() * 0.85);
        }

        if (ball.getY() - r < table.getTop()) {
            ball.setY(table.getTop() + r);
            ball.setVelocityY(-ball.getVelocityY() * 0.85);
        }

        if (ball.getY() + r > table.getBottom()) {
            ball.setY(table.getBottom() - r);
            ball.setVelocityY(-ball.getVelocityY() * 0.85);
        }
    }

    private void checkPockets(List<Ball> balls) {
        List<Point> pockets = table.getPockets();
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            if (ball.isPocketed()) {
                continue;
            }
            for (int j = 0; j < pockets.size(); j++) {
                Point pocket = pockets.get(j);
                double dx = ball.getX() - pocket.x;
                double dy = ball.getY() - pocket.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < Table.POCKET_RADIUS) {
                    ball.setPocketed(true);
                    ball.setVelocityX(0);
                    ball.setVelocityY(0);
                }
            }
        }
    }

    public void applySpin(Ball cueBall, int spin) {
        if (spin == 1) {
            double speed = Math.sqrt(cueBall.getVelocityX() * cueBall.getVelocityX() + cueBall.getVelocityY() * cueBall.getVelocityY());
            double angle = Math.atan2(cueBall.getVelocityY(), cueBall.getVelocityX());
            double bonus = speed * 0.3;
            cueBall.setVelocityX(cueBall.getVelocityX() + Math.cos(angle) * bonus);
            cueBall.setVelocityY(cueBall.getVelocityY() + Math.sin(angle) * bonus);
        }
        if (spin == -1) {
            cueBall.setVelocityX(cueBall.getVelocityX() * 0.5);
            cueBall.setVelocityY(cueBall.getVelocityY() * 0.5);
        }
    }

    public void applySpinAfterCollision(Ball cueBall, int spin, double collisionAngle) {
        if (spin == 1) {
            cueBall.setVelocityX(cueBall.getVelocityX() + Math.cos(collisionAngle) * 1.5);
            cueBall.setVelocityY(cueBall.getVelocityY() + Math.sin(collisionAngle) * 1.5);
        }
        if (spin == -1) {
            cueBall.setVelocityX(-Math.cos(collisionAngle) * 2.0);
            cueBall.setVelocityY(-Math.sin(collisionAngle) * 2.0);
        }
    }
}