package Server.ClientMap;

import Server.Main;

import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class ClientMapFunction {

    public static String getClientUsername (Socket client){
        Map<String, Socket> clientMap = Main.getClientMap();

        Set<Map.Entry<String, Socket>> entrySet = clientMap.entrySet();
        String userName = null;
        for (Map.Entry<String, Socket> socketEntry : entrySet) {
            if (socketEntry.getValue() == client) {
                userName = socketEntry.getKey();
            }
        }

        return userName;
    }
}
