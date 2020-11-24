package Shared;

import Shared.CardEnum.Card;
import Shared.CardEnum.Instruction;

import java.io.Serializable;

public class PlayCommand implements Serializable {
    private String turn;
    private Instruction instruction;
    private Card card;

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
