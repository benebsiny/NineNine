package Shared.Command.Game;

import java.io.Serializable;

public class NoRemainCardsWinnerCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


}
