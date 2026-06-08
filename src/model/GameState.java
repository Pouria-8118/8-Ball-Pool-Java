package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public static final int SPIN_STOP = 0;
    public static final int SPIN_TOP = 1;
    public static final int SPIN_BACK = -1;

    public static final int STATE_BREAK = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_BALL_IN_HAND = 2;
    public static final int STATE_POCKET_SELECT = 3;
    public static final int STATE_SPIN_SELECT = 4;
    public static final int STATE_AIMING = 5;
    public static final int STATE_GAME_OVER = 6;

    private Player player1;
    private Player player2;
    private int currentPlayerIndex;
    private List<Ball> balls;
    private Ball cueBall;
    private int selectedPocketIndex;
    private int selectedSpin;
    private int gameState;
    private boolean tableOpen;
    private boolean breakDone;
    private String winnerName;


    public GameState(String name1, String name2) {
        player1 = new Player(name1);
        player2 = new Player(name2);
        currentPlayerIndex = 0;
        balls = new ArrayList<Ball>();
        selectedPocketIndex = -1;
        selectedSpin = SPIN_STOP;
        gameState = STATE_BREAK;
        tableOpen = true;
        breakDone = false;
        winnerName = null;
    }

    public Player getCurrentPlayer() {
        if (currentPlayerIndex == 0) {
            return player1;
        }
        return player2;
    }

    public Player getOtherPlayer() {
        if (currentPlayerIndex == 0) {
            return player2;
        }
        return player1;
    }

    public void switchTurn() {
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = 1;
        } else {
            currentPlayerIndex = 0;
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public List<Ball> getBalls() {
        return balls;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public Ball getCueBall() {
        return cueBall;
    }

    public void setCueBall(Ball cueBall) {
        this.cueBall = cueBall;
    }

    public int getSelectedPocketIndex() {
        return selectedPocketIndex;
    }

    public void setSelectedPocketIndex(int index) {
        this.selectedPocketIndex = index;
    }

    public int getSelectedSpin() {
        return selectedSpin;
    }

    public void setSelectedSpin(int spin) {
        this.selectedSpin = spin;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int state) {
        this.gameState = state;
    }

    public boolean isTableOpen() {
        return tableOpen;
    }
    public void setTableOpen(boolean open) {
        this.tableOpen = open;
    }

    public boolean isBreakDone() {
        return breakDone;
    }

    public void setBreakDone(boolean done) {
        this.breakDone = done;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String name) {
        this.winnerName = name;
    }

    public boolean allBallsStopped() {
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            if (!b.isPocketed() && b.isMoving()) {
                return false;
            }
        }
        return true;
    }

    public int countRemainingBalls(int type) {
        int count = 0;
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            if (b.getType() == type && !b.isPocketed()) {
                count++;
            }
        }
        return count;
    }
}

