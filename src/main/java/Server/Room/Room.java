package Server.Room;

import Shared.CardEnum.Card;

public class Room {
    private String[] playersName;
    private Card[] chosenCards;

    public Room(Card[] chosenCards) {
        Card[] cloneCards = new Card[chosenCards.length];
        System.arraycopy(chosenCards, 0, cloneCards, 0, chosenCards.length);
        this.chosenCards = cloneCards;
    }

    public String[] getPlayersName() {
        return playersName;
    }

    public void addPlayer(String player) {
        this.playersName[playersName.length] = player;
    }

    public Card[] getChosenCards() {
        return chosenCards;
    }

    public void setPlayersName(String[] playersName) {
        this.playersName = playersName;
    }
}
