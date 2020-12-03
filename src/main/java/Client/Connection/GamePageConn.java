package Client.Connection;

import Client.Main;
import Shared.Command.Game.PlayCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GamePageConn {

    /**
     * Receive command from server
     *
     * @return It may return PlayCommand, NextPlayerCommand
     */
    public static Object receive() throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());

        return in.readObject();
    }

    /**
     * Send PlayCommand to the server
     */
    public static void send(PlayCommand command) throws IOException {
        Socket server = Main.getServer();
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(command);
    }
}
