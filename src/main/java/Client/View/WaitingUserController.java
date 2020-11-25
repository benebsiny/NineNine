package Client.View;

import Client.Main;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WaitingUserController {

    public JFXListView<String> userList;

    @FXML
    void initialize() {
//        userList.getItems().add("Benny");
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("ChooseRoom");
    }
}
