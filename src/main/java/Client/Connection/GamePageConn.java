package Client.Connection;

import Client.Main;

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
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

        return in.readObject();
    }
}