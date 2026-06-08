package controller;

import model.*;
import physics.PhysicsEngine;
import view.GamePanel;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {

    private static final int TABLE_X = 60;
    private static final int TABLE_Y = 60;
    private static final int TABLE_WIDTH = 800;
    private static final int TABLE_HEIGHT = 400;
    private static final double MAX_POWER = 18.0;

    private GameState gameState;
    private PhysicsEngine physicsEngine;
    private GamePanel gamePanel;
    private DatabaseController databaseController;
    private Timer gameLoop;
    private boolean isRunning;

    public GameController(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.physicsEngine = new PhysicsEngine();
        this.databaseController = new DatabaseController();
        this.isRunning = false;
    }

    public void startNewGame(String player1Name, String player2Name) {
        Player player1 = new Player(player1Name);
        Player player2 = new Player(player2Name);
        Table table = new Table(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT);
        gameState = new GameState(player1, player2, table);

        List<Ball> balls = createBalls(table);
        gameState.setBalls(balls);

        for (Ball ball : balls) {
            if (ball.isCueBall()) {
                gameState.setCueBall(ball);
            }
        }

        Random random = new Random();
        if (random.nextBoolean()) {
            gameState.setCurrentPlayer(player1);
        } else {
            gameState.setCurrentPlayer(player2);
        }

        gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);
        startGameLoop();
    }

    private List<Ball> createBalls(Table table) {
        List<Ball> balls = new ArrayList<>();

        double rackX = table.getX() + table.getWidth() * 3.0 / 4.0;
        double rackY = table.getY() + table.getHeight() / 2.0;
        double spacing = Ball.RADIUS * 2 + 1;

        java.awt.Color[] solidColors = {
            java.awt.Color.YELLOW,
            java.awt.Color.BLUE,
            java.awt.Color.RED,
            java.awt.Color.MAGENTA,
            java.awt.Color.ORANGE,
            java.awt.Color.GREEN,
            new java.awt.Color(139, 0, 0)
        };

        java.awt.Color[] stripeColors = {
            java.awt.Color.YELLOW,
            java.awt.Color.BLUE,
            java.awt.Color.RED,
            java.awt.Color.MAGENTA,
            java.awt.Color.ORANGE,
            java.awt.Color.GREEN,
            new java.awt.Color(139, 0, 0)
        };

        double[][] rackPositions = {
            {0, 0},
            {1, -0.5}, {1, 0.5},
            {2, -1}, {2, 0}, {2, 1},
            {3, -1.5}, {3, -0.5}, {3, 0.5}, {3, 1.5},
            {4, -2}, {4, -1}, {4, 0}, {4, 1}, {4, 2}
        };

        int[] rackOrder = {1, 9, 2, 10, 8, 3, 11, 4, 12, 5, 13, 6, 14, 7, 15};

        for (int i = 0; i < 15; i++) {
            double px = rackX + rackPositions[i][0] * spacing * 0.87;
            double py = rackY + rackPositions[i][1] * spacing;
            int num = rackOrder[i];

            if (num == 8) {
                balls.add(new Ball(8, px, py, java.awt.Color.BLACK, false, false, true));
            } else if (num <= 7) {
                balls.add(new Ball(num, px, py, solidColors[num - 1], false, false, false));
            } else {
                balls.add(new Ball(num, px, py, stripeColors[num - 9], true, false, false));
            }
        }

        double cueX = table.getX() + table.getWidth() / 4.0;
        double cueY = table.getY() + table.getHeight() / 2.0;
        balls.add(new Ball(0, cueX, cueY, java.awt.Color.WHITE, false, true, false));

        return balls;
    }

    private void startGameLoop() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stop();
        }
        isRunning = true;
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

    public void onMouseMoved(int mouseX, int mouseY) {
        if (gameState == null) {
            return;
        }
        if (gameState.getPhase() == GameState.Phase.AIMING) {
            gamePanel.setAimAngle(calculateAngle(mouseX, mouseY));
            gamePanel.setAimPower(calculatePower(mouseX, mouseY));
        }
    }

    public void onMousePressed(int mouseX, int mouseY) {
        if (gameState == null) {
            return;
        }

        if (gameState.getPhase() == GameState.Phase.PLACE_CUE_BALL) {
            placeCueBall(mouseX, mouseY);
            return;
        }

        if (gameState.getPhase() == GameState.Phase.SELECT_POCKET) {
            trySelectPocket(mouseX, mouseY);
            return;
        }
    }

    public void onMouseReleased(int mouseX, int mouseY) {
        if (gameState == null) {
            return;
        }
        if (gameState.getPhase() == GameState.Phase.AIMING) {
            performShot(mouseX, mouseY);
        }
    }

    private void placeCueBall(int mouseX, int mouseY) {
        Ball cueBall = gameState.getCueBall();
        Table table = gameState.getTable();

        if (mouseX < table.getBreakLineX() && mouseX > table.getLeftWall() + Ball.RADIUS
                && mouseY > table.getTopWall() + Ball.RADIUS
                && mouseY < table.getBottomWall() - Ball.RADIUS) {
            cueBall.setX(mouseX);
            cueBall.setY(mouseY);
            cueBall.setPocketed(false);
            gameState.setPhase(GameState.Phase.SELECT_POCKET);
        }
    }

    private void trySelectPocket(int mouseX, int mouseY) {
        for (Pocket pocket : gameState.getTable().getPockets()) {
            double dx = mouseX - pocket.getX();
            double dy = mouseY - pocket.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < Pocket.RADIUS + 10) {
                gameState.setSelectedPocket(pocket);
                gameState.setPhase(GameState.Phase.SELECT_SPIN);
                gamePanel.showSpinMenu();
                return;
            }
        }
    }

    public void onSpinSelected(SpinType spin) {
        gameState.setSelectedSpin(spin);
        gameState.setPhase(GameState.Phase.AIMING);
        gamePanel.hideSpinMenu();
    }

    private double calculateAngle(int mouseX, int mouseY) {
        Ball cueBall = gameState.getCueBall();
        double dx = mouseX - cueBall.getX();
        double dy = mouseY - cueBall.getY();
        return Math.atan2(dy, dx);
    }

    private double calculatePower(int mouseX, int mouseY) {
        Ball cueBall = gameState.getCueBall();
        double dx = mouseX - cueBall.getX();
        double dy = mouseY - cueBall.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        double power = dist / 20.0;
        if (power > MAX_POWER) {
            power = MAX_POWER;
        }
        return power;
    }

    private void performShot(int mouseX, int mouseY) {
        double angle = calculateAngle(mouseX, mouseY);
        double power = calculatePower(mouseX, mouseY);

        if (power < 0.5) {
            return;
        }

        physicsEngine.resetTurnTracking();
        Ball cueBall = gameState.getCueBall();
        physicsEngine.shoot(cueBall, angle, power, gameState.getSelectedSpin());
        gameState.setPhase(GameState.Phase.BALLS_MOVING);
    }

    private void processTurnResult() {
        List<Ball> pocketed = physicsEngine.getPocketedThisTurn();
        boolean cueBallPocketed = physicsEngine.isCueBallPocketed();
        Ball firstHit = physicsEngine.getFirstHitBall();
        Player current = gameState.getCurrentPlayer();
        Player opponent = gameState.getOpponent(current);

        if (!gameState.isFirstBreakDone()) {
            gameState.setFirstBreakDone(true);
            boolean validBreak = !pocketed.isEmpty() || physicsEngine.getWallHitsThisTurn() >= 4;

            if (!validBreak) {
                gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);
                switchTurn();
                return;
            }

            for (Ball ball : pocketed) {
                if (ball.isBlack()) {
                    restartGame();
                    return;
                }
            }
        }

        boolean foul = detectFoul(current, firstHit, pocketed, cueBallPocketed);

        if (foul) {
            if (cueBallPocketed) {
                Ball cue = gameState.getCueBall();
                cue.setPocketed(false);
                cue.setVelocityX(0);
                cue.setVelocityY(0);
            }
            opponent.setBallInHand(true);
            switchTurn();
            gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);
            return;
        }

        for (Ball ball : pocketed) {
            if (ball.isBlack()) {
                handleBlackBallPocketed(current, opponent);
                return;
            }
        }

        if (gameState.isTableOpen() && !pocketed.isEmpty()) {
            assignGroups(current, opponent, pocketed);
        }

        boolean scoredOwnBall = false;
        for (Ball ball : pocketed) {
            if (ball.isBlack()) {
                continue;
            }
            if (gameState.isTableOpen()) {
                current.incrementScore();
                scoredOwnBall = true;
            } else if (current.getGroup() == Player.Group.SOLID && !ball.isStripe()) {
                current.incrementScore();
                scoredOwnBall = true;
            } else if (current.getGroup() == Player.Group.STRIPE && ball.isStripe()) {
                current.incrementScore();
                scoredOwnBall = true;
            }
        }

        if (!scoredOwnBall) {
            switchTurn();
        }

        gameState.setSelectedPocket(null);
        gameState.setSelectedSpin(SpinType.NONE);

        if (gameState.getPhase() != GameState.Phase.GAME_OVER) {
            if (gameState.getCurrentPlayer().hasBallInHand()) {
                gameState.setPhase(GameState.Phase.PLACE_CUE_BALL);
            } else {
                gameState.setPhase(GameState.Phase.SELECT_POCKET);
            }
        }
    }

    private boolean detectFoul(Player current, Ball firstHit, List<Ball> pocketed, boolean cueBallPocketed) {
        if (cueBallPocketed) {
            return true;
        }

        if (firstHit == null) {
            return true;
        }

        if (!gameState.isTableOpen()) {
            if (current.getGroup() == Player.Group.SOLID && firstHit.isStripe()) {
                return true;
            }
            if (current.getGroup() == Player.Group.STRIPE && !firstHit.isStripe() && !firstHit.isBlack()) {
                return true;
            }

            if (gameState.getRemainingCount(current.getGroup()) > 0 && firstHit.isBlack()) {
                return true;
            }
        }

        return false;
    }

    private void assignGroups(Player current, Player opponent, List<Ball> pocketed) {
        Ball firstPocketed = pocketed.get(0);
        if (firstPocketed.isStripe()) {
            current.setGroup(Player.Group.STRIPE);
            opponent.setGroup(Player.Group.SOLID);
        } else {
            current.setGroup(Player.Group.SOLID);
            opponent.setGroup(Player.Group.STRIPE);
        }
        gameState.setTableOpen(false);
    }

    private void handleBlackBallPocketed(Player current, Player opponent) {
        Pocket selected = gameState.getSelectedPocket();
        boolean pocketedInSelectedPocket = false;

        if (selected != null) {
            for (Ball ball : physicsEngine.getPocketedThisTurn()) {
                if (ball.isBlack()) {
                    double dx = ball.getX() - selected.getX();
                    double dy = ball.getY() - selected.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < Pocket.RADIUS * 2) {
                        pocketedInSelectedPocket = true;
                    }
                }
            }
        }

        int remaining = 0;
        if (!gameState.isTableOpen()) {
            remaining = gameState.getRemainingCount(current.getGroup());
        }

        if (remaining > 0 || !pocketedInSelectedPocket || physicsEngine.isCueBallPocketed()) {
            endGame(opponent);
        } else {
            endGame(current);
        }
    }

    private void endGame(Player winner) {
        gameState.setPhase(GameState.Phase.GAME_OVER);
        Player loser = gameState.getOpponent(winner);
        String date = LocalDate.now().toString();
        GameRecord record = new GameRecord(
                gameState.getPlayer1().getName(),
                gameState.getPlayer2().getName(),
                winner.getName(),
                date
        );
        databaseController.saveRecord(record);
        gamePanel.showGameOver(winner.getName());

        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void restartGame() {
        String p1 = gameState.getPlayer1().getName();
        String p2 = gameState.getPlayer2().getName();
        startNewGame(p1, p2);
    }
    private void switchTurn() {
        Player current = gameState.getCurrentPlayer();
        current.setBallInHand(false);
        gameState.setCurrentPlayer(gameState.getOpponent(current));
        Player next = gameState.getCurrentPlayer();
        gamePanel.showAnnouncement(next.getName() + "'s Turn");
}

    public GameState getGameState() { 
        return gameState; 
    }
}