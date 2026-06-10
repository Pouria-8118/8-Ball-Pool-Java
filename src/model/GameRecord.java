package model;

public class GameRecord {

    private String player1Name;
    private String player2Name;
    private String winnerName;
    private int player1Score;
    private int player2Score;
    private String date;

    public GameRecord(String player1Name, String player2Name,
                      String winnerName, int player1Score, int player2Score, String date) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.winnerName  = winnerName;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.date        = date;
    }

    public GameRecord(String player1Name, String player2Name,
                      String winnerName, String date) {
        this(player1Name, player2Name, winnerName, 0, 0, date);
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public String getDate() {
        return date;
    }
}