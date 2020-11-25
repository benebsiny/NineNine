package Client.View;

import Client.Connection.GamePageConn;
import Client.Status.PlayerStatus;
import Shared.CardEnum.Card;
import Shared.NextPlayerCommand;
import Shared.PlayCommand;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;

public class GamePageController {

    public ImageView playCardImage;
    public Circle shineCircle;
    public Rectangle countdownBar;
    public Label valueLabel;

    volatile int value = 0;

    FillTransition ft;

    @FXML
    void initialize() {

        countdownBar.setVisible(false);
        shineCircle.setVisible(false);
        playCardImage.setVisible(false);

//        playCard(1, Card.D2, 20);

        Thread connection = new Thread(new GamePageConnection(this));
        connection.start();
    }


    /**
     * Play the animation that play the card
     *
     * @param turnId - Which player to play the card.
     * @param card   - The card the player played
     * @param nextValue  - The next value of the sea
     */
    public void playCard(int turnId, Card card, int nextValue) {
        Line line = new Line();

        if (turnId == 0) { // Left player
            line.setStartX(160);
            line.setStartY(250);

        } else if (turnId == 1) { // Middle player
            line.setStartX(450);
            line.setStartY(80);

        } else if (turnId == 2) { // Right player
            line.setStartX(750);
            line.setStartY(250);
        }

        line.setEndX(450);
        line.setEndY(300);

        EventHandler<ActionEvent> moving = event -> {
            // Moving path
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(playCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            // Scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), playCardImage);
            scaleTransition.setFromX(0);
            scaleTransition.setFromY(0);
            scaleTransition.setToX(1.2);
            scaleTransition.setToY(1.2);

            pathTransition.play();
            scaleTransition.play();
        };

        EventHandler<ActionEvent> zoomOut = event -> {
            // Scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), playCardImage);
            scaleTransition.setFromX(1.2);
            scaleTransition.setFromY(1.2);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        EventHandler<ActionEvent> fadeOut = event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), playCardImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        };

        Timeline time = new Timeline();

        time.getKeyFrames().add(new KeyFrame(Duration.millis(0), event -> playCardImage.setVisible(true)));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(1), moving));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(1200), zoomOut));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(2000), fadeOut));


        int diff = nextValue - this.value;
        for (int i = 1; i <= 8; i++) {
            int finalI = i;
            time.getKeyFrames().add(new KeyFrame(Duration.millis(2000 + 150 * i),
                    event -> valueLabel.setText(String.valueOf(nextValue + diff / 8 * finalI))));
        }
        time.play();
    }

    /**
     * Show the animation that other is playing
     *
     * @param turnId - Which player to play the card
     */
    public void otherPlayerThinking(int turnId) {

        if (turnId == 0) {
            shineCircle.setCenterX(150);
            shineCircle.setCenterY(225);
        } else if (turnId == 1) {
            shineCircle.setCenterX(450);
            shineCircle.setCenterY(100);
        } else if (turnId == 2) {
            shineCircle.setCenterX(750);
            shineCircle.setCenterY(225);
        }

        ft = new FillTransition(Duration.seconds(2), shineCircle, Color.BLACK, Color.valueOf("#ffcb21"));
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        shineCircle.setVisible(true);
    }


    /**
     * Show the countdown bar
     */
    public void countdown() {
        countdownBar.setWidth(900);
        countdownBar.setVisible(true);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(countdownBar.widthProperty(), 0);
        KeyFrame kf = new KeyFrame(Duration.seconds(20), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    /**
     * Get the order of the player by username
     *
     * @param username - The current player's username
     * @return - Order of the player (Turn ID)
     */
    public int getTurnByName(String username) {

        String[] players = PlayerStatus.getPlayers();

        // Who's play the card??
        for (int i = 0; i < 4; i++) {
            if (username.equals(players[i])) {
                return i;
            }
        }
        return -1;
    }
}

class GamePageConnection implements Runnable {

    private final GamePageController GUI;

    GamePageConnection(GamePageController gamePage) {
        this.GUI = gamePage;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Object receivedObject = GamePageConn.receive();

                // Other player play the card
                if (receivedObject instanceof PlayCommand) {
                    new Thread(new PlayCommandHandler(GUI, (PlayCommand) receivedObject)).start();
                }

                // It's other player's turn
                else if (receivedObject instanceof NextPlayerCommand) {
                    new Thread(new NextPlayerHandler(GUI, (NextPlayerCommand) receivedObject)).start();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

class PlayCommandHandler implements Runnable {

    PlayCommand command;
    GamePageController GUI;

    PlayCommandHandler(GamePageController GUI, PlayCommand command) {
        this.GUI = GUI;
        this.command = command;
    }

    @Override
    public void run() {

        if (GUI.ft != null) GUI.ft.stop(); // Stop the shining effect

        // Show playing card animation for others
        int turnId = GUI.getTurnByName(command.getUsername());
        if (turnId != 3) {
            Platform.runLater(() -> GUI.playCard(turnId, Card.D2, 20));
        }
    }
}

class NextPlayerHandler implements Runnable {

    GamePageController GUI;
    NextPlayerCommand command;

    public NextPlayerHandler(GamePageController GUI, NextPlayerCommand command) {
        this.GUI = GUI;
        this.command = command;
    }

    @Override
    public void run() {

        int turnId = GUI.getTurnByName(command.getNextPlayerUsername());

        // It's not my turn
        if (turnId != 3) {
            Platform.runLater(() -> GUI.otherPlayerThinking(turnId));
        }

        // It's my turn!!
        else {
            Platform.runLater(() -> GUI.countdown());
        }
    }
}
