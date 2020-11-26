package Client.View;

import Client.Connection.SignInConn;
import Client.Main;
import Client.Status.UserStatus;
import Shared.Data.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class SignInController {

    public JFXTextField usernameField;
    public JFXPasswordField passwordField;
    public Label errMsg;

    @FXML
    void initialize() {
        RequiredFieldValidator rfv = new RequiredFieldValidator("欄位不得為空");
        usernameField.getValidators().add(rfv);
        passwordField.getValidators().add(rfv);
        errMsg.setVisible(false);
    }

    public void goBack(ActionEvent actionEvent) {
        Main.switchScene("Home");
    }
    public void goToRegisterPage(ActionEvent actionEvent) {
        Main.switchScene("register");
    }


    public void signIn(ActionEvent actionEvent) {
        if (usernameField.validate() && passwordField.validate()) {
            new Thread(new SignInHandler(this)).start();
        }
    }
}

class SignInHandler implements Runnable{

    SignInController GUI;
    public SignInHandler(SignInController GUI) {
        this.GUI = GUI;
    }

    @Override
    public void run() {
        try {
            boolean signInSuccess = SignInConn.signIn(new User(GUI.usernameField.getText(), GUI.passwordField.getText()));

            // Sign in success
            if (signInSuccess) {
                UserStatus.setSignInUser(GUI.usernameField.getText());
                Platform.runLater(()-> Main.switchScene("Home"));
            }
            // Sign in fail
            else {
                Platform.runLater(()-> GUI.errMsg.setVisible(true));
            }

        } catch (IOException e) {
            // TODO Show connection error message
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {
        }
    }
}