package Server.Room;

import Shared.CardEnum.Card;

public class Room {
    private String[] usersname;
    private Card[] chosenCards;

    public Room(Card[] chosenCards) {
        Card[] cloneCards = new Card[chosenCards.length];
        System.arraycopy(chosenCards, 0, cloneCards, 0, chosenCards.length);
        this.chosenCards = cloneCards;
    }

    public String[] getUsersname() {
        return usersname;
    }

    public void setUsername(String username) {
        usersname[usersname.length - 1] = username;
    }

    public Card[] getChosenCards() {
        return chosenCards;
    }


}
