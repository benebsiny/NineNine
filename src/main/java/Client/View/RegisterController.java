package Client.View;

import Client.Connection.RegisterConn;
import Client.Main;
import Client.Status.WindowStatus;
import Shared.Data.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class RegisterController {
    public JFXTextField usernameField;
    public JFXPasswordField passwordField;
    public JFXPasswordField passwordAgainField;
    public Label errMsg;
    public ImageView loadingImg;

    @FXML
    void initialize() {
        RequiredFieldValidator rfv = new RequiredFieldValidator("請輸入欄位");
        usernameField.getValidators().add(rfv);
        passwordField.getValidators().add(rfv);
        passwordAgainField.getValidators().add(rfv);

        loadingImg.setVisible(false);
        errMsg.setVisible(false);
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
                new Thread(new RegisterHandler(this)).start();

            } else {
                passwordAgainField.getValidators().remove(1);
            }
        }
    }
}

class RegisterHandler implements Runnable {

    RegisterController GUI;

    RegisterHandler(RegisterController GUI) {
        this.GUI = GUI;
    }

    @Override
    public void run() {
        // Send register information to server
        try {
            User registeringUser = new User(GUI.usernameField.getText(), GUI.passwordAgainField.getText());

            Platform.runLater(() -> GUI.loadingImg.setVisible(true));
            boolean registerSuccess = RegisterConn.register(registeringUser);
            Platform.runLater(() -> GUI.loadingImg.setVisible(false));

            if (registerSuccess) {
                // TODO Show register successful tooltip
                Platform.runLater(() -> Main.switchScene("Home", "註冊成功", WindowStatus.MessageSeverity.SUCCESS));

            } else {
                Platform.runLater(() -> GUI.errMsg.setVisible(true));
            }


        } catch (IOException e) {
            // TODO Show connection error message
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {
        }
    }
}