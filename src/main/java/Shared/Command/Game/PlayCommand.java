package Shared.Command.Game;

import Shared.CardEnum.Card;

import java.io.Serializable;

public class PlayCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String player;
    private String assignPlayer; // This is to determine who is the next player for card 5
    private boolean plusValue; // This is to determine plus/minus 10/20 for card 10 and card Q
    private int remainCardCount;
    private Card card;

    public String getAssignPlayer() {
        return assignPlayer;
    }

    public void setAssignPlayer(String assignPlayer) {
        this.assignPlayer = assignPlayer;
    }

    public int getRemainCardCount() {
        return remainCardCount;
    }

    public void setRemainCardCount(int remainCardCount) {
        this.remainCardCount = remainCardCount;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public boolean isPlusValue() {
        return plusValue;
    }

    public void setPlusValue(boolean plusValue) {
        this.plusValue = plusValue;
    }
}
