package physics;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.SpinType;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private CollisionHandler collisionHandler;
    private List<Ball> pocketedThisTurn;
    private int wallHitsThisTurn;
    private boolean cueBallHitOwnGroup;
    private boolean cueBallPocketed;
    private Ball firstHitBall;

    public PhysicsEngine() {
        this.collisionHandler = new CollisionHandler();
        this.pocketedThisTurn = new ArrayList<>();
        this.wallHitsThisTurn = 0;
        this.cueBallHitOwnGroup = false;
        this.cueBallPocketed = false;
        this.firstHitBall = null;
    }

    public void resetTurnTracking() {
        pocketedThisTurn.clear();
        wallHitsThisTurn = 0;
        cueBallHitOwnGroup = false;
        cueBallPocketed = false;
        firstHitBall = null;
    }

    public void shoot(Ball cueBall, double angle, double power, SpinType spin) {
        double vx = Math.cos(angle) * power;
        double vy = Math.sin(angle) * power;
        cueBall.setVelocityX(vx);
        cueBall.setVelocityY(vy);
    }

    public void update(GameState state) {
        List<Ball> balls = state.getBalls();
        Ball cueBall = state.getCueBall();

        for (Ball ball : balls) {
            if (!ball.isPocketed()) {
                collisionHandler.updateBall(ball, state.getTable());
            }
        }

        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball a = balls.get(i);
                Ball b = balls.get(j);
                if (a.isPocketed() || b.isPocketed()) {
                    continue;
                }
                if (collisionHandler.checkBallCollision(a, b)) {
                    if (a.isCueBall() && firstHitBall == null) {
                        firstHitBall = b;
                    }
                    if (b.isCueBall() && firstHitBall == null) {
                        firstHitBall = a;
                    }
                    collisionHandler.resolveBallCollision(a, b);
                }
            }
        }

        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            for (Pocket pocket : state.getTable().getPockets()) {
                if (collisionHandler.checkPocketCollision(ball, pocket)) {
                    ball.setPocketed(true);
                    ball.setVelocityX(0);
                    ball.setVelocityY(0);
                    if (ball.isCueBall()) {
                        cueBallPocketed = true;
                    } else {
                        pocketedThisTurn.add(ball);
                    }
                }
            }
        }

        applySpin(cueBall, state.getSelectedSpin());
    }

    private void applySpin(Ball cueBall, SpinType spin) {
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }
        if (!cueBall.isMoving()) {
            return;
        }
        if (spin == SpinType.TOP) {
            double speed = Math.sqrt(cueBall.getVelocityX() * cueBall.getVelocityX()
                    + cueBall.getVelocityY() * cueBall.getVelocityY());
            if (speed > 0.5) {
                double factor = 0.001;
                double nx = cueBall.getVelocityX() / speed;
                double ny = cueBall.getVelocityY() / speed;
                cueBall.setVelocityX(cueBall.getVelocityX() + nx * factor * speed);
                cueBall.setVelocityY(cueBall.getVelocityY() + ny * factor * speed);
            }
        }
        if (spin == SpinType.BACK) {
            double speed = Math.sqrt(cueBall.getVelocityX() * cueBall.getVelocityX()
                    + cueBall.getVelocityY() * cueBall.getVelocityY());
            if (speed > 0.5) {
                double factor = 0.003;
                double nx = cueBall.getVelocityX() / speed;
                double ny = cueBall.getVelocityY() / speed;
                cueBall.setVelocityX(cueBall.getVelocityX() - nx * factor * speed);
                cueBall.setVelocityY(cueBall.getVelocityY() - ny * factor * speed);
            }
        }
    }

    public List<Ball> getPocketedThisTurn() {
        return pocketedThisTurn;
    }
    public boolean isCueBallPocketed() {
        return cueBallPocketed;
    }
    public Ball getFirstHitBall() {
        return firstHitBall;
    }
    public int getWallHitsThisTurn() {
        return wallHitsThisTurn; 
    }
}