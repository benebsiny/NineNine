package Client.Connection;

import Client.Main;
import Shared.CardEnum.Card;
import Shared.Command.Room.EnterRoomCommand;
import Shared.Command.Room.RoomStatusCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RoomConn {

    public static RoomStatusCommand chooseRoom(Card firstCard, Card secondCard, Card thirdCard, EnterRoomCommand.RoomAction action) throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();
        EnterRoomCommand command = new EnterRoomCommand(action, new Card[]{firstCard, secondCard, thirdCard});

        // Send command to server
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(command);

        // Get command from server
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        return (RoomStatusCommand) in.readObject();
    }
}
