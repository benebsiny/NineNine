package Shared.Command.Game;

public class LoseGameCommand {

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
