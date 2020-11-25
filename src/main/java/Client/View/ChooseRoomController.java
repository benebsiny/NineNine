package Client.View;

import Client.Connection.RoomConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Shared.CardEnum.Card;
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
    int nextPosition = 0;
    boolean allPicked = false;


    @FXML
    void initialize() {
        pickedArray = new Label[]{firstPicked, secondPicked, thirdPicked};
        enterRoomButton.setDisable(true);
    }


    public void pickCard(ActionEvent actionEvent) {

        if (allPicked) return; // When all picked, do nothing

        JFXButton picked = (JFXButton) actionEvent.getSource();
        Image image = new Image(getClass().getResourceAsStream(String.format("../Img/Card/%s.png", picked.getId())));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(210);
        imageView.setFitWidth(150);

        pickedArray[nextPosition].setGraphic(imageView); // Set graphic on the label
//        labels[nextPosition].getStyleClass().add("pointer");

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
//        labels[nextPosition].getStyleClass().remove("pointer");
        findNextPosition();
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }

    public void enterRoom(ActionEvent actionEvent) {

        Card firstCard = Card.valueOf(firstPicked.getId());
        Card secondCard = Card.valueOf(secondPicked.getId());
        Card thirdCard = Card.valueOf(thirdPicked.getId());

        try {
            String[] players = RoomConn.chooseRoom(firstCard, secondCard, thirdCard);

            // Room found
            if (players != null) {
                PlayerStatus.setPlayers(players); // Set status
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
