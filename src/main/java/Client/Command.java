package Client;

import Client.CardEnum.*;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Instruction instruction;
    private Card card;


    public Command(Instruction instruction, Card card) {
        this.instruction = instruction;
        this.card = card;
    }

    public Command(Instruction instruction) {
        this.instruction = instruction;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public Card getCard() {
        return card;
    }
}
