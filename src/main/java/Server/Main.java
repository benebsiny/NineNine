package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8888);
            Socket socket = server.accept();
            //TODO handle connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
