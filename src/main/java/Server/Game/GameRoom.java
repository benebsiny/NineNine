package Server.Game;

import Shared.CardEnum.Card;
import java.util.Arrays;


public class GameRoom {

    private String[] playersName;

    private final Card[] desk;
    private int deskIndex = 0;
    private Order order = GameRoom.Order.Clockwise;
    private int value = 0;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public GameRoom(Card[] desk) {
        this.desk = Arrays.copyOf(desk, desk.length);
    }

    public String[] getPlayersName() {
        return Arrays.copyOf(playersName, playersName.length);
    }

    public void setPlayersName(String[] playersName) {
        this.playersName = Arrays.copyOf(playersName, playersName.length);
    }

    public Card[] getDesk() {
        return Arrays.copyOf(desk, desk.length);
    }

    public int getDeskIndex() {
        return deskIndex;
    }

    public void setDeskIndex(int deskIndex) {
        this.deskIndex = deskIndex;
    }


    public enum Order {
        Clockwise,
        Counterclockwise
    }
}
