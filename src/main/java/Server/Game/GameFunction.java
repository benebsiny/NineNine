package Server.Game;

import Server.Main;
import Shared.CardEnum.Card;
import Shared.Command.Game.DrawCommand;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static Server.ClientMap.ClientMapFunction.getClientUsername;

public class GameFunction {
    public static void initialDrawCard(Socket client) throws IOException {

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        Map<String, Socket> clientMap = Main.getClientMap();

        for (GameRoom gameRoom : gameRoomList){   //找到該玩家的gameRoom
            if(Arrays.binarySearch(gameRoom.getPlayersName(), getClientUsername(client)) >= 0){

                Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();
                String[] roomPlayers = gameRoom.getPlayersName();

                int deskIndex = gameRoom.getDeskIndex();
                int nextDeskIndex = roomPlayers.length*5;

                for (String roomPlayer : roomPlayers) {    //為每個gameRoom玩家發出DrawCommand內含5張牌
                    for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                        if (stringSocketEntry.getKey().equals(roomPlayer)) {
                            Socket socket = stringSocketEntry.getValue();
                            ObjectOutputStream allClientOut = new ObjectOutputStream(socket.getOutputStream());

                            Card[] drawCard = new Card[5];
                            for (int j = 0;j < 5;j++){
                                drawCard[j] = gameRoom.getDesk()[deskIndex];
                                deskIndex++;
                            }

                            DrawCommand drawCommand = new DrawCommand(drawCard);
                            allClientOut.writeObject(drawCommand);
                        }
                    }
                }
                gameRoom.setDeskIndex(nextDeskIndex);
                Main.setGameRoomList(gameRoomList);

                break;
            }
        }
    }

    //public static void sendNextPlayerCommand()

    public static Card[] shuffle() {

        Card[] deck = Card.values();
        int length = deck.length;

        int changes = 100;
        for (int i = 0; i < changes; i++) {
            int randIndex1 = new Random().nextInt(length);
            int randIndex2 = new Random().nextInt(length);
            Card tmp = deck[randIndex1];
            deck[randIndex1] = deck[randIndex2];
            deck[randIndex2] = tmp;
        }
        return deck;
    }
}
