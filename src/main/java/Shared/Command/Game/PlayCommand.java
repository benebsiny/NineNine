package Shared.Command.Game;

import Shared.CardEnum.Card;

import java.io.Serializable;

public class PlayCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String player;
    private Card card;
    private int value;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
