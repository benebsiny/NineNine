package Shared;

import Shared.CardEnum.Card;

import java.io.Serializable;

public class RoomCommand implements Serializable {
    private RoomAction action;
    private Card[] chosenCards;

    public RoomCommand(RoomAction action, Card[] chosenCards) {
        this.action = action;

        // Copy card array
        Card[] cloneCards = new Card[chosenCards.length];
        System.arraycopy(chosenCards, 0, cloneCards, 0, chosenCards.length);
        this.chosenCards = cloneCards;
    }

    public RoomAction getAction() {
        return action;
    }

    public void setAction(RoomAction action) {
        this.action = action;
    }

    public Card[] getChosenCards() {
        Card[] cloneCards = new Card[chosenCards.length];
        System.arraycopy(chosenCards, 0, cloneCards, 0, chosenCards.length);
        return cloneCards;
    }

    public void setChosenCards(Card[] chosenCards) {
        Card[] cloneCards = new Card[chosenCards.length];
        System.arraycopy(chosenCards, 0, cloneCards, 0, chosenCards.length);
        this.chosenCards = cloneCards;
    }

    public enum RoomAction {
        CREATE, CHOOSE
    }
}
