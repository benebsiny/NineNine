package Server.Game;

import Server.Main;
import Shared.CardEnum.Card;
import Shared.Command.Game.*;

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

    public static void processPlayCommand(PlayCommand playCommand, Socket client) throws IOException, InterruptedException {

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        Map<String, Socket> clientMap = Main.getClientMap();

        String playPlayer = playCommand.getPlayer();   //出牌玩家

        for (GameRoom gameRoom : gameRoomList){          //確定client的遊戲房間
            if(Arrays.asList(gameRoom.getPlayersName()).contains(playPlayer)){

                ReturnPlayCommand returnPlayCommand = new ReturnPlayCommand();
                returnPlayCommand.setPlayer(playPlayer);

                int deskIndex = gameRoom.getDeskIndex();

                if(deskIndex < 52){                       //如果牌堆還有牌 發出drawcommand給出牌client
                    Card[] drawCard = new Card[1];
                    drawCard[0] = gameRoom.getDesk()[deskIndex];

                    DrawCommand drawCommand = new DrawCommand(drawCard);


                    if(deskIndex == 51){
                        returnPlayCommand.setHasCardsInDesk(false);
                    }
                    else{
                        returnPlayCommand.setHasCardsInDesk(true);
                    }
                    returnPlayCommand.setRemainCardCount(playCommand.getRemainCardCount() + 1);
                    returnPlayCommand.setCard(playCommand.getCard());
                    returnPlayCommand.setValue(gameRoom.getValue());

                    sendReturnPlayCommand(clientMap,gameRoom.getPlayersName(),returnPlayCommand); //發送returnPlayCommand

                    Thread.sleep(2500);

                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    out.writeObject(drawCommand);         //發牌


                    deskIndex = deskIndex + 1;
                    gameRoom.setDeskIndex(deskIndex);
                }
                else{
                    returnPlayCommand.setHasCardsInDesk(false);
                    returnPlayCommand.setRemainCardCount(playCommand.getRemainCardCount());
                    returnPlayCommand.setCard(playCommand.getCard());
                    returnPlayCommand.setValue(gameRoom.getValue());

                    sendReturnPlayCommand(clientMap,gameRoom.getPlayersName(),returnPlayCommand);
                }

                sendNextPlayerCommand(client,playCommand,false);
                Main.setGameRoomList(gameRoomList);
                break;
            }
        }
    }

    public static void sendLoseGameCommand(Socket client) throws IOException {
        Map<String, Socket> clientMap = Main.getClientMap();
        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();

        String losePlayer = getClientUsername(client);

        LoseGameCommand loseGameCommand = new LoseGameCommand(losePlayer);

        for (GameRoom gameRoom : gameRoomList) {                    //找到該玩家的gameRoom
            if (Arrays.asList(gameRoom.getPlayersName()).contains(losePlayer)) {
                String[] roomPlayers = gameRoom.getPlayersName();

                for (String roomPlayer : roomPlayers) {             //發出loseGameCommand給房間所有玩家
                    for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                        if (stringSocketEntry.getKey().equals(roomPlayer)) {
                            Socket socket = stringSocketEntry.getValue();
                            ObjectOutputStream allClientOut = new ObjectOutputStream(socket.getOutputStream());

                            allClientOut.writeObject(loseGameCommand);
                        }
                    }
                }

                break;
            }
        }

    }

    public static void sendReturnPlayCommand(Map<String, Socket> clientMap,String[] roomPlayers,ReturnPlayCommand returnPlayCommand) throws IOException {
        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

        for (String roomPlayer : roomPlayers) {             //發出returnPlayCommand給房間所有玩家
            for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                if (stringSocketEntry.getKey().equals(roomPlayer)) {
                    Socket socket = stringSocketEntry.getValue();
                    ObjectOutputStream allClientOut = new ObjectOutputStream(socket.getOutputStream());

                    allClientOut.writeObject(returnPlayCommand);
                }
            }
        }
    }

    public static void initialDrawCard(Socket client) throws IOException {

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        Map<String, Socket> clientMap = Main.getClientMap();

        for (GameRoom gameRoom : gameRoomList){   //找到該玩家的gameRoom
            if(Arrays.asList(gameRoom.getPlayersName()).contains(getClientUsername(client))){

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

    public static void sendNextPlayerCommand(Socket client,PlayCommand playCommand,boolean isPlayerLose) throws IOException {

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        Map<String, Socket> clientMap = Main.getClientMap();

        String nowPlayer = getClientUsername(client);
        String nextPlayer = null;


        for (GameRoom gameRoom : gameRoomList){   //找到該玩家的gameRoom
            if(Arrays.asList(gameRoom.getPlayersName()).contains(nowPlayer)){
                String[] playersName = gameRoom.getPlayersName();

                if(playCommand.getCard().getRank() == 5){
                    nextPlayer = playCommand.getAssignPlayer();
                }
                else {

                    for (int i = 0; i < playersName.length; i++) {  //找出下個玩家
                        if (playersName[i] == nowPlayer) {
                            if (gameRoom.getOrder() == GameRoom.Order.Clockwise) {   //如果為順序
                                if (i == playersName.length - 1) {
                                    nextPlayer = playersName[0];
                                } else {
                                    nextPlayer = playersName[i + 1];
                                }
                            } else if (gameRoom.getOrder() == GameRoom.Order.Counterclockwise) {  //如果為逆序
                                if (i == 0) {
                                    nextPlayer = playersName[playersName.length - 1];
                                } else {
                                    nextPlayer = playersName[i - 1];
                                }
                            }

                            gameRoom.setNowDrawCardPlayer(nextPlayer);
                            Main.setGameRoomList(gameRoomList);
                            break;
                        }
                    }
                }
                String[] roomPlayers = gameRoom.getPlayersName();
                Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

                for (String roomPlayer : roomPlayers) {                     //發送所有人NextPlayerCommand
                    if((isPlayerLose == false && roomPlayer==nowPlayer) || roomPlayer!=nowPlayer) {
                        for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                            if (stringSocketEntry.getKey().equals(roomPlayer)) {
                                System.out.println(roomPlayer);
                                Socket socket = stringSocketEntry.getValue();
                                ObjectOutputStream allClientOut = new ObjectOutputStream(socket.getOutputStream());

                                NextPlayerCommand nextPlayerCommand = new NextPlayerCommand(nextPlayer);
                                allClientOut.writeObject(nextPlayerCommand);
                            }
                        }
                    }
                }
                break;
            }
        }
    }



    public static void sendWinnerCommand(String winner) throws IOException {
        Map<String, Socket> clientMap = Main.getClientMap();
        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

        for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
            if (stringSocketEntry.getKey().equals(winner)) {
                Socket socket = stringSocketEntry.getValue();
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                WinnerCommand winnerCommand = new WinnerCommand();
                out.writeObject(winnerCommand);

                break;
            }
        }

    }

    public static void sendAllWinnerCommand(String winner) throws IOException {
        Map<String, Socket> clientMap = Main.getClientMap();
        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        WinnerCommand winnerCommand =  new WinnerCommand();

        for (GameRoom gameRoom : gameRoomList) {                    //找到該玩家的gameRoom
            if (Arrays.asList(gameRoom.getPlayersName()).contains(winner)) {
                String[] roomPlayers = gameRoom.getPlayersName();

                for (String roomPlayer : roomPlayers) {             //發出WinnerCommand給房間所有玩家
                    for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                        if (stringSocketEntry.getKey().equals(roomPlayer)) {
                            Socket socket = stringSocketEntry.getValue();
                            ObjectOutputStream allClientOut = new ObjectOutputStream(socket.getOutputStream());

                            allClientOut.writeObject(winnerCommand);
                        }
                    }
                }

                break;
            }
        }

    }

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
