package Server.Room;

import Server.Main;
import Shared.CardEnum.Card;

import java.util.List;

public class RoomFunction {
    public static boolean checkRoomPattern (Card[] chosenCards){
        List<Room> roomList = Main.getRoomList();
        for (Room room : roomList) {
            if(room.getChosenCards().equals(chosenCards)){
                return true;
            }
        }
        return false;
    }
}
