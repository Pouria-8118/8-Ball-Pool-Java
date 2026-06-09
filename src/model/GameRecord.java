package model;

public class GameRecord {

    private String player1Name;
    private String player2Name;
    private String winnerName;
    private String date;

    public GameRecord(String player1Name, String player2Name,
                      String winnerName, String date) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.winnerName  = winnerName;
        this.date        = date;
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

    public String getDate() {
        return date;
    }
}