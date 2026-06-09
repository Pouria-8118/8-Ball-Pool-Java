package controller;

import model.Ball;
import model.GameRecord;
import model.GameState;
import model.Player;
import model.Pocket;
import model.SpinType;
import model.Table;
import physics.PhysicsEngine;
import view.GamePanel;
import view.PlayerInfoPanel;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController {

    private static final int    TABLE_X      = GamePanel.TABLE_X;
    private static final int    TABLE_Y      = GamePanel.TABLE_Y;
    private static final int    TABLE_WIDTH  = GamePanel.TABLE_WIDTH;
    private static final int    TABLE_HEIGHT = GamePanel.TABLE_HEIGHT;
    private static final double MAX_POWER    = 22.0;

    private GameState          gameState;
    private PhysicsEngine      physicsEngine;
    private GamePanel          gamePanel;
    private DatabaseController dbController;
    private Timer              gameLoop;

    public GameController(GamePanel gamePanel) {
        this.gamePanel     = gamePanel;
        this.physicsEngine = new PhysicsEngine();
        this.dbController  = new DatabaseController();
    }

    public void startNewGame(String name1, String name2) {
        Player p1    = new Player(name1);
        Player p2    = new Player(name2);
        Table  table = new Table(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);
        gameState = new GameState(p1, p2, table);

        List<Ball> balls = buildRack(table);
        gameState.setBalls(balls);

        for (Ball b : balls) {
            if (b.isCueBall()) {
                gameState.setCueBall(b);
            }
        }

        Player first = new Random().nextBoolean() ? p1 : p2;
        gameState.setCurrentPlayer(first);
        gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);

        startLoop();
    }

    private List<Ball> buildRack(Table table) {
        List<Ball> balls   = new ArrayList<>();
        double     rackCX  = table.getX() + table.getWidth() * 3.0 / 4.0;
        double     rackCY  = table.getY() + table.getHeight() / 2.0;
        double     spacing = Ball.RADIUS * 2.05;

        java.awt.Color[] solidColors = {
            new java.awt.Color(230, 180, 0),
            new java.awt.Color(20,  90,  210),
            new java.awt.Color(210, 30,  30),
            new java.awt.Color(150, 0,   150),
            new java.awt.Color(230, 100, 0),
            new java.awt.Color(0,   160, 40),
            new java.awt.Color(160, 30,  0)
        };

        java.awt.Color[] stripeColors = {
            new java.awt.Color(230, 180, 0),
            new java.awt.Color(20,  90,  210),
            new java.awt.Color(210, 30,  30),
            new java.awt.Color(150, 0,   150),
            new java.awt.Color(230, 100, 0),
            new java.awt.Color(0,   160, 40),
            new java.awt.Color(160, 30,  0)
        };

        int[] rowCounts = {1, 2, 3, 4, 5};
        int[] order     = {1, 9, 2, 10, 8, 3, 11, 4, 12, 5, 13, 6, 14, 7, 15};
        int   idx       = 0;

        for (int row = 0; row < rowCounts.length; row++) {
            int    count = rowCounts[row];
            double rowX  = rackCX + row * spacing * 0.866;
            for (int col = 0; col < count; col++) {
                double ballY = rackCY + (col - (count - 1) / 2.0) * spacing;
                int    num   = order[idx++];

                if (num == 8) {
                    balls.add(new Ball(8, rowX, ballY,
                            java.awt.Color.BLACK, false, false, true));
                } else if (num <= 7) {
                    balls.add(new Ball(num, rowX, ballY,
                            solidColors[num - 1], false, false, false));
                } else {
                    balls.add(new Ball(num, rowX, ballY,
                            stripeColors[num - 9], true, false, false));
                }
            }
        }

        double cueX = table.getX() + table.getWidth() / 4.0;
        double cueY = table.getY() + table.getHeight() / 2.0;
        balls.add(new Ball(0, cueX, cueY,
                java.awt.Color.WHITE, false, true, false));
        return balls;
    }

    private void startLoop() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stop();
        }

        gameLoop = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        gameLoop.start();
    }

    private void tick() {
        if (gameState == null) {
            return;
        }

        if (gameState.getPhase() == GameState.Phase.BALLS_MOVING) {
            physicsEngine.update(gameState);
            if (gameState.allBallsStopped()) {
                processTurnResult();
            }
        }

        gamePanel.repaint();
    }

    public void onMouseMoved(int mx, int my) {
        if (gameState == null) {
            return;
        }

        if (gameState.getPhase() != GameState.Phase.AIMING) {
            return;
        }

        Ball cue = gameState.getCueBall();
        if (cue == null) {
            return;
        }

        double dx    = mx - cue.getX();
        double dy    = my - cue.getY();
        double dist  = Math.sqrt(dx * dx + dy * dy);
        double power = Math.min(dist / 16.0, MAX_POWER);
        double angle = Math.atan2(dy, dx) + Math.PI;

        gamePanel.setAimAngle(angle);
        gamePanel.setAimPower(power);
    }

    public void onMousePressed(int mx, int my) {
        if (gameState == null) {
            return;
        }

        if (gameState.getPhase() == GameState.Phase.PLACE_CUE_BALL) {
            tryPlaceCueBall(mx, my);
            return;
        }

        if (gameState.getPhase() == GameState.Phase.SELECT_POCKET) {
            trySelectPocket(mx, my);
        }
    }

    public void onMouseReleased(int mx, int my) {
        if (gameState == null) {
            return;
        }

        if (gameState.getPhase() == GameState.Phase.AIMING) {
            doShoot(mx, my);
        }
    }

    private void tryPlaceCueBall(int mx, int my) {
        Table table  = gameState.getTable();
        Ball  cueBall = gameState.getCueBall();

        boolean behindLine = mx < table.getBreakLineX();
        boolean inBounds   = mx > table.getLeftWall()   + Ball.RADIUS + 2
                          && my > table.getTopWall()    + Ball.RADIUS + 2
                          && my < table.getBottomWall() - Ball.RADIUS - 2;

        if (!behindLine || !inBounds) {
            return;
        }

        for (Ball ball : gameState.getBalls()) {
            if (ball.isCueBall() || ball.isPocketed()) {
                continue;
            }
            double dx   = mx - ball.getX();
            double dy   = my - ball.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < Ball.RADIUS * 2 + 2) {
                return;
            }
        }

        cueBall.setX(mx);
        cueBall.setY(my);
        cueBall.setPocketed(false);
        cueBall.setVelocityX(0);
        cueBall.setVelocityY(0);
        cueBall.setPocketAnimScale(1.0);
        gameState.getCurrentPlayer().setBallInHand(false);
        gameState.setPhase(GameState.Phase.SELECT_POCKET);
    }

    private void trySelectPocket(int mx, int my) {
        for (Pocket pocket : gameState.getTable().getPockets()) {
            double dx   = mx - pocket.getX();
            double dy   = my - pocket.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < Pocket.RADIUS + 16) {
                gameState.setSelectedPocket(pocket);
                gameState.setPhase(GameState.Phase.SELECT_SPIN);
                gamePanel.showSpinSelector();
                return;
            }
        }
    }

    public void onSpinSelected(SpinType spin) {
        gameState.setSelectedSpin(spin);
        gameState.setPhase(GameState.Phase.AIMING);
        gamePanel.hideSpinSelector();
    }

    private void doShoot(int mx, int my) {
        Ball   cue   = gameState.getCueBall();
        double dx    = mx - cue.getX();
        double dy    = my - cue.getY();
        double dist  = Math.sqrt(dx * dx + dy * dy);
        double power = Math.min(dist / 16.0, MAX_POWER);

        if (power < 0.8) {
            return;
        }

        double angle = Math.atan2(dy, dx);

        physicsEngine.resetTurnTracking();
        gamePanel.notifyShot();
        physicsEngine.shoot(cue, angle, power, gameState.getSelectedSpin());
        gameState.setPhase(GameState.Phase.BALLS_MOVING);
    }

    private void processTurnResult() {
        List<Ball> pocketed    = physicsEngine.getPocketedThisTurn();
        boolean    cuePocketed = physicsEngine.isCueBallPocketed();
        Ball       firstHit    = physicsEngine.getFirstHitBall();
        Player     current     = gameState.getCurrentPlayer();
        Player     opponent    = gameState.getOpponent(current);

        if (!gameState.isFirstBreakDone()) {
            handleBreak(pocketed, cuePocketed, current, opponent);
            return;
        }

        for (Ball b : pocketed) {
            if (b.isBlack()) {
                handleEightBall(current, opponent, cuePocketed);
                return;
            }
        }

        boolean foul = isFoul(current, firstHit, cuePocketed);

        if (foul) {
            doFoul(current, opponent, cuePocketed);
            return;
        }

        if (gameState.isTableOpen() && !pocketed.isEmpty()) {
            assignGroups(current, opponent, pocketed);
        }

        boolean scoredOwn = false;
        for (Ball b : pocketed) {
            if (b.isBlack()) {
                continue;
            }
            boolean own = gameState.isTableOpen()
                    || (current.getGroup() == Player.Group.SOLID  && !b.isStripe())
                    || (current.getGroup() == Player.Group.STRIPE && b.isStripe());
            if (own) {
                current.incrementScore();
                scoredOwn = true;
            }
        }

        gameState.setSelectedPocket(null);
        gameState.setSelectedSpin(SpinType.NONE);

        if (!scoredOwn && pocketed.isEmpty()) {
            doFoul(current, opponent, false);
            return;
        }

        if (scoredOwn) {
            Color c = (current == gameState.getPlayer1())
                    ? PlayerInfoPanel.PLAYER1_COLOR : PlayerInfoPanel.PLAYER2_COLOR;
            gamePanel.showAnnouncement(current.getName() + " scored!", c);
            gameState.setPhase(GameState.Phase.SELECT_POCKET);
        } else {
            switchTurn("Turn over!", opponent);
        }
    }

    private void handleBreak(List<Ball> pocketed, boolean cuePocketed,
                              Player current, Player opponent) {
        gameState.setFirstBreakDone(true);

        if (cuePocketed) {
            opponent.setBallInHand(true);
            switchTurn("FOUL on break!", opponent);
            return;
        }

        for (Ball b : pocketed) {
            if (b.isBlack()) {
                gamePanel.showAnnouncement("8-ball on break! Re-racking...", Color.YELLOW);
                restartGame();
                return;
            }
        }

        int wallHits   = physicsEngine.getWallHitsThisTurn();
        boolean valid  = !pocketed.isEmpty() || wallHits >= 4;

        if (!valid) {
            opponent.setBallInHand(true);
            switchTurn("Invalid break!", opponent);
            return;
        }

        gameState.setSelectedPocket(null);
        gameState.setSelectedSpin(SpinType.NONE);
        gameState.setPhase(GameState.Phase.SELECT_POCKET);

        Color c = (current == gameState.getPlayer1())
                ? PlayerInfoPanel.PLAYER1_COLOR : PlayerInfoPanel.PLAYER2_COLOR;
        gamePanel.showAnnouncement("Good break!", c);
    }

    private boolean isFoul(Player current, Ball firstHit, boolean cuePocketed) {
        if (cuePocketed) {
            return true;
        }

        if (firstHit == null) {
            return true;
        }

        if (!gameState.isTableOpen()) {
            int remaining = gameState.getRemainingCount(current.getGroup());

            if (remaining > 0) {
                if (current.getGroup() == Player.Group.SOLID) {
                    if (firstHit.isStripe() || firstHit.isBlack()) {
                        return true;
                    }
                }
                if (current.getGroup() == Player.Group.STRIPE) {
                    if (!firstHit.isStripe() && !firstHit.isBlack()) {
                        return true;
                    }
                }
                if (firstHit.isBlack()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void doFoul(Player current, Player opponent, boolean cuePocketed) {
        if (cuePocketed) {
            Ball cue = gameState.getCueBall();
            cue.setPocketed(false);
            cue.setVelocityX(0);
            cue.setVelocityY(0);
            cue.setPocketAnimScale(1.0);
        }

        opponent.setBallInHand(true);
        gameState.setSelectedPocket(null);
        gameState.setSelectedSpin(SpinType.NONE);
        switchTurn("FOUL! Ball in Hand", opponent);
    }

    private void handleEightBall(Player current, Player opponent, boolean cuePocketed) {
        if (cuePocketed) {
            endGame(opponent, "8-ball + scratch!");
            return;
        }

        int remaining = gameState.getRemainingCount(current.getGroup());
        if (remaining > 0) {
            endGame(opponent, "8-ball too early!");
            return;
        }

        Pocket selected = gameState.getSelectedPocket();
        if (selected == null) {
            endGame(opponent, "No pocket declared!");
            return;
        }

        Ball eight = null;
        for (Ball b : physicsEngine.getPocketedThisTurn()) {
            if (b.isBlack()) {
                eight = b;
                break;
            }
        }

        if (eight == null) {
            endGame(opponent, "Error!");
            return;
        }

        Pocket closestPocket = findClosestPocket(eight);
        double dx   = selected.getX() - closestPocket.getX();
        double dy   = selected.getY() - closestPocket.getY();
        boolean ok  = Math.sqrt(dx * dx + dy * dy) < 40;

        if (ok) {
            endGame(current, "Perfect finish!");
        } else {
            endGame(opponent, "Wrong pocket!");
        }
    }

    private Pocket findClosestPocket(Ball ball) {
        Pocket closest     = null;
        double closestDist = Double.MAX_VALUE;
        for (Pocket p : gameState.getTable().getPockets()) {
            double dx   = ball.getX() - p.getX();
            double dy   = ball.getY() - p.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < closestDist) {
                closestDist = dist;
                closest     = p;
            }
        }
        return closest;
    }

    private void assignGroups(Player current, Player opponent, List<Ball> pocketed) {
        Ball first = pocketed.get(0);
        if (first.isStripe()) {
            current.setGroup(Player.Group.STRIPE);
            opponent.setGroup(Player.Group.SOLID);
        } else {
            current.setGroup(Player.Group.SOLID);
            opponent.setGroup(Player.Group.STRIPE);
        }
        gameState.setTableOpen(false);

        String grp = (current.getGroup() == Player.Group.SOLID) ? "Solid" : "Stripe";
        Color  c   = (current == gameState.getPlayer1())
                   ? PlayerInfoPanel.PLAYER1_COLOR : PlayerInfoPanel.PLAYER2_COLOR;
        gamePanel.showAnnouncement(current.getName() + " is " + grp + "!", c);
    }

    private void switchTurn(String message, Player next) {
        Player old = gameState.getCurrentPlayer();
        old.setBallInHand(false);
        gameState.setCurrentPlayer(next);
        gameState.setSelectedPocket(null);
        gameState.setSelectedSpin(SpinType.NONE);

        Color c = (next == gameState.getPlayer1())
                ? PlayerInfoPanel.PLAYER1_COLOR : PlayerInfoPanel.PLAYER2_COLOR;
        gamePanel.showAnnouncement(message, next.getName() + "'s turn", c);

        if (next.hasBallInHand()) {
            gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);
        } else {
            gameState.setPhase(GameState.Phase.SELECT_POCKET);
        }
    }

    private void endGame(Player winner, String reason) {
        gameState.setPhase(GameState.Phase.GAME_OVER);

        GameRecord rec = new GameRecord(
                gameState.getPlayer1().getName(),
                gameState.getPlayer2().getName(),
                winner.getName(),
                LocalDate.now().toString());
        dbController.saveRecord(rec);

        if (gameLoop != null) {
            gameLoop.stop();
        }

        gamePanel.showGameOver(winner.getName());
    }

    private void restartGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        startNewGame(gameState.getPlayer1().getName(),
                     gameState.getPlayer2().getName());
    }

    public GameState getGameState() {
        return gameState;
    }
}