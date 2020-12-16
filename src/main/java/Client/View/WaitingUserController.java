package Client.View;

import Client.Connection.WaitingUserConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Client.Status.RoomCardStatus;
import Client.Status.WindowStatus;
import Shared.CardEnum.Card;
import Shared.Command.Room.LeaveRoomCommand;
import Shared.Command.Room.RoomDisbandCommand;
import Shared.Command.Room.RoomPlayerCommand;
import Shared.Command.Room.StartGameCommand;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.SocketException;

public class WaitingUserController {

    public JFXListView<String> userList;
    public Label hostRoomLabel;
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

        // Keep connection at backend
        new Thread(new WaitingUserHandler(this)).start();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            WaitingUserConn.leave();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                    Platform.runLater(() -> {
                        GUI.userList.getItems().clear(); // Remove all user from the list
                        GUI.userList.getItems().addAll(players); // Add new users to the list
                    });

                    PlayerStatus.setPlayers(players);
                }
                // Host leave the room -> client leave the room
                else if (receiveObject instanceof RoomDisbandCommand) {
                    PlayerStatus.setPlayers(new String[0]);
                    Platform.runLater(() -> Main.switchScene("Home", "房主離開房間", WindowStatus.MessageSeverity.ERROR));
                    break;
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