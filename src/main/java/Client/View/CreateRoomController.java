package Client.View;

import Client.Main;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CreateRoomController {

    public Label firstPicked;
    public Label secondPicked;
    public Label thirdPicked;
    public JFXButton createRoomButton;

    Label[] labels;
    int nextPosition = 0;
    boolean allPicked = false;


    @FXML
    void initialize() {
        labels = new Label[]{firstPicked, secondPicked, thirdPicked};
        createRoomButton.setDisable(true);
    }

    public void pickCard(ActionEvent actionEvent) {

        if (allPicked) return; // When all picked, do nothing

        JFXButton picked = (JFXButton) actionEvent.getSource();
        Image image = new Image(getClass().getResourceAsStream(String.format("../Img/Card/%s.png", picked.getId())));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(210);
        imageView.setFitWidth(150);

        labels[nextPosition].setGraphic(imageView); // Set graphic on the label
//        labels[nextPosition].getStyleClass().add("pointer");

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
//        labels[nextPosition].getStyleClass().remove("pointer");
        findNextPosition();
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }

    public void enterRoom(ActionEvent actionEvent) {
        Main.switchScene("WaitingUser");
    }
}
