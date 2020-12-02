package Client.View;

import Client.Connection.RoomConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Shared.CardEnum.Card;
import Shared.Command.Room.EnterRoomCommand;
import Shared.Command.Room.RoomStatusCommand;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class CreateRoomController {

    public Label firstPicked;
    public Label secondPicked;
    public Label thirdPicked;
    public JFXButton createRoomButton;
    public ImageView loadingImg;
    public Label errMsg;

    Label[] labels;
    String[] pickedCard;
    int nextPosition = 0;
    boolean allPicked = false;


    @FXML
    void initialize() {

        loadingImg.setVisible(false);
        errMsg.setVisible(false);

        labels = new Label[]{firstPicked, secondPicked, thirdPicked};
        pickedCard = new String[3];
        createRoomButton.setDisable(true);
    }

    public void pickCard(ActionEvent actionEvent) {

        if (allPicked) return; // When all picked, do nothing

        JFXButton picked = (JFXButton) actionEvent.getSource();
        Image image = new Image(getClass().getResourceAsStream(String.format("../Img/Card/%s.png", picked.getId())));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(210);
        imageView.setFitWidth(150);

        pickedCard[nextPosition] = picked.getId();
        labels[nextPosition].setGraphic(imageView); // Set graphic on the label

        findNextPosition();
    }

    /**
     * Find next position that picked card will locate at there.
     */
    void findNextPosition() {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].getGraphic() == null) {
                nextPosition = i;
                allPicked = false;
                createRoomButton.setDisable(true);
                pickedCard[i] = null;
                return;
            }
        }
        createRoomButton.setDisable(false);
        allPicked = true;
    }

    /**
     * Remove picked card by clicking on them
     *
     * @param mouseEvent - Mouse Event
     */
    public void removeCard(MouseEvent mouseEvent) {
        Label label = (Label) mouseEvent.getSource(); // Get the clicked label
        label.setGraphic(null); // Remove image
        findNextPosition();
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }

    public void enterRoom(ActionEvent actionEvent) {

        Card firstCard = Card.valueOf(pickedCard[0]);
        Card secondCard = Card.valueOf(pickedCard[1]);
        Card thirdCard = Card.valueOf(pickedCard[2]);

        new Thread(new CreateRoomHandler(this, firstCard, secondCard, thirdCard)).start();
    }
}

class CreateRoomHandler implements Runnable {

    CreateRoomController GUI;
    Card firstCard, secondCard, thirdCard;

    CreateRoomHandler(CreateRoomController GUI, Card firstCard, Card secondCard, Card thirdCard) {
        this.GUI = GUI;
        this.firstCard = firstCard;
        this.secondCard = secondCard;
        this.thirdCard = thirdCard;
    }

    @Override
    public void run() {
        try {
            Platform.runLater(() -> GUI.loadingImg.setVisible(true));
            RoomStatusCommand command = RoomConn.chooseRoom(firstCard, secondCard, thirdCard, EnterRoomCommand.RoomAction.CREATE);
            Platform.runLater(() -> GUI.loadingImg.setVisible(false));

            // Room created
            if (command.getRoomStatus() == RoomStatusCommand.RoomStatus.CREATED) {
                PlayerStatus.setPlayers(command.getPlayers()); // Set status
                Platform.runLater(() -> Main.switchScene("WaitingUserHost")); // Go to waiting user

            }
            // Room already exists
            else if (command.getRoomStatus() == RoomStatusCommand.RoomStatus.REPEATED) {
                Platform.runLater(() -> GUI.errMsg.setVisible(true));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {
        }
    }
}