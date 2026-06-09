package model;

public class Player {

    public enum Group {
        NONE,
        SOLID,
        STRIPE
    }

    private String  name;
    private Group   group;
    private int     score;
    private boolean ballInHand;

    public Player(String name) {
        this.name       = name;
        this.group      = Group.NONE;
        this.score      = 0;
        this.ballInHand = false;
    }

    public void incrementScore() {
        this.score++;
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public int getScore() {
        return score;
    }

    public boolean hasBallInHand() {
        return ballInHand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setBallInHand(boolean ballInHand) {
        this.ballInHand = ballInHand;
    }
}