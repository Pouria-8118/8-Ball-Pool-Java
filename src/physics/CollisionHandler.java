package physics;

import model.Ball;
import model.Pocket;
import model.Table;

public class CollisionHandler {

    private static final double WALL_RESTITUTION = 0.55;
    private static final double BALL_RESTITUTION = 0.97;

    public void handleWallCollision(Ball ball, Table table) {
        int r = Ball.RADIUS;
        double left = table.getLeftWall() + r;
        double right = table.getRightWall() - r;
        double top = table.getTopWall() + r;
        double bottom = table.getBottomWall() - r;

        Vector pos = new Vector(ball.getX(), ball.getY());
        Vector vel = new Vector(ball.getVelocityX(), ball.getVelocityY());

        if (pos.getX() < left) {
            pos = new Vector(left, pos.getY());
            if (vel.getX() < 0) {
                vel = new Vector(-vel.getX() * WALL_RESTITUTION, vel.getY() * WALL_RESTITUTION);
            }
        }

        if (pos.getX() > right) {
            pos = new Vector(right, pos.getY());
            if (vel.getX() > 0) {
                vel = new Vector(-vel.getX() * WALL_RESTITUTION, vel.getY() * WALL_RESTITUTION);
            }
        }

        if (pos.getY() < top) {
            pos = new Vector(pos.getX(), top);
            if (vel.getY() < 0) {
                vel = new Vector(vel.getX() * WALL_RESTITUTION, -vel.getY() * WALL_RESTITUTION);
            }
        }

        if (pos.getY() > bottom) {
            pos = new Vector(pos.getX(), bottom);
            if (vel.getY() > 0) {
                vel = new Vector(vel.getX() * WALL_RESTITUTION, -vel.getY() * WALL_RESTITUTION);
            }
        }

        ball.setX(pos.getX());
        ball.setY(pos.getY());
        ball.setVelocityX(vel.getX());
        ball.setVelocityY(vel.getY());
    }

    public boolean areBallsOverlapping(Ball a, Ball b) {
        Vector pa = new Vector(a.getX(), a.getY());
        Vector pb = new Vector(b.getX(), b.getY());
        Vector diff = pa.subtract(pb);
        double dist = diff.length();
        return dist < Ball.RADIUS * 2.0;
    }

    public void resolveBallCollision(Ball a, Ball b) {
        Vector pa = new Vector(a.getX(), a.getY());
        Vector pb = new Vector(b.getX(), b.getY());
        Vector va = new Vector(a.getVelocityX(), a.getVelocityY());
        Vector vb = new Vector(b.getVelocityX(), b.getVelocityY());

        Vector n = pb.subtract(pa);
        double dist = n.length();

        if (dist < 0.0001) {
            n = new Vector(1.0, 0.0);
            dist = 1.0;
        } else {
            n = n.normalize();
        }

        double overlap = Ball.RADIUS * 2.0 - dist;
        if (overlap > 0) {
            Vector correction = n.scale(overlap / 2.0 + 0.2);
            pa = pa.subtract(correction);
            pb = pb.add(correction);
            a.setX(pa.getX());
            a.setY(pa.getY());
            b.setX(pb.getX());
            b.setY(pb.getY());
        }

        double va_n = va.dot(n);
        double vb_n = vb.dot(n);

        if (va_n - vb_n <= 0) {
            return;
        }

        Vector va_t = va.subtract(n.scale(va_n));
        Vector vb_t = vb.subtract(n.scale(vb_n));

        double new_va_n = vb_n * BALL_RESTITUTION;
        double new_vb_n = va_n * BALL_RESTITUTION;

        Vector new_va = va_t.add(n.scale(new_va_n));
        Vector new_vb = vb_t.add(n.scale(new_vb_n));

        a.setVelocityX(new_va.getX());
        a.setVelocityY(new_va.getY());
        b.setVelocityX(new_vb.getX());
        b.setVelocityY(new_vb.getY());
    }

    public boolean isBallNearPocket(Ball ball, Pocket pocket) {
        Vector ballPos = new Vector(ball.getX(), ball.getY());
        Vector pocketPos = new Vector(pocket.getX(), pocket.getY());
        Vector diff = ballPos.subtract(pocketPos);
        double dist = diff.length();
        return dist < Pocket.RADIUS * 1.1;
    }
}