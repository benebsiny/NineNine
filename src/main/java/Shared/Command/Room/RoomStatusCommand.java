package Shared.Command.Room;

import java.io.Serializable;

public class RoomStatusCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] players = null;
    private RoomStatus roomStatus;

    public String[] getPlayers() {
        String[] clonePlayers = new String[players.length];
        System.arraycopy(players, 0, clonePlayers, 0, players.length);
        return clonePlayers;
    }

    public void setPlayers(String[] players) {
        String[] clonePlayers = new String[players.length];
        System.arraycopy(players, 0, clonePlayers, 0, players.length);
        this.players = clonePlayers;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public enum RoomStatus {
        FOUND, NOT_FOUND, REPEATED, FULL, CREATE
    }
}
