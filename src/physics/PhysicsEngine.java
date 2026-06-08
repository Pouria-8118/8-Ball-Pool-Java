package physics;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.SpinType;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int SUBSTEPS = 4;

    private CollisionHandler collisionHandler;
    private List<Ball> pocketedThisTurn;
    private int wallHitsThisTurn;
    private boolean cueBallPocketed;
    private Ball firstHitBall;

    public PhysicsEngine() {
        this.collisionHandler = new CollisionHandler();
        this.pocketedThisTurn = new ArrayList<>();
        this.wallHitsThisTurn = 0;
        this.cueBallPocketed = false;
        this.firstHitBall = null;
    }

    public void resetTurnTracking() {
        pocketedThisTurn.clear();
        wallHitsThisTurn = 0;
        cueBallPocketed = false;
        firstHitBall = null;
    }

    public void shoot(Ball cueBall, double angle, double power, SpinType spin) {
        cueBall.setVelocityX(Math.cos(angle) * power);
        cueBall.setVelocityY(Math.sin(angle) * power);
    }

    public void update(GameState state) {
        for (int step = 0; step < SUBSTEPS; step++) {
            subStep(state);
        }
        applySpin(state.getCueBall(), state.getSelectedSpin());
    }

    private void subStep(GameState state) {
        List<Ball> balls = state.getBalls();

        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            ball.applyFriction(Math.pow(0.988, 1.0 / SUBSTEPS), 0.08 / SUBSTEPS);
            ball.update(1.0 / SUBSTEPS);
            handleWallBounce(ball, state);
        }

        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball a = balls.get(i);
                Ball b = balls.get(j);
                if (a.isPocketed() || b.isPocketed()) {
                    continue;
                }
                if (collisionHandler.checkBallCollision(a, b)) {
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
                        if (!pocketedThisTurn.contains(ball)) {
                            pocketedThisTurn.add(ball);
                        }
                    }
                }
            }
        }
    }

    private void handleWallBounce(Ball ball, GameState state) {
        int r = Ball.RADIUS;
        double left = state.getTable().getLeftWall() + r;
        double right = state.getTable().getRightWall() - r;
        double top = state.getTable().getTopWall() + r;
        double bottom = state.getTable().getBottomWall() - r;

        boolean hitWall = false;

        if (ball.getX() < left) {
            ball.setX(left);
            ball.setVelocityX(Math.abs(ball.getVelocityX()) * 0.65);
            hitWall = true;
        }
        if (ball.getX() > right) {
            ball.setX(right);
            ball.setVelocityX(-Math.abs(ball.getVelocityX()) * 0.65);
            hitWall = true;
        }
        if (ball.getY() < top) {
            ball.setY(top);
            ball.setVelocityY(Math.abs(ball.getVelocityY()) * 0.65);
            hitWall = true;
        }
        if (ball.getY() > bottom) {
            ball.setY(bottom);
            ball.setVelocityY(-Math.abs(ball.getVelocityY()) * 0.65);
            hitWall = true;
        }

        if (hitWall) {
            wallHitsThisTurn++;
        }
    }

    private void applySpin(Ball cueBall, SpinType spin) {
        if (cueBall == null || cueBall.isPocketed() || !cueBall.isMoving()) {
            return;
        }

        double speed = Math.sqrt(
                cueBall.getVelocityX() * cueBall.getVelocityX()
                + cueBall.getVelocityY() * cueBall.getVelocityY()
        );

        if (speed < 0.3) {
            return;
        }

        double nx = cueBall.getVelocityX() / speed;
        double ny = cueBall.getVelocityY() / speed;

        if (spin == SpinType.TOP) {
            cueBall.setVelocityX(cueBall.getVelocityX() + nx * 0.04);
            cueBall.setVelocityY(cueBall.getVelocityY() + ny * 0.04);
        }

        if (spin == SpinType.BACK) {
            cueBall.setVelocityX(cueBall.getVelocityX() - nx * 0.06);
            cueBall.setVelocityY(cueBall.getVelocityY() - ny * 0.06);
        }
    }

    public List<Ball> getPocketedThisTurn() { return pocketedThisTurn; }
    public boolean isCueBallPocketed() { return cueBallPocketed; }
    public Ball getFirstHitBall() { return firstHitBall; }
    public int getWallHitsThisTurn() { return wallHitsThisTurn; }
}