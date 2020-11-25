package Client.Status;

public class PlayerStatus {
    private static String[] players;

    public static String[] getPlayers() {
        String[] clonePlayers = new String[players.length];
        System.arraycopy(PlayerStatus.players, 0, clonePlayers, 0, players.length);
        return clonePlayers;
    }

    public static void setPlayers(String[] players) {
        System.arraycopy(players, 0, PlayerStatus.players, 0, players.length);
    }
}
