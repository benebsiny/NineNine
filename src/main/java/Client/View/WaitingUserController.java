package Client.View;

import Client.Connection.WaitingUserConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Shared.Command.Room.RoomPlayerCommand;
import Shared.Command.Room.StartGameCommand;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class WaitingUserController {

    public JFXListView<String> userList;

    @FXML
    void initialize() {
        // Keep connection at backend
        new Thread(new WaitingUserHandler(this)).start();
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("ChooseRoom");
    }

}

class WaitingUserHandler implements Runnable {

    WaitingUserController GUI;

    public WaitingUserHandler(WaitingUserController GUI) {
        this.GUI = GUI;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object receiveObject = WaitingUserConn.waiting();

                // Play comes in or leave
                if (receiveObject instanceof RoomPlayerCommand) {
                    String[] players = ((RoomPlayerCommand) receiveObject).getPlayers();
                    for (String player : players) {
                        GUI.userList.getItems().add(player);
                    }
                    PlayerStatus.setPlayers(players);
                }
                // Game start
                else if (receiveObject instanceof StartGameCommand) {
                    Main.switchScene("GamePage");
                }

            } catch (IOException e) {
                // TODO Show connection error message
                e.printStackTrace();
            } catch (ClassNotFoundException ignored) {
            }
        }

    }
}