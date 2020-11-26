package Client.Connection;

import Client.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class WaitingUserConn {
    public static Object waiting() throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();

        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        return in.readObject();
    }
}
