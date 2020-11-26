package Shared.Command.Room;

import java.io.Serializable;

public class RoomPlayerCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private String[] players;

    public RoomPlayerCommand(String[] players) {
        String[] clonePlayer = new String[players.length];
        System.arraycopy(clonePlayer, 0, players, 0, clonePlayer.length);
        this.players = clonePlayer;
    }

    public String[] getPlayers() {
        String[] clonePlayer = new String[players.length];
        System.arraycopy(players, 0, clonePlayer, 0, players.length);
        return clonePlayer;
    }

    public void setPlayers(String[] players) {
        String[] clonePlayer = new String[players.length];
        System.arraycopy(clonePlayer, 0, players, 0, clonePlayer.length);
        this.players = clonePlayer;
    }
}
