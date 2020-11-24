package Client.View;

import Client.Main;
import Client.Status.Player;
import Shared.CardEnum.Instruction;
import Shared.CardEnum.Status;
import Shared.PlayCommand;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class GamePage {

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
}

class GamePageConnection implements Runnable {

    private final GamePage GUI;
    private final Socket server;
    private final String[] players = Player.getPlayers();
    private int turnId = 0;

    GamePageConnection(GamePage gamePage) {
        this.GUI = gamePage;
        server = Main.getServer();
    }

    @Override
    public void run() {
        ObjectInputStream out;

        try {
            out = new ObjectInputStream(server.getInputStream());
            PlayCommand playCommand = (PlayCommand) out.readObject();

            if (playCommand.getStatus() == Status.KEEP) { // Keep playing game

                // Get number turn of the next turn of the user
                for (int i = 0; i < 4; i++) {
                    if (playCommand.getTurn().equals(players[i])) {
                        turnId = i;
                        break;
                    }
                }

                // Other user play a card, show it on the GUI
                if (playCommand.getInstruction() == Instruction.PLAY) {
                    Platform.runLater(() -> {
                        GUI.playCard(turnId);
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
