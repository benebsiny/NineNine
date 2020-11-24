package Client.View;

import Client.Main;
import javafx.event.ActionEvent;

public class SignIn {

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }
    public void register(ActionEvent actionEvent) {
        Main.switchScene("register");
    }
}
