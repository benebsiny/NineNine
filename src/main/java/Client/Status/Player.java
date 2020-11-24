package Client.Status;

public class Player {
    private static String[] players = new String[4];

    public static String[] getPlayers() {
        return players;
    }

    public static void setPlayers(String[] players) {
        Player.players = players;
    }
}
