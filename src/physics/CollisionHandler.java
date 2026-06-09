package physics;

import model.Ball;
import model.Pocket;
import model.Table;

public class CollisionHandler {

    private static final double WALL_RESTITUTION = 0.55;
    private static final double BALL_RESTITUTION = 0.97;

    public void handleWallCollision(Ball ball, Table table) {
        int    r      = Ball.RADIUS;
        double left   = table.getLeftWall()   + r;
        double right  = table.getRightWall()  - r;
        double top    = table.getTopWall()    + r;
        double bottom = table.getBottomWall() - r;

        if (ball.getX() < left) {
            ball.setX(left);
            if (ball.getVelocityX() < 0) {
                ball.setVelocityX(-ball.getVelocityX() * WALL_RESTITUTION);
                ball.setVelocityY(ball.getVelocityY()  * WALL_RESTITUTION);
            }
        }

        if (ball.getX() > right) {
            ball.setX(right);
            if (ball.getVelocityX() > 0) {
                ball.setVelocityX(-ball.getVelocityX() * WALL_RESTITUTION);
                ball.setVelocityY(ball.getVelocityY()  * WALL_RESTITUTION);
            }
        }

        if (ball.getY() < top) {
            ball.setY(top);
            if (ball.getVelocityY() < 0) {
                ball.setVelocityY(-ball.getVelocityY() * WALL_RESTITUTION);
                ball.setVelocityX(ball.getVelocityX()  * WALL_RESTITUTION);
            }
        }

        if (ball.getY() > bottom) {
            ball.setY(bottom);
            if (ball.getVelocityY() > 0) {
                ball.setVelocityY(-ball.getVelocityY() * WALL_RESTITUTION);
                ball.setVelocityX(ball.getVelocityX()  * WALL_RESTITUTION);
            }
        }
    }

    public boolean areBallsOverlapping(Ball a, Ball b) {
        double dx   = a.getX() - b.getX();
        double dy   = a.getY() - b.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < Ball.RADIUS * 2.0;
    }

    public void resolveBallCollision(Ball a, Ball b) {
        double dx   = b.getX() - a.getX();
        double dy   = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.0001) {
            dist = 0.0001;
            dx   = 1.0;
            dy   = 0.0;
        }

        double nx = dx / dist;
        double ny = dy / dist;

        double overlap = Ball.RADIUS * 2.0 - dist;
        if (overlap > 0) {
            double correction = overlap / 2.0 + 0.2;
            a.setX(a.getX() - nx * correction);
            a.setY(a.getY() - ny * correction);
            b.setX(b.getX() + nx * correction);
            b.setY(b.getY() + ny * correction);
        }

        double aVn = a.getVelocityX() * nx + a.getVelocityY() * ny;
        double bVn = b.getVelocityX() * nx + b.getVelocityY() * ny;

        if (aVn - bVn <= 0) {
            return;
        }

        double aVtx  = a.getVelocityX() - aVn * nx;
        double aVty  = a.getVelocityY() - aVn * ny;
        double bVtx  = b.getVelocityX() - bVn * nx;
        double bVty  = b.getVelocityY() - bVn * ny;
        double newAVn = bVn * BALL_RESTITUTION;
        double newBVn = aVn * BALL_RESTITUTION;

        a.setVelocityX(aVtx + newAVn * nx);
        a.setVelocityY(aVty + newAVn * ny);
        b.setVelocityX(bVtx + newBVn * nx);
        b.setVelocityY(bVty + newBVn * ny);
    }

    public boolean isBallNearPocket(Ball ball, Pocket pocket) {
        double dx   = ball.getX() - pocket.getX();
        double dy   = ball.getY() - pocket.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < Pocket.RADIUS * 1.1;
    }
}