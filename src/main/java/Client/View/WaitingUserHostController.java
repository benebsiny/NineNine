package Client.View;

import Client.Connection.WaitingUserConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Client.Status.RoomCardStatus;
import Shared.CardEnum.Card;
import Shared.Command.Room.LeaveRoomCommand;
import Shared.Command.Room.RoomDisbandCommand;
import Shared.Command.Room.RoomPlayerCommand;
import Shared.Command.Room.StartGameCommand;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.SocketException;

public class WaitingUserHostController {
    public JFXListView<String> userList;
    public Label hostRoomLabel;
    public JFXButton startGameButton;
    public ImageView card1;
    public ImageView card2;
    public ImageView card3;

    @FXML
    void initialize() {

        Card[] roomCards = RoomCardStatus.getCards();
        card1.setImage(new Image("/Client/Img/" + roomCards[0] + ".png"));
        card2.setImage(new Image("/Client/Img/" + roomCards[1] + ".png"));
        card3.setImage(new Image("/Client/Img/" + roomCards[2] + ".png"));

        hostRoomLabel.setText(String.format("%s 的房間", PlayerStatus.getPlayers()[0]));
        userList.getItems().addAll(PlayerStatus.getPlayers());
        startGameButton.setDisable(true);

        // Keep connection at backend
        new Thread(new WaitingUserHostHandler(this)).start();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            WaitingUserConn.leave();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.switchScene("CreateRoom");
    }

    public void startGame(ActionEvent actionEvent) {
        new Thread(new StartGameHandler()).start();
    }
}


class StartGameHandler implements Runnable {

    @Override
    public void run() {
        try {
            WaitingUserConn.start();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Show connection error message
        }
    }
}

class WaitingUserHostHandler implements Runnable {

    WaitingUserHostController GUI;

    public WaitingUserHostHandler(WaitingUserHostController GUI) {
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

                    Platform.runLater(() -> {
                        GUI.userList.getItems().clear(); // Remove all user from the list
                        GUI.userList.getItems().addAll(PlayerStatus.getPlayers());

                        GUI.startGameButton.setDisable(players.length < 2);
                    });

                    PlayerStatus.setPlayers(players);
                }
                // Host leave the room -> others also leave the room
                else if (receiveObject instanceof RoomDisbandCommand) {
                    PlayerStatus.setPlayers(new String[0]);
                    Platform.runLater(() -> Main.switchScene("Home"));
                }
                // Game start
                else if (receiveObject instanceof StartGameCommand) {
                    Platform.runLater(() -> Main.switchScene("GamePage"));
                    break;
                }
                // Time to close this thread (leave room)
                else if (receiveObject instanceof LeaveRoomCommand) {
                    break;
                }

            } catch (SocketException ex) {
                System.out.println("Bye Bye!");
                break;
            } catch (IOException e) {
                // TODO Show connection error message
                e.printStackTrace();
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
}
