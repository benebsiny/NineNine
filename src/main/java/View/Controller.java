package View;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controller {
    @FXML
    public Pane homePane;

    final static int slidingDuration = 700;

    public void slidePaneToLeft(ActionEvent actionEvent) {

        // Sliding animation
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(homePane.translateXProperty(), -900, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(slidingDuration), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }


    public void slidePaneToRight(ActionEvent actionEvent) {

        // Sliding animation
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(homePane.translateXProperty(), 0, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(slidingDuration), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}
