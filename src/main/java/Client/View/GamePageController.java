package Client.View;

import Client.Connection.GamePageConn;
import Client.Status.PlayerStatus;
import Shared.NextPlayerCommand;
import Shared.PlayCommand;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;

public class GamePageController {

    public ImageView playCardImage;

    @FXML
    void initialize() {

        playCardImage.setVisible(false);
        Thread connection = new Thread(new GamePageConnection(this));
        connection.start();
    }


    /**
     * Play the animation that play the card
     *
     * @param turnId - Which player to play the card
     */
    public void playCard(int turnId) {
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

        EventHandler moving = event -> {
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

        EventHandler zoomOut = event -> {
            // Scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), playCardImage);
            scaleTransition.setFromX(1.2);
            scaleTransition.setFromY(1.2);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        EventHandler fadeOut = event -> {
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
        time.play();
    }

    /**
     * Show the animation that other is playing
     *
     * @param turnId - Which player to play the card
     */
    public void otherPlayerThinking(int turnId) {
        // TODO Show the animation that other is playing
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
        // TODO Show the user who's thinking
    }
}

class PlayCommandHandler implements Runnable {

    PlayCommand command;
    GamePageController GUI;

    PlayCommandHandler(GamePageController GUI, PlayCommand command) {
        this.command = command;
    }

    @Override
    public void run() {

        int turnId = GUI.getTurnByName(command.getUsername());

        // Show playing card animation
        Platform.runLater(() -> GUI.playCard(turnId));
    }
}
