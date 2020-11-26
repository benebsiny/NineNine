package Shared.Command.Game;

import java.io.Serializable;

public class NextPlayerCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nextPlayer;

    public NextPlayerCommand(String nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(String nextPlayer) {
        this.nextPlayer = nextPlayer;
    }
}
