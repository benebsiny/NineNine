package Enum;

public enum Card {
    HA(1, Suit.HEART), H2(2, Suit.HEART), H3(3, Suit.HEART), H4(4, Suit.HEART),
    H5(5, Suit.HEART), H6(6, Suit.HEART), H7(7, Suit.HEART), H8(8, Suit.HEART),
    H9(9, Suit.HEART), HT(10, Suit.HEART), HJ(11, Suit.HEART), HQ(12, Suit.HEART),
    HK(13, Suit.HEART),

    SA(1, Suit.SPADE), S2(2, Suit.SPADE), S3(3, Suit.SPADE), S4(4, Suit.SPADE),
    S5(5, Suit.SPADE), S6(6, Suit.SPADE), S7(7, Suit.SPADE), S8(8, Suit.SPADE),
    S9(9, Suit.SPADE), ST(10, Suit.SPADE), SJ(11, Suit.SPADE), SQ(12, Suit.SPADE),
    SK(13, Suit.SPADE),

    DA(1, Suit.DIAMOND), D2(2, Suit.DIAMOND), D3(3, Suit.DIAMOND), D4(4, Suit.DIAMOND),
    D5(5, Suit.DIAMOND), D6(6, Suit.DIAMOND), D7(7, Suit.DIAMOND), D8(8, Suit.DIAMOND),
    D9(9, Suit.DIAMOND), DT(10, Suit.DIAMOND), DJ(11, Suit.DIAMOND), DQ(12, Suit.DIAMOND),
    DK(13, Suit.DIAMOND),

    CA(1, Suit.CARD), C2(2, Suit.CARD), C3(3, Suit.CARD), C4(4, Suit.CARD),
    C5(5, Suit.CARD), C6(6, Suit.CARD), C7(7, Suit.CARD), C8(8, Suit.CARD),
    C9(9, Suit.CARD), CT(10, Suit.CARD), CJ(11, Suit.CARD), CQ(12, Suit.CARD),
    CK(13, Suit.CARD);


    private int rank;
    private Suit suit;

    Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }
}
