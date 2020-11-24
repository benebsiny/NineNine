package Server;

import Shared.PlayCommand;
import Shared.Register;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static class ExecuteClientThread implements Runnable {
        private Socket client;

        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        byte [] buf = new byte[1000];

        ExecuteClientThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());

                in = new ObjectInputStream(client.getInputStream());

                Object temp = in.readObject();
                if(temp instanceof PlayCommand) {

                }
                else if(temp instanceof Register){

                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }



        }
    }
    public static void main(String[] args) {


        try {
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            ServerSocket server = new ServerSocket(8888);
            for(int i = 0; i < 5; i ++) {
                Socket socket = server.accept();
                executorService.execute(new ExecuteClientThread(socket));
            }
            executorService.shutdown();
            server.close();
            //TODO handle connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
