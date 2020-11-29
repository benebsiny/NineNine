package Shared.Command.Game;
import Shared.CardEnum.Card;

import java.io.Serializable;
import java.util.Arrays;

public class DrawCommand implements Serializable  {
    private static final long serialVersionUID = 1L;

    private Card[] drawCards;

    public DrawCommand(Card[] drawCards) {
        this.drawCards = Arrays.copyOf(drawCards, drawCards.length);
    }

    public Card[] getDrawCards() {
        return Arrays.copyOf(drawCards, drawCards.length);
    }

    public void setDrawCards(Card[] drawCards) {
        this.drawCards = Arrays.copyOf(drawCards, drawCards.length);
    }


}
