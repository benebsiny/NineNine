package Server;

import Server.Database.UserDB;
import Server.Game.GameRoom;
import Server.Room.Room;
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
import static Server.Game.GameFunction.initialDrawCard;
import static Server.Room.RoomFunction.*;

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
                    out = new ObjectOutputStream(client.getOutputStream());

                    Object input = in.readObject();

                    if (input instanceof RegisterCommand) {
                        User user = ((RegisterCommand) input).getUser();
                        UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

                        if (userDB.signUp()) {
                            out.writeObject(input);
//                        clientMap.put(user.getUsername(), client);
                        } else {
                            out.writeObject(null);
                        }

                    } else if (input instanceof SignInCommand) {

                        User user = ((SignInCommand) input).getUser();
                        UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

//                        System.out.println(user.getUsername());
//                        System.out.println(user.getPassword());

                        if (userDB.login()) {
                            out.writeObject(input);
                            clientMap.put(user.getUsername(), client);
                            System.out.println("aaa");
                        } else {
                            out.writeObject(null);
                            System.out.println("bbb");
                        }
                    } else if (input instanceof EnterRoomCommand) {
                        processEnterRoomCommand((EnterRoomCommand) input, client, out);
                    } else if (input instanceof LeaveRoomCommand) {
                        processLeaveRoomCommand((LeaveRoomCommand) input);
                    } else if (input instanceof StartGameCommand) {
                        processStartGameCommand((StartGameCommand) input, client);
                        initialDrawCard(client);
                    }
                    //else if(input instanceof PlayCommand)


                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    if(e instanceof SocketException){
                        System.out.println("Client disconnect!!");
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
            for(int i = 0; i < 100; i ++) {
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
