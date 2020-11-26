package Client.View;

import Client.Connection.SignInConn;
import Client.Main;
import Client.Status.UserStatus;
import Shared.Data.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class SignInController {

    public JFXTextField usernameField;
    public JFXPasswordField passwordField;

    @FXML
    void initialize() {
        RequiredFieldValidator rfv = new RequiredFieldValidator("欄位不得為空");
        usernameField.getValidators().add(rfv);
        passwordField.getValidators().add(rfv);
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }
    public void goToRegisterPage(ActionEvent actionEvent) {
        Main.switchScene("register");
    }


    public void signIn(ActionEvent actionEvent) {
        if (usernameField.validate() && passwordField.validate()) {
            try {
                boolean signInSuccess = SignInConn.signIn(new User(usernameField.getText(), passwordField.getText()));

                // Sign in success
                if (signInSuccess) {
                    UserStatus.setSignInUser(usernameField.getText());
                    Main.switchScene("Home");
                } else { // Sign in fail
                    // TODO Show sign in failed message
                }

            } catch (IOException e) {
                // TODO Show connection error message
                e.printStackTrace();
            } catch (ClassNotFoundException ignored) {
            }
        }
    }
}
