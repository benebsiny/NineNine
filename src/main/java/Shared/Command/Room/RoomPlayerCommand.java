package Shared.Command.Room;

import java.io.Serializable;
import java.util.Arrays;

public class RoomPlayerCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private String[] players;

    public RoomPlayerCommand(String[] players) {
        this.players = Arrays.copyOf(players, players.length);
    }

    public String[] getPlayers() {
        return Arrays.copyOf(players, players.length);
    }

    public void setPlayers(String[] players) {
        this.players = Arrays.copyOf(players, players.length);
    }
}
