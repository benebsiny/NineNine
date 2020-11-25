package Server;

import Server.Database.UserDB;
import Shared.*;
import Shared.CardEnum.Card;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<String, Socket>();
    private static List<Room> roomList = Collections.synchronizedList(new ArrayList<Room>());

    static class ExecuteClientThread implements Runnable {
        private Socket client;

        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        byte [] buf = new byte[1000];

        ExecuteClientThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());

                in = new ObjectInputStream(client.getInputStream());

                Object input = in.readObject();

                if(input instanceof RegisterCommand){
                    User user = ((RegisterCommand) input).getUser();
                    UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

                    if(userDB.signUp()){
                        out.writeObject(input);
                        clientMap.put(user.getUsername(), client);
                    }
                    else{
                        out.writeObject(null);
                    }

                }
                else if(input instanceof SignInCommand){

                    User user = ((SignInCommand) input).getUser();
                    UserDB userDB = new UserDB(user.getUsername(), user.getPassword());

                    System.out.println(user.getUsername());
                    System.out.println(user.getPassword());

                    if(userDB.login()){
                        out.writeObject(input);
                        clientMap.put(user.getUsername(), client);
                        System.out.println("aaa");
                    }
                    else{
                        out.writeObject(null);
                        System.out.println("bbb");
                    }
                }
                else if(input instanceof RoomCommand){
                    RoomCommand.RoomAction roomAction = ((RoomCommand) input).getAction();
                    Card[] chosenCards = ((RoomCommand) input).getChosenCards();
                    if(roomAction == RoomCommand.RoomAction.CREATE){

                        if(checkRoomPattern(chosenCards)){ //如果創房重複
                            out.writeObject("Room repeat");
                        }
                        else {
                            Room room = new Room(chosenCards);
                            room.setUsername(getClientUsername(client));
                            out.writeObject(room.getChosenCards());
                        }
                    }
                    else if(roomAction == RoomCommand.RoomAction.CHOOSE){
                        if(checkRoomPattern(chosenCards)){ //如果進入房間存在
                            for (Room room : roomList) {
                                if(room.getChosenCards().equals(chosenCards)){
                                    if(room.getChosenCards().length==4){
                                        out.writeObject("Room full");
                                    }
                                    else {
                                        room.setUsername(getClientUsername(client));
                                        out.writeObject(room.getChosenCards());
                                    }
                                    break;
                                }
                            }

                        }
                        else{
                            out.writeObject("Room not exist");
                        }
                    }


                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }



        }

        private boolean checkRoomPattern (Card[] chosenCards){
            for (Room room : roomList) {
                if(room.getChosenCards().equals(chosenCards)){
                    return true;
                }
            }
            return false;
        }
        private String getClientUsername (Socket client){
            Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();
            String userName = null;
            for (Map.Entry<String, Socket> socketEntry : entrySet) {
                if (socketEntry.getValue() == client) {
                    userName = socketEntry.getKey();
                }
            }
            return userName;
        }

    }
    public static void main(String[] args) {

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            ServerSocket server = new ServerSocket(8888);
            for(int i = 0; i < 5; i ++) {
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
