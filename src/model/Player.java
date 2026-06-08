package model;

public class Player {

    public static final int GROUP_NONE = 0;
    public static final int GROUP_SOLID = 1;
    public static final int GROUP_STRIPE = 2;

    private String name;
    private int group;
    private int score;
    private boolean hasBallInHand;

    public Player(String name) {
        this.name = name;
        this.group = GROUP_NONE;
        this.score = 0;
        this.hasBallInHand = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public boolean hasBallInHand() {
        return hasBallInHand;
    }

    public void setBallInHand(boolean ballInHand) {
        this.hasBallInHand = ballInHand;
    }

    public String getGroupName() {
        if (group == GROUP_SOLID) {
            return "Solid";
        }
        if (group == GROUP_STRIPE) {
            return "Stripe";
        }
        return "Open";
    }
}