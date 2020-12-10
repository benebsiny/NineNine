package Shared.Command.Game;

import java.io.Serializable;

public class LoseGameCommand implements Serializable {

    private static final long serialVersionUID = 1L;
    private String losePlayer;

    public String getLosePlayer() {
        return losePlayer;
    }

    public void setLosePlayer(String losePlayer) {
        this.losePlayer = losePlayer;
    }

    public LoseGameCommand(String losePlayer) {
        this.losePlayer = losePlayer;
    }


}
