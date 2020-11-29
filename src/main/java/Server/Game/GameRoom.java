package Server.Game;

import Shared.CardEnum.Card;
import java.util.Arrays;
import java.util.List;

public class GameRoom {

    private String[] playersName;
    private Card[] usedCards;

    public String[] getPlayersName() {
        return Arrays.copyOf(playersName, playersName.length);
    }

    public void setPlayersName(String[] playersName) {
        this.playersName = Arrays.copyOf(playersName, playersName.length);
    }

    public void addUsedCard(Card card){
        List<Card> list = Arrays.asList(usedCards.clone());
        list.add(card);
        this.usedCards = (Card[])list.toArray();
    }
}
