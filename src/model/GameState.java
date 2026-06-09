package model;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public enum Phase {
        PLACE_CUE_BALL,
        SELECT_POCKET,
        SELECT_SPIN,
        AIMING,
        BALLS_MOVING,
        GAME_OVER
    }

    private List<Ball>  balls;
    private Table       table;
    private Player      player1;
    private Player      player2;
    private Player      currentPlayer;
    private Phase       phase;
    private Pocket      selectedPocket;
    private SpinType    selectedSpin;
    private boolean     tableOpen;
    private boolean     firstBreakDone;
    private Ball        cueBall;

    public GameState(Player player1, Player player2, Table table) {
        this.player1        = player1;
        this.player2        = player2;
        this.table          = table;
        this.balls          = new ArrayList<>();
        this.tableOpen      = true;
        this.firstBreakDone = false;
        this.selectedSpin   = SpinType.NONE;
        this.phase          = Phase.PLACE_CUE_BALL;
    }

    public boolean allBallsStopped() {
        for (Ball ball : balls) {
            if (!ball.isPocketed() && ball.isMoving()) {
                return false;
            }
            if (ball.getPocketAnimScale() > 0 && ball.getPocketAnimScale() < 1.0) {
                return false;
            }
        }
        return true;
    }

    public int getRemainingCount(Player.Group group) {
        int count = 0;
        for (Ball ball : balls) {
            if (ball.isPocketed()) {
                continue;
            }
            if (ball.isBlack() || ball.isCueBall()) {
                continue;
            }
            if (group == Player.Group.SOLID && !ball.isStripe()) {
                count++;
            }
            if (group == Player.Group.STRIPE && ball.isStripe()) {
                count++;
            }
        }
        return count;
    }

    public Player getOpponent(Player player) {
        if (player == player1) {
            return player2;
        }
        return player1;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public Table getTable() {
        return table;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Phase getPhase() {
        return phase;
    }

    public Pocket getSelectedPocket() {
        return selectedPocket;
    }

    public SpinType getSelectedSpin() {
        return selectedSpin;
    }

    public boolean isTableOpen() {
        return tableOpen;
    }

    public boolean isFirstBreakDone() {
        return firstBreakDone;
    }

    public Ball getCueBall() {
        return cueBall;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public void setSelectedPocket(Pocket pocket) {
        this.selectedPocket = pocket;
    }

    public void setSelectedSpin(SpinType spin) {
        this.selectedSpin = spin;
    }

    public void setTableOpen(boolean tableOpen) {
        this.tableOpen = tableOpen;
    }

    public void setFirstBreakDone(boolean done) {
        this.firstBreakDone = done;
    }

    public void setCueBall(Ball cueBall) {
        this.cueBall = cueBall;
    }
}