package Server;

import Server.Database.UserDB;
import Server.Room.Room;
import Server.Room.RoomFunction;
import Shared.CardEnum.Card;
import Shared.Command.Player.RegisterCommand;
import Shared.Command.Player.SignInCommand;
import Shared.Command.Room.EnterRoomCommand;
import Shared.Command.Room.LeaveRoomCommand;
import Shared.Command.Room.RoomDisbandCommand;
import Shared.Command.Room.RoomStatusCommand;
import Shared.Data.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();
    private static List<Room> roomList = Collections.synchronizedList(new ArrayList<>());

    public static Map<String, Socket> getClientMap() {
        return clientMap;
    }

    public static void setClientMap(Map<String, Socket> clientMap) {
        Main.clientMap = clientMap;
    }

    public static List<Room> getRoomList() {
        return roomList;
    }

    public static void setRoomList(List<Room> roomList) {
        Main.roomList = roomList;
    }

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
//                        clientMap.put(user.getUsername(), client);
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
                else if(input instanceof EnterRoomCommand){
                    EnterRoomCommand.RoomAction roomAction = ((EnterRoomCommand) input).getAction();
                    Card[] chosenCards = ((EnterRoomCommand) input).getChosenCards();
                    RoomStatusCommand roomStatusCommand = new RoomStatusCommand();

                    if(roomAction == EnterRoomCommand.RoomAction.CREATE){

                        if(RoomFunction.checkRoomPattern(chosenCards)){ //如果創房重複

                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.REPEATED);
                        }
                        else { //成功創房
                            Room newRoom = new Room(chosenCards);
                            newRoom.addPlayer(getClientUsername(client));
                            roomList.add(newRoom);

                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.CREATED);
                            roomStatusCommand.setPlayers(newRoom.getPlayersName());
                        }
                        out.writeObject(roomStatusCommand);
                    }
                    else if(roomAction == EnterRoomCommand.RoomAction.CHOOSE){
                        if(RoomFunction.checkRoomPattern(chosenCards)){ //如果進入房間存在
                            for (Room room : roomList) {
                                if(Arrays.equals(room.getChosenCards(), chosenCards)){
                                    if(room.getPlayersName().length==4){
                                        roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FULL);
                                    }
                                    else {

                                        room.addPlayer(getClientUsername(client));
                                        roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FOUND);
                                        roomStatusCommand.setPlayers(room.getPlayersName());

                                    }
                                    out.writeObject(roomStatusCommand);
                                    break;
                                }
                            }

                        }
                        else{
                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.NOT_FOUND);
                            out.writeObject(roomStatusCommand);
                        }
                    }
                }
                else if(input instanceof LeaveRoomCommand){
                    String leavePlayerName = ((LeaveRoomCommand) input).getPlayer();
                    for (Room room : roomList) {
                        if(Arrays.binarySearch(room.getPlayersName(),leavePlayerName)==0){ //房間主人離開

                            Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();
                            for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {

                            }

                            RoomDisbandCommand roomDisbandCommand = new RoomDisbandCommand();
                            roomList.remove(room);
                            out.writeObject(roomDisbandCommand);
                            break;
                        }
                        else if(Arrays.binarySearch(room.getPlayersName(),leavePlayerName)>0){


                        }
                    }
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

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
