package Shared;

public class RoomPlayerCommand {

    private String[] player;

    public RoomPlayerCommand(String[] player) {
        String[] clonePlayer = new String[player.length];
        System.arraycopy(clonePlayer, 0, player, 0, clonePlayer.length);
        this.player = clonePlayer;
    }

    public String[] getPlayer() {
        String[] clonePlayer = new String[player.length];
        System.arraycopy(player, 0, clonePlayer, 0, player.length);
        return clonePlayer;
    }

    public void setPlayer(String[] player) {
        String[] clonePlayer = new String[player.length];
        System.arraycopy(clonePlayer, 0, player, 0, clonePlayer.length);
        this.player = clonePlayer;
    }
}
