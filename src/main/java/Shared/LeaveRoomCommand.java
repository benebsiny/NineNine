package Shared;

import java.io.Serializable;

public class LeaveRoomCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private String player;

    public LeaveRoomCommand(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
