package physics;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.SpinType;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int    SUBSTEPS           = 6;
    private static final double FRICTION_PER_FRAME = 0.980;
    private static final double STOP_SPEED         = 0.11;
    private static final double POCKET_PULL_RADIUS = Pocket.RADIUS * 1.2;
    private static final double POCKET_ANIM_SPEED  = 0.06;
    private SpinType currentSpin = SpinType.NONE;

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
        currentSpin      = SpinType.NONE;

    }

    public void shoot(Ball cueBall, double angle, double power, SpinType spin) {
        cueBall.setVelocityX(Math.cos(angle) * power);
        cueBall.setVelocityY(Math.sin(angle) * power);
        this.currentSpin = spin;

    }

    public void update(GameState state) {
        double frictionPerSubstep = Math.pow(FRICTION_PER_FRAME, 1.0 / SUBSTEPS);

        for (int step = 0; step < SUBSTEPS; step++) {
            applyFrictionAndMove(state.getBalls(), frictionPerSubstep);
            resolveWalls(state.getBalls(), state);
            resolveBallCollisions(state.getBalls());
        }

        checkPockets(state.getBalls(), state.getTable().getPockets());
        updatePocketAnimations(state.getBalls());
    }

    private void applyFrictionAndMove(List<Ball> balls, double friction) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            Vector vel = new Vector(ball.getVelocityX(), ball.getVelocityY());
            vel = vel.scale(friction);
            
            double speed = vel.length();

            if (speed < STOP_SPEED) {
                ball.setVelocityX(0);
                ball.setVelocityY(0);
                continue;
            }

            ball.setVelocityX(vel.getX());
            ball.setVelocityY(vel.getY());
            ball.setX(ball.getX() + vel.getX() / SUBSTEPS);
            ball.setY(ball.getY() + vel.getY() / SUBSTEPS);
        }
    }

    private void resolveWalls(List<Ball> balls, GameState state) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            Vector prevVel = new Vector(ball.getVelocityX(), ball.getVelocityY());

            collisionHandler.handleWallCollision(ball, state.getTable());

            Vector currVel = new Vector(ball.getVelocityX(), ball.getVelocityY());
            
            boolean xFlipped = prevVel.getX() * currVel.getX() < 0;
            boolean yFlipped = prevVel.getY() * currVel.getY() < 0;

            if (xFlipped || yFlipped) {
                wallHitsThisTurn++;
            }
        }
    }

    private void resolveBallCollisions(List<Ball> balls) {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball a = balls.get(i);
                Ball b = balls.get(j);

                if (a.isPocketed() || b.isPocketed()) {
                    continue;
                }

                if (collisionHandler.areBallsOverlapping(a, b)) {
                    boolean wasFirstHit = (firstHitBall == null);
                    Ball hitBall = null;
                    
                    if (wasFirstHit) {
                        if (a.isCueBall()) {
                            firstHitBall = b;
                            hitBall = b;
                        }
                        if (b.isCueBall()) {
                            firstHitBall = a;
                            hitBall = a;
                        }
                    }
                    
                    collisionHandler.resolveBallCollision(a, b);
                    
                    if (wasFirstHit && firstHitBall != null && hitBall != null) {
                        if (a.isCueBall()) {
                            applySpinAfterCollision(a, currentSpin, hitBall);
                        }
                        if (b.isCueBall()) {
                            applySpinAfterCollision(b, currentSpin, hitBall);
                        }
                    }
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
                Vector ballPos = new Vector(ball.getX(), ball.getY());
                Vector pocketPos = new Vector(pocket.getX(), pocket.getY());
                Vector diff = ballPos.subtract(pocketPos);
                double dist = diff.length();

                if (dist < POCKET_PULL_RADIUS) {
                    startPocketing(ball, pocket);
                    break;
                }

                if (dist < Pocket.RADIUS * 1.7) {
                    double pullStrength = 0.08;
                    Vector dir = diff.normalize();
                    Vector pull = dir.scale(pullStrength);
                    Vector newVel = new Vector(ball.getVelocityX(), ball.getVelocityY()).subtract(pull);
                    ball.setVelocityX(newVel.getX());
                    ball.setVelocityY(newVel.getY());
                }
            }
        }
    }

    private void startPocketing(Ball ball, Pocket pocket) {
        ball.setVelocityX(0);
        ball.setVelocityY(0);
        ball.setPocketed(true);
        ball.setPocketAnimScale(1.0);

        if (ball.isCueBall()) {
            cueBallPocketed = true;
        } else {
            if (!pocketedThisTurn.contains(ball)) {
                pocketedThisTurn.add(ball);
            }
        }
    }

    private void updatePocketAnimations(List<Ball> balls) {
        for (Ball ball : balls) {
            if (!ball.isPocketed()) {
                continue;
            }
            double scale = ball.getPocketAnimScale();
            if (scale > 0) {
                ball.setPocketAnimScale(Math.max(0, scale - POCKET_ANIM_SPEED));
            }
        }
    }

    private void applySpinAfterCollision(Ball cueBall, SpinType spin, Ball hitBall) {
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }
        
        Vector vel = new Vector(cueBall.getVelocityX(), cueBall.getVelocityY());
        double speed = vel.length();
        
        if (speed < 0.5) {
            return;
        }
        
        Vector dirToHit = new Vector(hitBall.getX() - cueBall.getX(), hitBall.getY() - cueBall.getY());
        dirToHit = dirToHit.normalize();
        
        Vector velDir = vel.normalize();
        
        double dotProduct = velDir.getX() * dirToHit.getX() + velDir.getY() * dirToHit.getY();
        boolean isFullHit = Math.abs(dotProduct) > 0.95;
        
        Vector tangent = new Vector(-dirToHit.getY(), dirToHit.getX());
        tangent = tangent.normalize();
        
        if (spin == SpinType.NONE) {
            if (isFullHit) {
                cueBall.setVelocityX(0);
                cueBall.setVelocityY(0);
            } else {
                Vector tangentMove = tangent.scale(speed * 0.4);
                cueBall.setVelocityX(tangentMove.getX());
                cueBall.setVelocityY(tangentMove.getY());
            }
            return;
        }
        
        if (spin == SpinType.TOP) {
            if (isFullHit) {
                Vector forward = velDir.scale(speed);
                cueBall.setVelocityX(forward.getX());
                cueBall.setVelocityY(forward.getY());
            } else {
                Vector tangentMove = tangent.scale(speed * 0.3);
                Vector forwardMove = velDir.scale(speed * 0.5);
                Vector newVel = tangentMove.add(forwardMove);
                cueBall.setVelocityX(newVel.getX());
                cueBall.setVelocityY(newVel.getY());
            }
            return;
        }
        
        if (spin == SpinType.BACK) {
            if (isFullHit) {
                Vector backward = velDir.scale(-speed * 0.6);
                cueBall.setVelocityX(backward.getX());
                cueBall.setVelocityY(backward.getY());
            } else {
                Vector tangentMove = tangent.scale(speed * 0.3);
                Vector backwardMove = velDir.scale(-speed * 0.4);
                Vector newVel = tangentMove.add(backwardMove);
                cueBall.setVelocityX(newVel.getX());
                cueBall.setVelocityY(newVel.getY());
            }
            return;
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