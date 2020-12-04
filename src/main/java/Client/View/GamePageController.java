package Client.View;

import Client.Connection.GamePageConn;
import Client.Status.PlayerStatus;
import Client.Status.UserStatus;
import Shared.CardEnum.Card;
import Shared.Command.Game.DrawCommand;
import Shared.Command.Game.NextPlayerCommand;
import Shared.Command.Game.PlayCommand;
import Shared.Command.Game.ReturnPlayCommand;
import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
    public ImageView deskCardImage;
    public JFXButton first;
    public JFXButton second;
    public JFXButton third;
    public JFXButton fourth;
    public JFXButton fifth;

    Card[] myCards = new Card[5];
    int nextPositionToPlace = 0;
    int cardCount = 0;

    JFXButton[] cardButtons;

    volatile int value = 0;

    FillTransition ft;

    @FXML
    void initialize() {

        cardButtons = new JFXButton[]{first, second, third, fourth, fifth};

        countdownBar.setVisible(false);
        shineCircle.setVisible(false);
        playCardImage.setVisible(false);

        Thread connection = new Thread(new GamePageConnection(this));
        connection.start();
    }

    public void pickCard(ActionEvent actionEvent) {
        JFXButton pickedButton = (JFXButton) actionEvent.getSource();

        // Card I picked
        Card pickedCard = myCards[0];
        switch (pickedButton.getId()) {
            case "first" -> {
                pickedCard = myCards[0];
                nextPositionToPlace = 0;
                myCards[0] = null;
            }
            case "second" -> {
                pickedCard = myCards[1];
                nextPositionToPlace = 1;
                myCards[1] = null;
            }
            case "third" -> {
                pickedCard = myCards[2];
                nextPositionToPlace = 2;
                myCards[2] = null;
            }
            case "forth" -> {
                pickedCard = myCards[3];
                nextPositionToPlace = 3;
                myCards[3] = null;
            }
            case "fifth" -> {
                pickedCard = myCards[4];
                nextPositionToPlace = 4;
                myCards[4] = null;
            }
        }

        // Send command to server
        PlayCommand playCommand = new PlayCommand();
        playCommand.setPlayer(UserStatus.getSignInUser());
        playCommand.setCard(pickedCard);
        playCommand.setRemainCardCount(--cardCount); // Since I played a card, so cardCount - 1
        try {
            GamePageConn.send(playCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show play card animation
        pickedButton.setGraphic(null);
        playCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", pickedCard.toString())));
        EventHandler<ActionEvent> moving = event -> {
            Line line = new Line();
            line.setStartX(pickedButton.getLayoutX());
            line.setStartY(pickedButton.getLayoutY());
            line.setEndX(450);
            line.setEndY(300);

            // Path transition
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(playCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            // Scale transition
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), playCardImage);
            scaleTransition.setFromX(0);
            scaleTransition.setFromY(0);
            scaleTransition.setToX(1.4);
            scaleTransition.setToY(1.4);

            pathTransition.play();
            scaleTransition.play();
        };

        // Scale
        EventHandler<ActionEvent> zoomOut = event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), playCardImage);
            scaleTransition.setFromX(1.4);
            scaleTransition.setFromY(1.4);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        // Fade out
        EventHandler<ActionEvent> fadeOut = event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), playCardImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), __ -> pickedButton.setVisible(true)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), moving));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), zoomOut));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(550), fadeOut));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(850), __ -> pickedButton.setVisible(false)));
        timeline.play();
    }


    /**
     * Play the animation that play the card, and show the number adding animation
     *
     * @param turnId    - Which player to play the card.
     * @param card      - The card the player played
     * @param nextValue - The next value of the sea
     */
    public void otherPlayCardAnimation(int turnId, Card card, int nextValue) {

        // Set card image
        playCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", card.toString())));

        // Set card start position
        Line line = new Line();

        if (turnId == 1) { // Left player
            line.setStartX(160);
            line.setStartY(250);

        } else if (turnId == 2) { // Middle player
            line.setStartX(450);
            line.setStartY(80);

        } else if (turnId == 3) { // Right player
            line.setStartX(750);
            line.setStartY(250);
        }

        // Set card end position
        line.setEndX(450);
        line.setEndY(300);

        // The animation for the card
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
            scaleTransition.setToX(1.4);
            scaleTransition.setToY(1.4);

            pathTransition.play();
            scaleTransition.play();
        };

        // Scale
        EventHandler<ActionEvent> zoomOut = event -> {

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), playCardImage);
            scaleTransition.setFromX(1.4);
            scaleTransition.setFromY(1.4);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        // Fade out
        EventHandler<ActionEvent> fadeOut = event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), playCardImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        };


        Timeline time = new Timeline();
        time.getKeyFrames().add(new KeyFrame(Duration.millis(0), event -> playCardImage.setVisible(true)));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(1), moving));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(700), zoomOut));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(1700), fadeOut));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(2000), event -> playCardImage.setVisible(false)));


        // The value calculating
        int diff = nextValue - this.value;
        if (diff > 0) {
            for (int i = 1; i <= diff; i++) {
                int finalI = i;
                time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + 50 * i),
                        event -> valueLabel.setText(String.format("%02d", this.value + finalI))));
            }
        } else if (diff < 0) {
            for (int i = 1; i <= -diff; i++) {
                int finalI = i;
                time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + 50 * i),
                        event -> valueLabel.setText(String.format("%02d", this.value - finalI))));
            }
        }

        time.play();
    }

    /**
     * Show the animation that other is playing
     *
     * @param turnId - Which player to play the card
     */
    public void otherPlayerThinkingAnimation(int turnId) {

        if (turnId == 1) {
            shineCircle.setCenterX(150);
            shineCircle.setCenterY(225);
        } else if (turnId == 2) {
            shineCircle.setCenterX(450);
            shineCircle.setCenterY(100);
        } else if (turnId == 3) {
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
    public void countdownAnimation() {
        countdownBar.setWidth(900);
        countdownBar.setVisible(true);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(countdownBar.widthProperty(), 0);
        KeyFrame kf = new KeyFrame(Duration.seconds(20), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }


    /**
     * Draw the card to me
     *
     * @param card Card to show on my desk
     */
    public void drawCardToMeAnimation(Card card) {

        playCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", card.toString())));

        Line line = new Line();
        line.setStartX(820);
        line.setStartY(310);

        line.setEndY(394);
        switch (nextPositionToPlace) {
            case 0 -> line.setEndX(95);
            case 1 -> line.setEndX(235);
            case 2 -> line.setEndX(385);
            case 3 -> line.setEndX(546);
            case 4 -> line.setEndX(696);
        }

        EventHandler<ActionEvent> moving = event -> {
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(playCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            pathTransition.play();
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), event -> playCardImage.setVisible(true)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), moving));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(501), event -> playCardImage.setVisible(false)));
        timeline.play();

        cardButtons[nextPositionToPlace].setGraphic(new ImageView(new Image(String.format("/Client/Img/Card/%s.png", card.toString()))));

    }

    void draw5CardsToMeAnimation(Card[] cards) {

    }

    /**
     * Get the order of the player by username
     *
     * @param username - The current player's username
     * @return - Order of the player (Turn ID)
     */
    public int getTurnByName(String username) {

        String[] players = PlayerStatus.getTurnPlayers();

        // Who's play the card??
        for (int i = 0; i < 4; i++) {
            if (username.equals(players[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find the next empty place of the card that had been played
     */
    public void findNextEmptyPlaceForCard() {
        for (int i = 0; i < 5; i++) {
            if (myCards[i] == null) {
                nextPositionToPlace = i;
                return;
            }
        }
        nextPositionToPlace = -1; // There are full of cards
    }

    /**
     * Draw a card from server, you have to record where the card should play
     *
     * @param card Card drew from server
     */
    public void addOneCard(Card card) {
        cardCount++;
        myCards[nextPositionToPlace] = card; // Save the card to the array
        findNextEmptyPlaceForCard(); // Find the next empty position
    }
}

/**
 * Receive the command from server
 */
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
                if (receivedObject instanceof ReturnPlayCommand) {
                    new Thread(new ReturnPlayCommandHandler(GUI, (ReturnPlayCommand) receivedObject)).start();
                }

                // It's other player's turn
                else if (receivedObject instanceof NextPlayerCommand) {
                    new Thread(new NextPlayerHandler(GUI, (NextPlayerCommand) receivedObject)).start();
                }

                // Draw cards
                else if (receivedObject instanceof DrawCommand) {
                    new Thread(new DrawHandler(GUI, (DrawCommand) receivedObject)).start();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * I'll get the Draw Card Command (I'll draw the card)
 */
class DrawHandler implements Runnable {

    GamePageController GUI;
    DrawCommand command;

    DrawHandler(GamePageController GUI, DrawCommand command) {
        this.GUI = GUI;
        this.command = command;
    }

    @Override
    public void run() {
        Card[] cards = command.getDrawCards();

        // First draw
        if (cards.length == 5) {

        }

        // Draw one card
        else {
            Card card = cards[0];
            Platform.runLater(() -> GUI.drawCardToMeAnimation(card)); // Show the drawing card animation
            GUI.addOneCard(card);
        }
    }
}


/**
 * When player play a card, all the user will get this message.
 */
class ReturnPlayCommandHandler implements Runnable {

    ReturnPlayCommand command;
    GamePageController GUI;

    ReturnPlayCommandHandler(GamePageController GUI, ReturnPlayCommand command) {
        this.GUI = GUI;
        this.command = command;
    }

    @Override
    public void run() {

        if (GUI.ft != null) GUI.ft.stop(); // Stop the shining effect

        // Show playing card animation for others
        int turnId = GUI.getTurnByName(command.getPlayer());

        // The card is not played by me. Show the animation of whom to play the card
        if (turnId != 0) {
            Platform.runLater(() -> GUI.otherPlayCardAnimation(turnId, command.getCard(), command.getValue()));
        }

        // There's no card on the desk, hide it!!
        if (!command.isHasCardsInDesk()) {
            Platform.runLater(() -> GUI.deskCardImage.setVisible(false));
        }
    }
}

/**
 * It's time to the next player to play card, and it may be my turn
 */
class NextPlayerHandler implements Runnable {

    GamePageController GUI;
    NextPlayerCommand command;

    public NextPlayerHandler(GamePageController GUI, NextPlayerCommand command) {
        this.GUI = GUI;
        this.command = command;
    }

    @Override
    public void run() {

        int turnId = GUI.getTurnByName(command.getNextPlayer());

        // It's not my turn
        if (turnId != 3) {
            Platform.runLater(() -> GUI.otherPlayerThinkingAnimation(turnId));
        }

        // It's my turn!! Count down the bar
        else {
            Platform.runLater(() -> {
                GUI.countdownAnimation();
            });
        }
    }
}
