package Client.Status;

import java.util.Arrays;

public class PlayerStatus {
    private static String[] players;
    private static String[] turnPlayers;

    public static String[] getPlayers() {
        return Arrays.copyOf(PlayerStatus.players, PlayerStatus.players.length);
    }

    public static void setPlayers(String[] players) {
        PlayerStatus.players = Arrays.copyOf(players, players.length);
        countTurnPlayer();
    }

    public static String[] getTurnPlayers() {
        return Arrays.copyOf(turnPlayers, turnPlayers.length);
    }

    public static void countTurnPlayer() {
        turnPlayers = new String[players.length];

        int index = Arrays.asList(players).indexOf(UserStatus.getSignInUser()); // Get my turn ID of new players

        // My turnID must be 0
        for (int i = index, j = 0; j < players.length; i = (i + 1) % players.length, j++) {
            turnPlayers[j] = players[i];
        }
    }
}
