package Client.Status;

import Shared.CardEnum.Card;

import java.util.Arrays;

public class RoomCardStatus {
    static Card[] cards = new Card[3];

    public static void setCards(Card[] cards) {
        RoomCardStatus.cards = Arrays.copyOf(cards, cards.length);
    }

    public static Card[] getCards() {
        return Arrays.copyOf(cards, cards.length);
    }
}
