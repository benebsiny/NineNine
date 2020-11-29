package Server.Room;

import Server.ClientMap.ClientMapFunction;
import Server.Main;
import Shared.CardEnum.Card;
import Shared.Command.Room.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import java.util.concurrent.CopyOnWriteArrayList;


public class RoomFunction {
    public static void processEnterRoomCommand(EnterRoomCommand input,Socket client) throws IOException {
        EnterRoomCommand.RoomAction roomAction = input.getAction();
        Card[] chosenCards = input.getChosenCards();
        RoomStatusCommand roomStatusCommand = new RoomStatusCommand();

        CopyOnWriteArrayList<Room> roomList = Main.getRoomList();

        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

        System.out.println(roomAction);

        if(roomAction == EnterRoomCommand.RoomAction.CREATE){
            System.out.println("create");
            if(checkRoomPattern(chosenCards)){          //如果創房重複
                System.out.println("repeat");
                roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.REPEATED);
            }
            else {                                      //成功創房
                System.out.println("create");
                Room newRoom = new Room(chosenCards);
                newRoom.addPlayer(ClientMapFunction.getClientUsername(client));
                roomList.add(newRoom);

                Main.setRoomList(roomList);

                roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.CREATED);
                roomStatusCommand.setPlayers(newRoom.getPlayersName());
            }
            out.writeObject(roomStatusCommand);
        }
        else if(roomAction == EnterRoomCommand.RoomAction.CHOOSE){
            System.out.println("choose");
            if(checkRoomPattern(chosenCards)){

                for (Room room : roomList) {
                    if(Arrays.equals(room.getChosenCards(), chosenCards)){  //房間額滿
                        if(room.getPlayersName().length>=4){
                            System.out.println("full");
                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FULL);
                        }
                        else {
                            System.out.println("found");                   //成功進房
                            room.addPlayer(ClientMapFunction.getClientUsername(client));
                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FOUND);
                            roomStatusCommand.setPlayers(room.getPlayersName());

                            Main.setRoomList(roomList);
                            sendRoomPlayerCommand(room,ClientMapFunction.getClientUsername(client));
                        }
                        out.writeObject(roomStatusCommand);

                        break;
                    }
                }

            }
            else{
                System.out.println("not found");        //房間不存在
                roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.NOT_FOUND);
                out.writeObject(roomStatusCommand);
            }
        }


    }

    public static void processLeaveRoomCommand(LeaveRoomCommand input) throws IOException {
        String leavePlayerName = input.getPlayer();

        Map<String, Socket> clientMap = Main.getClientMap();
        CopyOnWriteArrayList<Room> roomList = Main.getRoomList();

        for (Room room : roomList) {
            if(Arrays.binarySearch(room.getPlayersName(),leavePlayerName)==0){ //房間主人離開
                String[] roomPlayers = room.getPlayersName();

                Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();

                for (int i = 1; i < roomPlayers.length;i++){       //找該房間其他人的socket,送roomDisbandCommand

                    for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                        if(stringSocketEntry.getKey().equals(roomPlayers[i])){

                            Socket socket = stringSocketEntry.getValue();
                            ObjectOutputStream otherClientOut = new ObjectOutputStream(socket.getOutputStream());
                            RoomDisbandCommand roomDisbandCommand = new RoomDisbandCommand();
                            otherClientOut.writeObject(roomDisbandCommand);
                        }
                    }
                }

                roomList.remove(room);
                Main.setRoomList(roomList);
                break;
            }
            else if(Arrays.binarySearch(room.getPlayersName(),leavePlayerName)>0){ //非房間主人離開

                List<String> list = new ArrayList(Arrays.asList(room.getPlayersName()));
                list.remove(leavePlayerName);
                room.setPlayersName((String[])list.toArray());

                Main.setRoomList(roomList);

                sendRoomPlayerCommand(room,leavePlayerName);
                break;
            }
        }
    }


    public static void sendRoomPlayerCommand(Room room,String player) throws IOException {
        Map<String, Socket> clientMap = Main.getClientMap();

        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();
        String[] roomPlayers = room.getPlayersName();

        for (String roomPlayer : roomPlayers) {       //找該房間其他人的socket,送roomPlayerCommand
            if (!roomPlayer.equals(player)) {
                for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                    if (stringSocketEntry.getKey().equals(roomPlayer)) {

                        Socket socket = stringSocketEntry.getValue();
                        ObjectOutputStream otherClientOut = new ObjectOutputStream(socket.getOutputStream());
                        RoomPlayerCommand roomPlayerCommand = new RoomPlayerCommand(roomPlayers);
                        otherClientOut.writeObject(roomPlayerCommand);
                    }
                }
            }
        }
    }


    public static boolean checkRoomPattern (Card[] chosenCards){
        List<Room> roomList = Main.getRoomList();
        for (Room room : roomList) {
            if(Arrays.equals(room.getChosenCards(), chosenCards)){
                return true;
            }
        }
        return false;
    }


}
