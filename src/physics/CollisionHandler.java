package physics;

import model.Ball;

public class CollisionHandler {

    public void handleBallCollision(Ball a, Ball b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        double minDist = Ball.RADIUS * 2;

        if (dist < minDist && dist > 0) {
            double nx = dx / dist;
            double ny = dy / dist;

            double overlap = minDist - dist;
            a.setX(a.getX() - nx * overlap / 2);
            a.setY(a.getY() - ny * overlap / 2);
            b.setX(b.getX() + nx * overlap / 2);
            b.setY(b.getY() + ny * overlap / 2);

            double dvx = b.getVelocityX() - a.getVelocityX();
            double dvy = b.getVelocityY() - a.getVelocityY();
            double dot = dvx * nx + dvy * ny;

            if (dot < 0) {
                double impulse = dot;
                a.setVelocityX(a.getVelocityX() + impulse * nx);
                a.setVelocityY(a.getVelocityY() + impulse * ny);
                b.setVelocityX(b.getVelocityX() - impulse * nx);
                b.setVelocityY(b.getVelocityY() - impulse * ny);
            }
        }
    }
}