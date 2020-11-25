package Shared;

import Shared.CardEnum.Card;

import java.io.Serializable;

public class PlayCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private Card card;
    private int value;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
