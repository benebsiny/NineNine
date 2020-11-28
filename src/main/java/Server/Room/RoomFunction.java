package Server.Room;

import Server.ClientMap.ClientMapFunction;
import Server.Main;
import Shared.CardEnum.Card;
import Shared.Command.Room.EnterRoomCommand;
import Shared.Command.Room.RoomStatusCommand;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

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
                        if(room.getPlayersName().length==4){
                            System.out.println("full");
                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FULL);
                        }
                        else {
                            System.out.println("found");                   //成功進房
                            room.addPlayer(ClientMapFunction.getClientUsername(client));
                            roomStatusCommand.setRoomStatus(RoomStatusCommand.RoomStatus.FOUND);
                            roomStatusCommand.setPlayers(room.getPlayersName());


                            Main.setRoomList(roomList);
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
