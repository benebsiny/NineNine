package Shared.Command.Game;

import Shared.CardEnum.Card;

import java.io.Serializable;

public class ReturnPlayCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String player;
    private int remainCardCount;
    private Card card;
    private int value;
    private boolean hasCardsInDesk;

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isHasCardsInDesk() {
        return hasCardsInDesk;
    }

    public void setHasCardsInDesk(boolean hasCardsInDesk) {
        this.hasCardsInDesk = hasCardsInDesk;
    }
}
