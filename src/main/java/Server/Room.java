package Server;

import Shared.CardEnum.Card;
import Shared.User;

public class Room {
    private String[] usersname;
    private Card[] chosenCards;

    public Room(Card[] chosenCards) {
        this.chosenCards = chosenCards;
    }

    public String[] getUsersname() {
        return usersname;
    }

    public void setUsername(String username) {
        usersname[usersname.length-1] = username;
    }

    public Card[] getChosenCards() {
        return chosenCards;
    }


}
