package Client.Status;

import java.util.Arrays;

public class PlayerStatus {
    private static String[] players;

    public static String[] getPlayers() {
        return Arrays.copyOf(PlayerStatus.players, PlayerStatus.players.length);
    }

    public static void setPlayers(String[] players) {
        PlayerStatus.players = Arrays.copyOf(players, players.length);
    }
}
