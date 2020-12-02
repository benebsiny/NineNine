package Client.View;

import Client.Main;
import Client.Status.UserStatus;
import Client.Status.WindowStatus;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class HomeController {
    @FXML
    public Pane homePane;

    final static int slidingDuration = 700;
    public Text signInUserLabel;
    public AnchorPane pane;

    @FXML
    void initialize() {

        Main.checkMessage(pane);

        if (UserStatus.getSignInUser() == null) {
            signInUserLabel.setText("未登入");
        } else {
            signInUserLabel.setText(UserStatus.getSignInUser());
        }
    }

    public void slidePaneToLeft(ActionEvent actionEvent) {

        if (UserStatus.getSignInUser() != null) {
            // Sliding animation
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(homePane.translateXProperty(), -900, Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(slidingDuration), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        } else {
            // Go to sign in page if you're not signed in
            Main.switchScene("SignIn", "請先登入，才能進行遊玩", WindowStatus.MessageSeverity.ERROR);
        }

    }


    public void slidePaneToRight(ActionEvent actionEvent) {

        // Sliding animation
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(homePane.translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(slidingDuration), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public void goToCreateRoom(ActionEvent actionEvent) {
        Main.switchScene("CreateRoom");
    }

    public void goToChooseRoom(ActionEvent actionEvent) {
        Main.switchScene("ChooseRoom");
    }

    public void goToLoginPage(ActionEvent actionEvent) {
        Main.switchScene("SignIn");
    }
}
