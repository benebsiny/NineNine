package Client.View;

import Client.Connection.SignInConn;
import Client.Main;
import Client.Status.UserStatus;
import Client.Status.WindowStatus;
import Shared.Data.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SignInController {

    public JFXTextField usernameField;
    public JFXPasswordField passwordField;
    public Label errMsg;
    public ImageView loadingImg;
    public AnchorPane pane;

    @FXML
    void initialize() {

        Main.checkMessage(pane);

        RequiredFieldValidator rfv = new RequiredFieldValidator("欄位不得為空");
        usernameField.getValidators().add(rfv);
        passwordField.getValidators().add(rfv);
        errMsg.setVisible(false);
        loadingImg.setVisible(false);
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

class SignInHandler implements Runnable {

    SignInController GUI;

    public SignInHandler(SignInController GUI) {
        this.GUI = GUI;
    }

    @Override
    public void run() {
        try {
            Platform.runLater(() -> GUI.loadingImg.setVisible(true)); // Show loading image

            boolean signInSuccess = SignInConn.signIn(new User(GUI.usernameField.getText(), GUI.passwordField.getText()));

            Platform.runLater(() -> GUI.loadingImg.setVisible(false)); // Hide loading image

            // Sign in success
            if (signInSuccess) {
                String username = GUI.usernameField.getText();
                UserStatus.setSignInUser(username);
                Platform.runLater(() -> {
                    Main.setStageName(username);
                    Main.switchScene("Home", "登入成功", WindowStatus.MessageSeverity.SUCCESS);
                });
            }

            // Sign in fail
            else {
                Platform.runLater(() -> GUI.errMsg.setVisible(true));
            }

        } catch (IOException e) {
            // TODO Show connection error message
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {
        }
    }
}