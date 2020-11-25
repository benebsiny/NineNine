package Client.View;

import Client.Connection.RegisterConn;
import Client.Main;
import Shared.RegisterCommand;
import Shared.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegisterController {
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
                try {
                    User registeringUser =  new User(usernameField.getText(), passwordAgainField.getText());
                    boolean registerSuccess = RegisterConn.register(registeringUser);

                    if (registerSuccess) {
                        // TODO Show register successful tooltip
                    } else {
                        // TODO Show register fail tooltip (the reason is usually name duplicate)
                    }


                } catch (IOException e) {
                    // TODO Show connection error message
                    e.printStackTrace();
                } catch (ClassNotFoundException ignored) {
                }

                Main.switchScene("Home");
            } else {
                passwordAgainField.getValidators().remove(1);
            }
        }
    }
}
