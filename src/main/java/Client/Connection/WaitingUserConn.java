package Client.Connection;

import Client.Main;
import Client.Status.UserStatus;
import Shared.Command.Room.LeaveRoomCommand;
import Shared.Command.Room.StartGameCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WaitingUserConn {

    /**
     * Wait until the command comes.
     * There may be RoomPlayerCommand, RoomDisbandCommand, StartGameCommand
     *
     * @return Object
     */
    public static Object waiting() throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();

        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        return in.readObject();
    }

    /**
     * Host sends StartGameCommand to server to notify other players to start to play
     */
    public static void start() throws IOException {
        Socket server = Main.getServer();

        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(new StartGameCommand());
    }

    /**
     * When leave the room, send this command to server
     */
    public static void leave() throws IOException {
        Socket server = Main.getServer();

        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(new LeaveRoomCommand(UserStatus.getSignInUser()));
    }
}
