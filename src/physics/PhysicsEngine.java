package physics;

import model.Ball;
import model.GameState;
import model.Pocket;
import model.SpinType;
import view.SoundManager;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int    SUBSTEPS            = 6;
    private static final double FRICTION_PER_FRAME  = 0.980;
    private static final double STOP_SPEED          = 0.11;
    private static final double POCKET_PULL_RADIUS  = Pocket.RADIUS * 1.2;
    private static final double POCKET_ANIM_SPEED   = 0.06;

    private static final double BACK_DECEL_STRENGTH = 0.15;
    private static final double TOP_ACCEL_STRENGTH  = 0.10;
    private static final int    SPIN_FRAMES         = 25;

    private CollisionHandler collisionHandler;
    private List<Ball>       pocketedThisTurn;
    private int              wallHitsThisTurn;
    private boolean          cueBallPocketed;
    private Ball             firstHitBall;

    private SpinType currentSpin;
    private boolean  firstCollisionDone;
    private int      spinFramesLeft;
    private double   spinDirX;
    private double   spinDirY;
    private boolean  wasFullHit;

    public PhysicsEngine() {
        this.collisionHandler = new CollisionHandler();
        this.pocketedThisTurn = new ArrayList<>();
        resetTurnTracking();
    }

    public void resetTurnTracking() {
        pocketedThisTurn.clear();
        wallHitsThisTurn   = 0;
        cueBallPocketed    = false;
        firstHitBall       = null;
        currentSpin        = SpinType.NONE;
        firstCollisionDone = false;
        spinFramesLeft     = 0;
        spinDirX           = 0;
        spinDirY           = 0;
        wasFullHit         = false;
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

        applyOngoingSpinForce(state.getCueBall());
        checkPockets(state.getBalls(), state.getTable().getPockets());
        updatePocketAnimations(state.getBalls());
    }

    private void applyFrictionAndMove(List<Ball> balls, double friction) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            double newVx = ball.getVelocityX() * friction;
            double newVy = ball.getVelocityY() * friction;
            double speed = Math.sqrt(newVx * newVx + newVy * newVy);

            if (speed < STOP_SPEED) {
                ball.setVelocityX(0);
                ball.setVelocityY(0);
                continue;
            }

            ball.setVelocityX(newVx);
            ball.setVelocityY(newVy);
            ball.setX(ball.getX() + newVx / SUBSTEPS);
            ball.setY(ball.getY() + newVy / SUBSTEPS);
        }
    }

    private void resolveWalls(List<Ball> balls, GameState state) {
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }

            double prevVx = ball.getVelocityX();
            double prevVy = ball.getVelocityY();

            collisionHandler.handleWallCollision(ball, state.getTable());

            boolean xFlipped = prevVx * ball.getVelocityX() < 0;
            boolean yFlipped = prevVy * ball.getVelocityY() < 0;

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

                if (!collisionHandler.areBallsOverlapping(a, b)) {
                    continue;
                }

                boolean isFirstHit = (firstHitBall == null);
                Ball cueBall       = null;
                Ball hitBall       = null;

                if (a.isCueBall()) {
                    cueBall = a;
                    hitBall = b;
                }
                if (b.isCueBall()) {
                    cueBall = b;
                    hitBall = a;
                }

                if (isFirstHit && cueBall != null) {
                    firstHitBall = hitBall;
                }

                double cueVxBefore = (cueBall != null) ? cueBall.getVelocityX() : 0;
                double cueVyBefore = (cueBall != null) ? cueBall.getVelocityY() : 0;
                double hitDx       = (hitBall != null && cueBall != null)
                                     ? hitBall.getX() - cueBall.getX() : 0;
                double hitDy       = (hitBall != null && cueBall != null)
                                     ? hitBall.getY() - cueBall.getY() : 0;

                collisionHandler.resolveBallCollision(a, b);
                SoundManager.getInstance().playBallCollision();

                if (isFirstHit && cueBall != null && hitBall != null) {
                    applySpinImmediately(cueBall,
                                         cueVxBefore, cueVyBefore,
                                         hitDx, hitDy);
                }
            }
        }
    }

    private void applySpinImmediately(Ball cueBall,
                                       double cueVxBefore, double cueVyBefore,
                                       double hitDx, double hitDy) {
        double hitDist = Math.sqrt(hitDx * hitDx + hitDy * hitDy);
        if (hitDist < 0.001) {
            return;
        }

        double hitNx = hitDx / hitDist;
        double hitNy = hitDy / hitDist;

        double incomingSpeed = Math.sqrt(cueVxBefore * cueVxBefore
                                       + cueVyBefore * cueVyBefore);
        if (incomingSpeed < 0.001) {
            return;
        }

        double incomingNx = cueVxBefore / incomingSpeed;
        double incomingNy = cueVyBefore / incomingSpeed;

        double dotProduct = incomingNx * hitNx + incomingNy * hitNy;
        boolean fullHit   = dotProduct > 0.92;

        double tangentX = -hitNy;
        double tangentY =  hitNx;

        double tangentDot = incomingNx * tangentX + incomingNy * tangentY;
        if (tangentDot < 0) {
            tangentX = -tangentX;
            tangentY = -tangentY;
        }

        if (currentSpin == SpinType.NONE) {
            applyStopShot(cueBall, incomingSpeed, hitNx, hitNy,
                          tangentX, tangentY, fullHit);
            return;
        }

        if (currentSpin == SpinType.BACK) {
            applyBackSpinImmediate(cueBall, incomingSpeed, hitNx, hitNy,
                                   tangentX, tangentY, fullHit);
            wasFullHit     = fullHit;
            spinDirX       = -hitNx;
            spinDirY       = -hitNy;
            spinFramesLeft = SPIN_FRAMES;
            firstCollisionDone = true;
            return;
        }

        if (currentSpin == SpinType.TOP) {
            applyTopSpinImmediate(cueBall, incomingSpeed, hitNx, hitNy,
                                  tangentX, tangentY, fullHit);
            wasFullHit     = fullHit;
            spinDirX       = hitNx;
            spinDirY       = hitNy;
            spinFramesLeft = SPIN_FRAMES;
            firstCollisionDone = true;
        }
    }

    private void applyStopShot(Ball cueBall, double speed,
                                double hitNx, double hitNy,
                                double tangentX, double tangentY,
                                boolean fullHit) {
        if (fullHit) {
            cueBall.setVelocityX(0);
            cueBall.setVelocityY(0);
        } else {
            double tangentSpeed = speed * 0.25;
            cueBall.setVelocityX(tangentX * tangentSpeed);
            cueBall.setVelocityY(tangentY * tangentSpeed);
        }
    }

    private void applyBackSpinImmediate(Ball cueBall, double speed,
                                         double hitNx, double hitNy,
                                         double tangentX, double tangentY,
                                         boolean fullHit) {
        if (fullHit) {
            double backSpeed = speed * 0.55;
            cueBall.setVelocityX(-hitNx * backSpeed);
            cueBall.setVelocityY(-hitNy * backSpeed);
        } else {
            double tangentSpeed  = speed * 0.30;
            double backwardSpeed = speed * 0.20;
            cueBall.setVelocityX(tangentX * tangentSpeed - hitNx * backwardSpeed);
            cueBall.setVelocityY(tangentY * tangentSpeed - hitNy * backwardSpeed);
        }
    }

    private void applyTopSpinImmediate(Ball cueBall, double speed,
                                        double hitNx, double hitNy,
                                        double tangentX, double tangentY,
                                        boolean fullHit) {
        if (fullHit) {
            double forwardSpeed = speed * 0.65;
            cueBall.setVelocityX(hitNx * forwardSpeed);
            cueBall.setVelocityY(hitNy * forwardSpeed);
        } else {
            double tangentSpeed  = speed * 0.30;
            double forwardSpeed  = speed * 0.35;
            cueBall.setVelocityX(tangentX * tangentSpeed + hitNx * forwardSpeed);
            cueBall.setVelocityY(tangentY * tangentSpeed + hitNy * forwardSpeed);
        }
    }

    private void applyOngoingSpinForce(Ball cueBall) {
        if (cueBall == null || cueBall.isPocketed()) {
            return;
        }

        if (!firstCollisionDone || spinFramesLeft <= 0) {
            return;
        }

        double currentVx    = cueBall.getVelocityX();
        double currentVy    = cueBall.getVelocityY();
        double currentSpeed = Math.sqrt(currentVx * currentVx + currentVy * currentVy);
        double progress     = (double) spinFramesLeft / SPIN_FRAMES;

        spinFramesLeft--;

        if (currentSpin == SpinType.BACK) {
            if (wasFullHit) {
                double forceStr = BACK_DECEL_STRENGTH * progress;
                cueBall.setVelocityX(currentVx + spinDirX * forceStr);
                cueBall.setVelocityY(currentVy + spinDirY * forceStr);
            } else {
                if (currentSpeed > STOP_SPEED * 2) {
                    double blendStrength = progress * 0.07;
                    double newVx = currentVx * (1.0 - blendStrength)
                                 + spinDirX   * currentSpeed * blendStrength;
                    double newVy = currentVy * (1.0 - blendStrength)
                                 + spinDirY   * currentSpeed * blendStrength;
                    cueBall.setVelocityX(newVx);
                    cueBall.setVelocityY(newVy);
                }
            }
        }

        if (currentSpin == SpinType.TOP) {
            if (wasFullHit) {
                double forceStr = TOP_ACCEL_STRENGTH * progress;
                cueBall.setVelocityX(currentVx + spinDirX * forceStr);
                cueBall.setVelocityY(currentVy + spinDirY * forceStr);
            } else {
                if (currentSpeed > STOP_SPEED * 2) {
                    double blendStrength = progress * 0.05;
                    double newVx = currentVx * (1.0 - blendStrength)
                                 + spinDirX   * currentSpeed * blendStrength;
                    double newVy = currentVy * (1.0 - blendStrength)
                                 + spinDirY   * currentSpeed * blendStrength;
                    cueBall.setVelocityX(newVx);
                    cueBall.setVelocityY(newVy);
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
                double dx   = ball.getX() - pocket.getX();
                double dy   = ball.getY() - pocket.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < POCKET_PULL_RADIUS) {
                    startPocketing(ball, pocket);
                    break;
                }

                if (dist < Pocket.RADIUS * 1.7) {
                    double pullStrength = 0.08;
                    double angle        = Math.atan2(dy, dx);
                    ball.setVelocityX(ball.getVelocityX() - Math.cos(angle) * pullStrength);
                    ball.setVelocityY(ball.getVelocityY() - Math.sin(angle) * pullStrength);
                }
            }
        }
    }

    private void startPocketing(Ball ball, Pocket pocket) {
        ball.setVelocityX(0);
        ball.setVelocityY(0);
        SoundManager.getInstance().playPocket();
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