package Server.Game;

import Server.Main;
import Shared.CardEnum.Card;
import Shared.Command.Game.PlayCommand;

import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManageGameRoomValue {

    public static void manageGameRoomValue(PlayCommand playCommand){
        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        String playPlayer = playCommand.getPlayer();

        GameRoom playRoom = null;
        Card card = playCommand.getCard();

        for (GameRoom gameRoom : gameRoomList) {          //確定玩家的遊戲房間
            if (Arrays.asList(gameRoom.getPlayersName()).contains(playPlayer)) {
                playRoom = gameRoom;
            }
        }

        //if(card.getRank()==2 || card.getRank()==3 || ())
    }
}
