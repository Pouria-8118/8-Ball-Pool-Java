package model;

public class Player {

    public enum Group {
        NONE,
        SOLID,
        STRIPE
    }

    private String name;
    private Group group;
    private int score;
    private boolean hasBallInHand;

    public Player(String name) {
        this.name = name;
        this.group = Group.NONE;
        this.score = 0;
        this.hasBallInHand = false;
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
        return hasBallInHand;
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
        this.hasBallInHand = ballInHand; 
    }
    public void incrementScore() { 
        this.score++; 
    }
}