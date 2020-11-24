package Client.View;

import Client.Main;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.Socket;

public class Register {
    public JFXTextField usernameField;
    public JFXPasswordField passwordField;
    public JFXPasswordField passwordAgainField;

    @FXML
    void initialize() {
        RequiredFieldValidator rfv = new RequiredFieldValidator("請輸入欄位");
        usernameField.getValidators().add(rfv);
        passwordField.getValidators().add(rfv);
        passwordAgainField.getValidators().add(rfv);
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("SignIn");
    }
    public void confirm(ActionEvent actionEvent) {
        if (usernameField.validate() && passwordField.validate() && passwordAgainField.validate()) {

            RegexValidator rv = new RegexValidator("兩次輸入密碼不一致");
            rv.setRegexPattern("^" + passwordField.getText() + "$");

            passwordAgainField.getValidators().add(rv);

            if (passwordAgainField.validate()) {

                // Send register information to server
                Socket server = Main.getServer();
            } else {
                passwordAgainField.getValidators().remove(1);
            }


        }

//        Main.switchScene("Home");
    }
}
