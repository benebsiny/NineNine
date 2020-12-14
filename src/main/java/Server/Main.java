package Server;

import Server.Database.UserDB;
import Server.Game.GameRoom;
import Server.Game.ManageGameRoomValue;
import Server.Room.Room;
import Shared.CardEnum.Card;
import Shared.Command.Game.NextPlayerCommand;
import Shared.Command.Game.PlayCommand;
import Shared.Command.Game.WinnerCommand;
import Shared.Command.Player.RegisterCommand;
import Shared.Command.Player.SignInCommand;
import Shared.Command.Room.*;
import Shared.Data.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Server.ClientMap.ClientMapFunction.getClientUsername;
import static Server.ExceptionFunction.EOFExceptionInGameRoom;
import static Server.Game.GameFunction.*;
import static Server.Game.ManageGameRoomValue.*;
import static Server.Room.RoomFunction.*;
import static Server.Room.RoomFunction.processLeaveRoomCommand;

public class Main {
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<Room> waitRoomList = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<GameRoom> gameRoomList = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<GameRoom> getGameRoomList() {
        return gameRoomList;
    }

    public static void setGameRoomList(CopyOnWriteArrayList<GameRoom> gameRoomList) {
        Main.gameRoomList = gameRoomList;
    }

    public static Map<String, Socket> getClientMap() {
        return clientMap;
    }

    public static void setClientMap(Map<String, Socket> clientMap) {
        Main.clientMap = clientMap;
    }

    public static CopyOnWriteArrayList<Room> getWaitRoomList() {
        return waitRoomList;
    }

    public static void setWaitRoomList(CopyOnWriteArrayList<Room> waitRoomList) {
        Main.waitRoomList = waitRoomList;
    }

    static class ExecuteClientThread implements Runnable {
        private final Socket client;

        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        //byte [] buf = new byte[1000];

        ExecuteClientThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    in = new ObjectInputStream(client.getInputStream());

                    Object input = in.readObject();

                    System.out.println(input.toString());

                    if (input instanceof RegisterCommand) {
                        out = new ObjectOutputStream(client.getOutputStream());
                        User user = ((RegisterCommand) input).getUser();
                        UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

                        if (userDB.signUp()) {
                            out.writeObject(input);
//                        clientMap.put(user.getUsername(), client);
                        } else {
                            out.writeObject(null);
                        }

                    } else if (input instanceof SignInCommand) {
                        out = new ObjectOutputStream(client.getOutputStream());
                        User user = ((SignInCommand) input).getUser();
                        UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

//                        System.out.println(user.getUsername());
//                        System.out.println(user.getPassword());

                        if (userDB.login()) {
                            out.writeObject(input);
                            clientMap.put(user.getUsername(), client);
                            System.out.println("login success");
                        } else {
                            out.writeObject(null);
                            System.out.println("login fail");
                        }
                    } else if (input instanceof EnterRoomCommand) {

                        out = new ObjectOutputStream(client.getOutputStream());
                        processEnterRoomCommand((EnterRoomCommand) input, client, out);
                    } else if (input instanceof LeaveRoomCommand) {

                        processLeaveRoomCommand(((LeaveRoomCommand) input).getPlayer());
                        System.out.println("leave player: " + ((LeaveRoomCommand) input).getPlayer());
                        out = new ObjectOutputStream(client.getOutputStream());
                        out.writeObject(input);
                    } else if (input instanceof StartGameCommand) {

                        processStartGameCommand((StartGameCommand) input, client);
                        initialDrawCard(client);
                        NextPlayerCommand nextPlayerCommand = new NextPlayerCommand(getClientUsername(client));
                        out = new ObjectOutputStream(client.getOutputStream());
                        out.writeObject(nextPlayerCommand);

                        for (GameRoom gameRoom : gameRoomList) {   //找到該玩家的gameRoom
                            if(Arrays.asList(gameRoom.getPlayersName()).contains(getClientUsername(client))){
                                gameRoom.setNowDrawCardPlayer(getClientUsername(client));
                            }
                        }

                        //sendNextPlayerCommand(client);
                    } else if (input instanceof PlayCommand) {

                        addReceiveCardCount((PlayCommand) input);
                        boolean isLose = manageGameRoomValue((PlayCommand) input);
                        //System.out.println("PlayCommand card value: "+ Pl);

                        //System.out.println("出牌結果: " + result);

                        if (isLose) {
                            String losePlayer = ((PlayCommand) input).getPlayer();
                            sendLoseGameCommand(client);
                            String judgeResult = judgeGameRoomWinner(losePlayer);

                            if(judgeResult == null){            //判斷房間是否僅剩一人

                                if(isAllCardReceive((PlayCommand) input)){
                                    System.out.println("is All Card Receive");
                                    deleteGameRoomPlayer(losePlayer);
                                    sendAllWinnerCommand(((PlayCommand) input).getPlayer());
                                }
                                else{
                                    sendNextPlayerCommand(client, (PlayCommand) input,true);
                                    deleteGameRoomPlayer(losePlayer);
                                }

                            }
                            else{                                //如果房間出現贏家
                                System.out.println("winner " + judgeResult);
                                sendWinnerCommand(judgeResult);
                                deleteGameRoom(judgeResult);
                            }

                        } else {
                            if(isAllCardReceive((PlayCommand) input)){
                                System.out.println("is All Card Receive");
                                sendAllWinnerCommand(((PlayCommand) input).getPlayer());
                            }
                            else{
                                processPlayCommand((PlayCommand) input, client);
                            }

                        }

                        //Thread.sleep(1500);

                    }

