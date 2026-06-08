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


}
