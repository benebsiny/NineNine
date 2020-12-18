package Client.View;

import Client.Main;
import Client.Status.UserStatus;
import Client.Status.WindowStatus;
import Shared.CardEnum.Card;
import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Random;

public class HomeController {
    @FXML
    public Pane homePane;
    public Text signInUserLabel;
    public AnchorPane pane;
    public Label pokerGameLabel;

    final static int slidingDuration = 700;
    public ImageView cardImage1;
    public ImageView cardImage2;
    public JFXButton loginButton;

    @FXML
    void initialize() {

        // If login, disable the login button
        if (UserStatus.getSignInUser() != null) {
            loginButton.setDisable(true);
        }

        // Random character show up
        String title = "99 POKER GAME";
        Random random = new Random();
        Timeline wordTimeline = new Timeline();

        for (int i = 0; i < title.length(); i++) {
            if (title.charAt(i) != ' ') {
                for (int j = 0; j < 5; j++) {
                    char randomChar = (char) ('A' + random.nextInt(26));
                    wordTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 250 + j * 50),
                            new KeyValue(pokerGameLabel.textProperty(), title.substring(0, i) + randomChar)));
                }

                wordTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 250 + 250),
                        new KeyValue(pokerGameLabel.textProperty(), title.substring(0, i + 1))));
            } else {
                wordTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 250),
                        new KeyValue(pokerGameLabel.textProperty(), title.substring(0, i + 1))));
            }
        }
        wordTimeline.play();


        // Label color change
        Timeline colorTimeline = new Timeline();
        String[] colorList = new String[]{"red", "orangered", "yellow", "limegreen", "aqua", "purple", "red"};
        for (int i = 0; i < colorList.length; i++) {
            colorTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(i * 700),
                            new KeyValue(pokerGameLabel.effectProperty(),
                                    new DropShadow(20, Color.valueOf(colorList[i])))));
        }
        colorTimeline.setCycleCount(Animation.INDEFINITE);
        colorTimeline.play();


        // Card Fade transition
        FadeTransition cardFade = new FadeTransition(Duration.millis(10000), cardImage1);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);
        cardFade.play();
        FadeTransition cardFade1 = new FadeTransition(Duration.millis(10000), cardImage2);
        cardFade1.setFromValue(0);
        cardFade1.setToValue(1);
        cardFade1.play();


        // Card Image change
        Timeline cardTimeline = new Timeline();
        EventHandler<ActionEvent> cardRotation = __ -> {
            cardImage1.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage1.getTransforms().add(new Rotate(5));

            cardImage2.setImage(new Image("/Client/Img/Card/" + Card.randomLetter().toString() + ".png"));
            cardImage2.getTransforms().add(new Rotate(-8));
        };

        cardTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), cardRotation));
        cardTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), cardRotation));
        cardTimeline.setCycleCount(Animation.INDEFINITE);
        cardTimeline.play();


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