                    int j = 0;
                    for (Room w : waitRoomList) {
                        j++;
                        System.out.println("waitRoom " + j + ".");
                        for (int i = 0; i < w.getPlayersName().length; i++) {
                            System.out.println(i + ". " + w.getPlayersName()[i]);
                        }

                    }

                    //else if(input instanceof PlayCommand)


                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                    if (e instanceof SocketException) {

                        System.out.println("Client disconnect!!");
                        break;
                    }
                    if (e instanceof EOFException) {
                        String disconnectClientName = getClientUsername(client);
                        System.out.println("Client: " + disconnectClientName + " disconnect");

                        try {
                            processLeaveRoomCommand(disconnectClientName);
                            boolean inGameRoomResult = EOFExceptionInGameRoom(disconnectClientName);

                            if(inGameRoomResult){
                                String judgeResult = judgeGameRoomWinner(disconnectClientName);
                                if(judgeResult == null){
                                    System.out.println("judgeResult: null");

                                    for (GameRoom gameRoom : gameRoomList) {   //找到該玩家的gameRoom
                                        if(Arrays.asList(gameRoom.getPlayersName()).contains(disconnectClientName)){
                                            if(gameRoom.getNowDrawCardPlayer() == disconnectClientName){
                                                PlayCommand playCommand = new PlayCommand();
                                                playCommand.setCard(Card.HA);
                                                sendNextPlayerCommand(client, playCommand,true);
                                            }
                                        }
                                    }

                                    deleteGameRoomPlayer(disconnectClientName);
                                }
                                else{                                                //如果房間出現贏家
                                    System.out.println("judgeResult: " + judgeResult);
                                    System.out.println("winner");
                                    sendWinnerCommand(judgeResult);
                                    deleteGameRoom(judgeResult);
                                }
                            }

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        break;
                    }
                }
            }

        }

    }

    public static void main(String[] args) {

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            ServerSocket server = new ServerSocket(8888);
            for (int i = 0; i < 100; i++) {
                Socket socket = server.accept();
                executorService.execute(new ExecuteClientThread(socket));
            }
            executorService.shutdown();
            server.close();
            //TODO handle connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
