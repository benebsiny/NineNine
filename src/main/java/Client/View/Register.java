package Client.View;

import Client.Main;
import javafx.event.ActionEvent;

public class Register {
    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("SignIn");
    }
    public void comfirm(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }

}
