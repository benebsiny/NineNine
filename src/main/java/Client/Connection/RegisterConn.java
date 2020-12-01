package Client.Connection;

import Client.Main;
import Shared.Command.Player.RegisterCommand;
import Shared.Data.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegisterConn {

    /**
     * Send a User object to the server to register. If successful, return true, else false
     *
     * @param user User to register
     * @return Register successful or not
     */
    public static boolean register(User user) throws IOException, ClassNotFoundException {

        Socket server = Main.getServer();

        // Send command to server
        RegisterCommand command = new RegisterCommand(user);
        ObjectOutput out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(command); // Send command to the server

        // Get command from server
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        RegisterCommand returnedCommand = (RegisterCommand) in.readObject();
        return returnedCommand != null;
    }
}
