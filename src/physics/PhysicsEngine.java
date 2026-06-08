package physics;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.SpinType;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int    SUBSTEPS            = 6;
    private static final double FRICTION_PER_FRAME  = 0.965;
    private static final double STOP_SPEED          = 0.15;

    private CollisionHandler collisionHandler;
    private List<Ball>       pocketedThisTurn;
    private int              wallHitsThisTurn;
    private boolean          cueBallPocketed;
    private Ball             firstHitBall;

    public PhysicsEngine() {
        this.collisionHandler = new CollisionHandler();
        this.pocketedThisTurn = new ArrayList<>();
        resetTurnTracking();
    }

    public void resetTurnTracking() {
        pocketedThisTurn.clear();
        wallHitsThisTurn = 0;
        cueBallPocketed  = false;
        firstHitBall     = null;
    }

    public void shoot(Ball cueBall, double angle, double power, SpinType spin) {
        cueBall.setVelocityX(Math.cos(angle) * power);
        cueBall.setVelocityY(Math.sin(angle) * power);
    }

    public void update(GameState state) {
        double frictionPerSubstep = Math.pow(FRICTION_PER_FRAME, 1.0 / SUBSTEPS);

        for (int step = 0; step < SUBSTEPS; step++) {
            applyFrictionAndMove(state.getBalls(), frictionPerSubstep);
            resolveAllWallCollisions(state.getBalls(), state);
            resolveAllBallCollisions(state.getBalls());
            checkPockets(state.getBalls(), state.getTable().getPockets());
        }

        applySpinEffect(state.getCueBall(), state.getSelectedSpin());
    }

    private void applyFrictionAndMove(List<Ball> balls, double friction) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            ball.setVelocityX(ball.getVelocityX() * friction);
            ball.setVelocityY(ball.getVelocityY() * friction);

            double speed = Math.sqrt(
                ball.getVelocityX() * ball.getVelocityX() +
                ball.getVelocityY() * ball.getVelocityY()
            );

            if (speed < STOP_SPEED) {
                ball.setVelocityX(0);
                ball.setVelocityY(0);
                continue;
            }

            ball.setX(ball.getX() + ball.getVelocityX() / SUBSTEPS);
            ball.setY(ball.getY() + ball.getVelocityY() / SUBSTEPS);
        }
    }

    private void resolveAllWallCollisions(List<Ball> balls, GameState state) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            double prevVx = ball.getVelocityX();
            double prevVy = ball.getVelocityY();

            collisionHandler.handleWallCollision(ball, state.getTable());

            boolean xFlipped = (prevVx > 0.1 && ball.getVelocityX() < 0)
                             || (prevVx < -0.1 && ball.getVelocityX() > 0);
            boolean yFlipped = (prevVy > 0.1 && ball.getVelocityY() < 0)
                             || (prevVy < -0.1 && ball.getVelocityY() > 0);

            if (xFlipped || yFlipped) {
                wallHitsThisTurn++;
            }
        }
    }

    private void resolveAllBallCollisions(List<Ball> balls) {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball a = balls.get(i);
                Ball b = balls.get(j);

                if (a.isPocketed() || b.isPocketed()) {
                    continue;
                }

                if (collisionHandler.areBallsOverlapping(a, b)) {
                    if (firstHitBall == null) {
                        if (a.isCueBall()) {
                            firstHitBall = b;
                        }
                        if (b.isCueBall()) {
                            firstHitBall = a;
                        }
                    }
                    collisionHandler.resolveBallCollision(a, b);
                }
            }
        }
    }

    private void checkPockets(List<Ball> balls, List<Pocket> pockets) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            for (Pocket pocket : pockets) {
                if (collisionHandler.isBallInPocket(ball, pocket)) {
                    ball.setPocketed(true);
                    ball.setVelocityX(0);
                    ball.setVelocityY(0);
                    if (ball.isCueBall()) {
                        cueBallPocketed = true;
                    } else {
                        if (!pocketedThisTurn.contains(ball)) {
                            pocketedThisTurn.add(ball);
                        }
                    }
                }
            }
        }
    }

    private void applySpinEffect(Ball cueBall, SpinType spin) {
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        double speed = Math.sqrt(
            cueBall.getVelocityX() * cueBall.getVelocityX() +
            cueBall.getVelocityY() * cueBall.getVelocityY()
        );

        if (speed < 0.5) {
            return;
        }

        double nx = cueBall.getVelocityX() / speed;
        double ny = cueBall.getVelocityY() / speed;

        if (spin == SpinType.TOP) {
            cueBall.setVelocityX(cueBall.getVelocityX() + nx * 0.05);
            cueBall.setVelocityY(cueBall.getVelocityY() + ny * 0.05);
        }

        if (spin == SpinType.BACK) {
            cueBall.setVelocityX(cueBall.getVelocityX() - nx * 0.08);
            cueBall.setVelocityY(cueBall.getVelocityY() - ny * 0.08);
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