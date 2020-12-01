package Client.Connection;

import Client.Main;
import Shared.Command.Player.SignInCommand;
import Shared.Data.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SignInConn {

    /**
     * Send a User object to the server to sign in. If successful, return true, else false
     *
     * @param user User to sign in
     * @return Sign in successful or not
     */
    public static boolean signIn(User user) throws IOException, ClassNotFoundException {
        Socket server = Main.getServer();

        // Send command to server
        SignInCommand command = new SignInCommand(user);
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        out.writeObject(command); // Send sign in command to server

        // Get command from server
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        SignInCommand returnedCommand = (SignInCommand) in.readObject();
        return returnedCommand != null;
    }
}
