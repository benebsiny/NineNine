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

        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

        SignInCommand command = new SignInCommand(user);
        out.writeObject(command); // Send sign in command to server

        // Read object from server
        SignInCommand returnedCommand = (SignInCommand) in.readObject();
        return returnedCommand != null;
    }
}
