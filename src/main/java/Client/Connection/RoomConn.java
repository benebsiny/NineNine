package Client.Connection;

import Client.Main;
import Shared.CardEnum.Card;
import Shared.RoomCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RoomConn {

    public static String[] chooseRoom(Card firstCard, Card secondCard, Card thirdCard) throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

        RoomCommand command = new RoomCommand(RoomCommand.RoomAction.CHOOSE, new Card[]{firstCard, secondCard, thirdCard});
        out.writeObject(command);

        String[] players = (String[]) in.readObject();
        return players;
    }
}
