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

    public static RoomStatusCommand chooseRoom(Card firstCard, Card secondCard, Card thirdCard) throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

        EnterRoomCommand command = new EnterRoomCommand(EnterRoomCommand.RoomAction.CHOOSE, new Card[]{firstCard, secondCard, thirdCard});
        out.writeObject(command);

        return (RoomStatusCommand) in.readObject();
    }
}
