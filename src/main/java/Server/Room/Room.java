package Server.Room;

import Shared.CardEnum.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    private String[] playersName = new String[0];
    private final Card[] chosenCards;

    public Room(Card[] chosenCards) {
        this.chosenCards = Arrays.copyOf(chosenCards, chosenCards.length);
    }

    public String[] getPlayersName() {
        return Arrays.copyOf(playersName, playersName.length);
    }

    public void addPlayer(String player) {
        ArrayList<String>  list = new ArrayList<>(Arrays.asList(playersName.clone()));
        list.add(player);
        this.playersName = (String[])list.toArray(new String[list.size()]);
    }

    public Card[] getChosenCards() {
        return Arrays.copyOf(chosenCards, chosenCards.length);
    }

    public void setPlayersName(String[] playersName) {
        this.playersName = Arrays.copyOf(playersName, playersName.length);
    }
}
