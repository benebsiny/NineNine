package Client.View;

import Client.Connection.RoomConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Shared.CardEnum.Card;
import Shared.RoomStatusCommand;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ChooseRoomController {

    @FXML
    public Label firstPicked;
    public Label secondPicked;
    public Label thirdPicked;
    public JFXButton enterRoomButton;

    Label[] pickedArray;
    String[] pickedCard;
    int nextPosition = 0;
    boolean allPicked = false;


    @FXML
    void initialize() {
        pickedArray = new Label[]{firstPicked, secondPicked, thirdPicked};
        pickedCard = new String[3];
        enterRoomButton.setDisable(true);
    }


    public void pickCard(ActionEvent actionEvent) {

        if (allPicked) return; // When all picked, do nothing

        JFXButton picked = (JFXButton) actionEvent.getSource();
        Image image = new Image(getClass().getResourceAsStream(String.format("../Img/Card/%s.png", picked.getId())));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(210);
        imageView.setFitWidth(150);


        pickedCard[nextPosition] = picked.getId();
        pickedArray[nextPosition].setGraphic(imageView); // Set graphic on the label

        findNextPosition();
    }

    /**
     * Find next position that picked card will locate at there.
     */
    void findNextPosition() {
        for (int i = 0; i < pickedArray.length; i++) {
            if (pickedArray[i].getGraphic() == null) {
                nextPosition = i;
                allPicked = false;
                enterRoomButton.setDisable(true);
                pickedCard[i] = null;
                return;
            }
        }
        enterRoomButton.setDisable(false);
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

        try {
            RoomStatusCommand command = RoomConn.chooseRoom(firstCard, secondCard, thirdCard);

            // Room found
            if (command.getRoomStatus() == RoomStatusCommand.RoomStatus.FOUND) {
                PlayerStatus.setPlayers(command.getPlayers()); // Set status
                Main.switchScene("WaitingUser"); // Go to waiting user

            } else { // Room not found
                // TODO Show error message
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {
        }
    }
}
