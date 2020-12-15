package Client.View;

import Client.Main;
import Client.Status.UserStatus;
import Client.Status.WindowStatus;
import Shared.CardEnum.Card;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class HomeController {
    @FXML
    public Pane homePane;
    public Text signInUserLabel;
    public AnchorPane pane;
    public Label pokerGameLabel;

    final static int slidingDuration = 700;
    public ImageView cardImage1;
    public ImageView cardImage2;

    @FXML
    void initialize() {


        // Label color change
        Timeline timeline = new Timeline();
        String[] colorList = new String[]{"red", "orangered", "yellow", "limegreen", "aqua", "purple", "red"};
        for (int i=0; i<colorList.length; i++) {
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i*700),
                            new KeyValue(pokerGameLabel.effectProperty(),
                                    new DropShadow(20, Color.valueOf(colorList[i])))));
        }
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Card Image change
        Timeline timeline1 = new Timeline();
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(0), __ -> {
            cardImage1.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage1.getTransforms().add(new Rotate(5));

            cardImage2.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage2.getTransforms().add(new Rotate(-5));

        }));
        timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(100), __ -> {
            cardImage1.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage1.getTransforms().add(new Rotate(5));

            cardImage2.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage2.getTransforms().add(new Rotate(-5));
        }));
        timeline1.setCycleCount(Animation.INDEFINITE);
        timeline1.play();


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
