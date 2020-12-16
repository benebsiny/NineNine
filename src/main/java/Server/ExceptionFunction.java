package Server;

import Server.Game.GameRoom;
import Server.Room.Room;
import Shared.Command.Game.LoseGameCommand;
import Shared.Command.Room.RoomDisbandCommand;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static Server.ClientMap.ClientMapFunction.getClientUsername;

public class ExceptionFunction {

    public static boolean EOFExceptionInGameRoom(String disconnectPlayerName) throws IOException {

        Map<String, Socket> clientMap = Main.getClientMap();
        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();

        LoseGameCommand loseGameCommand = new LoseGameCommand(disconnectPlayerName);

        boolean inGameRoom = false;

        for (GameRoom gameRoom : gameRoomList) {
            if (Arrays.asList(gameRoom.getPlayersName()).contains(disconnectPlayerName)) {

                inGameRoom = true;
                String[] roomPlayers = gameRoom.getPlayersName();

                for (String roomPlayer : roomPlayers) {             //發出loseGameCommand給房間除了斷線玩家

                    if (!roomPlayer.equals(disconnectPlayerName)) {

                        for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                            if (stringSocketEntry.getKey().equals(roomPlayer)) {

                                Socket socket = stringSocketEntry.getValue();
                                ObjectOutputStream otherClientOut = new ObjectOutputStream(socket.getOutputStream());

                                otherClientOut.writeObject(loseGameCommand);
                            }
                        }
                    }

                }

                break;
            }

        }

        return inGameRoom;


    }


}
