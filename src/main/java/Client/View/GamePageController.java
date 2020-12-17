package Client.View;

import Client.Connection.GamePageConn;
import Client.Main;
import Client.Status.PlayerStatus;
import Client.Status.UserStatus;
import Shared.CardEnum.Card;
import Shared.Command.Game.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;


class PlayingStatus {
    private Card pickedCard;
    private JFXButton pickedButton;
    private Status status = Status.NORMAL;

    enum Status {
        NORMAL, FIVE, TEN, QUEEN
    }

    public Card getPickedCard() {
        return pickedCard;
    }

    public void setPickedCard(Card pickedCard) {
        this.pickedCard = pickedCard;
    }

    public JFXButton getPickedButton() {
        return pickedButton;
    }

    public void setPickedButton(JFXButton pickedButton) {
        this.pickedButton = pickedButton;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

public class GamePageController {

    public ImageView otherPlayCardImage;
    public ImageView mePlayCardImage;
    public ImageView drawCardImage;
    public Circle shineCircle;
    public Rectangle countdownBar;
    public Label valueLabel;
    public ImageView deskCardImage;
    public JFXButton first;
    public JFXButton second;
    public JFXButton third;
    public JFXButton fourth;
    public JFXButton fifth;
    public Pane coverPane;
    public ImageView turn1Icon;
    public ImageView turn2Icon;
    public ImageView turn3Icon;
    public Pane card5Cover;

    static PlayingStatus playingStatus = new PlayingStatus();
    public Text firstPlayerLabel;
    public Text secondPlayerLabel;
    public Text thirdPlayerLabel;
    public AnchorPane pane;
    public StackPane stackPane;

    Card[] myCards = new Card[5];
    int nextPositionToPlace = 0;
    int cardCount = 0;

    JFXButton[] cardButtons;
    ImageView[] playerIcons;
    Text[] playerNames;

    volatile int value = 0;

    FillTransition ft;

    @FXML
    void initialize() {

        cardButtons = new JFXButton[]{first, second, third, fourth, fifth};
        playerIcons = new ImageView[]{null, turn1Icon, turn2Icon, turn3Icon};
        playerNames = new Text[]{null, firstPlayerLabel, secondPlayerLabel, thirdPlayerLabel};

        System.out.println(Arrays.toString(PlayerStatus.getTurnPlayers()));
        for (int i = 1; i < PlayerStatus.getTurnPlayers().length; i++) {
            playerNames[i].setText(PlayerStatus.getTurnPlayers()[i]);
        }

        countdownBar.setVisible(false);
        shineCircle.setVisible(false);

        otherPlayCardImage.setVisible(false);
        mePlayCardImage.setVisible(false);
        drawCardImage.setVisible(false);

        card5Cover.setVisible(false);

        Thread connection = new Thread(new GamePageConnection(this));
        connection.start();
    }

    /**
     * When a user picked a card to play
     */
    public void pickCard(ActionEvent actionEvent) {
        JFXButton pickedButton = (JFXButton) actionEvent.getSource();

        // Card I picked
        Card pickedCard;
        int pickedNumber = -1;
        switch (pickedButton.getId()) {
            case "first" -> pickedNumber = 0;
            case "second" -> pickedNumber = 1;
            case "third" -> pickedNumber = 2;
            case "fourth" -> pickedNumber = 3;
            case "fifth" -> pickedNumber = 4;
        }
        pickedCard = myCards[pickedNumber];
        myCards[pickedNumber] = null;
        findNextEmptyPlaceForCard();

        if (pickedCard.getRank() == 5) {

            // Save current status
            playingStatus.setStatus(PlayingStatus.Status.FIVE);
            playingStatus.setPickedCard(pickedCard);
            playingStatus.setPickedButton(pickedButton);

            card5Cover.setVisible(true);
            return;

        } else if (pickedCard.getRank() == 10 || pickedCard.getRank() == 12) {
            playingStatus.setPickedCard(pickedCard);
            playingStatus.setPickedButton(pickedButton);

            switch (pickedCard.getRank()) {
                case 10 -> {
                    playingStatus.setStatus(PlayingStatus.Status.TEN);
                    showPlusOrMinusDialog(10);
                }
                case 12 -> {
                    playingStatus.setStatus(PlayingStatus.Status.QUEEN);
                    showPlusOrMinusDialog(20);
                }
            }
            return;
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

        mePlayCardAnimation(pickedButton, pickedCard);
    }

    /**
     * When play card of 10 or 20,
     * show this dialog to let player choose which action they want to plus or minus
     *
     * @param number The number the player played
     */
    private void showPlusOrMinusDialog(int number) {
        VBox chooseNumberBox = new VBox(20);
        chooseNumberBox.setPrefHeight(350);
        chooseNumberBox.setPrefWidth(150);
        chooseNumberBox.setAlignment(Pos.CENTER);

        // Add button
        JFXButton plusButton = new JFXButton();
        plusButton.setRipplerFill(Paint.valueOf("#FC3333"));
        plusButton.getStyleClass().add("plus-button");
        plusButton.getStylesheets().add("/Client/Style/GamePage.css");

        ImageView addImage = new ImageView(new Image("/Client/Img/plus.png"));
        addImage.setFitWidth(72);
        addImage.setFitHeight(72);
        plusButton.setGraphic(addImage);

        // Number
//        Text numberText = new Text(String.valueOf(number));
//        numberText.getStyleClass().add("font-");
//        numberText.setFont(Font.font("Taipei Sans TC Beta"));
//        numberText.setFont(Font.font(60));
        Label label = new Label(String.valueOf(number));
        label.setFont(Font.font(72));
        label.getStyleClass().add("add-minus-number-label");
        label.getStylesheets().add("/Client/Style/GamePage.css");

        // Minus button
        JFXButton minusButton = new JFXButton();
        minusButton.setRipplerFill(Paint.valueOf("#3936F0"));
        minusButton.getStyleClass().add("minus-button");
        minusButton.getStylesheets().add("/Client/Style/GamePage.css");

        ImageView minusImage = new ImageView(new Image("/Client/Img/minus.png"));
        minusImage.setFitWidth(72);
        minusImage.setFitHeight(72);
        minusButton.setGraphic(minusImage);

        // Add all component
        chooseNumberBox.getChildren().addAll(plusButton, label, minusButton);

        // Dialog
        JFXDialog dialog = new JFXDialog(stackPane, chooseNumberBox, JFXDialog.DialogTransition.CENTER);
        dialog.show();

        // Button Actions
        plusButton.setOnAction(event -> {

            PlayCommand command = new PlayCommand();
            command.setPlusValue(true);
            command.setPlayer(UserStatus.getSignInUser());
            command.setCard(playingStatus.getPickedCard());
            command.setRemainCardCount(--cardCount);
            try {
                GamePageConn.send(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.close();

            mePlayCardAnimation(playingStatus.getPickedButton(), playingStatus.getPickedCard());
        });

        minusButton.setOnAction(event -> {
            PlayCommand command = new PlayCommand();
            command.setPlusValue(false);
            command.setPlayer(UserStatus.getSignInUser());
            command.setCard(playingStatus.getPickedCard());
            command.setRemainCardCount(--cardCount);
            try {
                GamePageConn.send(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.close();

            mePlayCardAnimation(playingStatus.getPickedButton(), playingStatus.getPickedCard());
        });
    }

    /**
     * When play card of 5, choose a player to be te next player
     */
    public void choosePlayer(MouseEvent mouseEvent) {

        if (playingStatus.getStatus() != PlayingStatus.Status.FIVE) return; // You're not player the '5' card

        ImageView pickedPlayerIcon = (ImageView) mouseEvent.getSource();
        int pickedPlayerTurn = 0;

        for (int i = 1; i <= 3; i++) {
            if (playerIcons[i] == pickedPlayerIcon) {
                pickedPlayerTurn = i;
                break;
            }
        }
        String assignPlayer = PlayerStatus.getTurnPlayers()[pickedPlayerTurn];
        System.out.println("Assign to " + assignPlayer);

        // Send play command to server
        PlayCommand playCommand = new PlayCommand();
        playCommand.setPlayer(UserStatus.getSignInUser());
        playCommand.setCard(playingStatus.getPickedCard());
        playCommand.setAssignPlayer(assignPlayer);
        playCommand.setRemainCardCount(--cardCount); // Since I played a card, so cardCount - 1
        try {
            GamePageConn.send(playCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        card5Cover.setVisible(false); // Hide the cover

        mePlayCardAnimation(playingStatus.getPickedButton(), playingStatus.getPickedCard()); // Play the animation

        playingStatus = new PlayingStatus(); // Revert the plating status
    }

    /**
     * Play the animation that I play the card
     *
     * @param pickedButton The button I clicked
     * @param pickedCard   The card I picked
     */
    private void mePlayCardAnimation(JFXButton pickedButton, Card pickedCard) {

        System.out.println("X: " + pickedButton.getLayoutX() + ", Y: " + pickedButton.getLayoutY());

        EventHandler<ActionEvent> init = event -> {
            mePlayCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", pickedCard.toString())));
            mePlayCardImage.setFitWidth(120);
            mePlayCardImage.setFitHeight(168);
            mePlayCardImage.setScaleX(1);
            mePlayCardImage.setScaleY(1);
            mePlayCardImage.setTranslateX(-150);
            mePlayCardImage.setTranslateY(-150);
            mePlayCardImage.setOpacity(1);
            mePlayCardImage.setVisible(true);
            pickedButton.setGraphic(null);
        };

        EventHandler<ActionEvent> moving = event -> {

            Line line = new Line();
            line.setStartX(pickedButton.getLayoutX() + 60);
            line.setStartY(pickedButton.getLayoutY() + 84);
            line.setEndX(450);
            line.setEndY(300);

            // Path transition
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(mePlayCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            // Scale transition
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), mePlayCardImage);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(1.4);
            scaleTransition.setToY(1.4);

            pathTransition.play();
            scaleTransition.play();
        };

        // Scale
        EventHandler<ActionEvent> zoomOut = event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), mePlayCardImage);
            scaleTransition.setFromX(1.4);
            scaleTransition.setFromY(1.4);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        // Fade out
        EventHandler<ActionEvent> fadeOut = event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), mePlayCardImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), init));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), moving));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(550), zoomOut));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1080), fadeOut));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(2000), __ -> mePlayCardImage.setVisible(false)));
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

        EventHandler<ActionEvent> init = event -> {
            // Set card image
            otherPlayCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", card.toString())));
            otherPlayCardImage.setFitWidth(120);
            otherPlayCardImage.setFitHeight(168);
            otherPlayCardImage.setScaleX(1);
            otherPlayCardImage.setScaleY(1);
            otherPlayCardImage.setTranslateX(-150);
            otherPlayCardImage.setTranslateY(-150);
            otherPlayCardImage.setOpacity(1);
            otherPlayCardImage.setVisible(true);
        };

        // The animation for the card
        EventHandler<ActionEvent> moving = event -> {

            // Set card start position
            Line line = new Line();

            double x = playerIcons[turnId].getLayoutX() + playerIcons[turnId].getFitWidth() / 2;
            double y = playerIcons[turnId].getLayoutY() + playerIcons[turnId].getFitHeight() / 2;
            line.setStartX(x);
            line.setStartY(y);

            // Set card end position
            line.setEndX(450);
            line.setEndY(300);

            // Moving path
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(otherPlayCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            // Scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), otherPlayCardImage);
            scaleTransition.setFromX(0);
            scaleTransition.setFromY(0);
            scaleTransition.setToX(1.4);
            scaleTransition.setToY(1.4);

            pathTransition.play();
            scaleTransition.play();
        };

        // Scale
        EventHandler<ActionEvent> zoomOut = event -> {

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(50), otherPlayCardImage);
            scaleTransition.setFromX(1.4);
            scaleTransition.setFromY(1.4);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        // Fade out
        EventHandler<ActionEvent> fadeOut = event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), otherPlayCardImage);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        };


        Timeline time = new Timeline();
        time.getKeyFrames().add(new KeyFrame(Duration.millis(0), init));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(0), moving));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(700), zoomOut));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(1700), fadeOut));
        time.getKeyFrames().add(new KeyFrame(Duration.millis(2000), event -> otherPlayCardImage.setVisible(false)));

        countingValue(nextValue, time);

        time.getKeyFrames().add(new KeyFrame(Duration.millis(5000), __ -> this.value = nextValue));

        time.play();
    }

    /**
     * When lose, do some animation, then go back to the home page
     */
    public void meLoseAnimation() {
        Pane losePane = new Pane();
        losePane.setPrefWidth(900);
        losePane.setPrefHeight(600);
        losePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Lose label
        Label loseLabel = new Label("YOU LOSE");
        loseLabel.getStyleClass().add("eng-font");
        loseLabel.getStylesheets().add("/Client/Style/General.css");
        loseLabel.setFont(Font.font(100));
        loseLabel.setTextFill(Paint.valueOf("white"));
        loseLabel.setLayoutX(130);
        loseLabel.setLayoutY(150);
        losePane.getChildren().add(loseLabel);

        // Back to home page
        JFXButton backButton = new JFXButton("回到主畫面");
        backButton.getStyleClass().add("ch-font");
        backButton.getStylesheets().add("/Client/Style/General.css");
        backButton.setButtonType(JFXButton.ButtonType.RAISED);
        backButton.setRipplerFill(Paint.valueOf("#d9d9d9"));
        backButton.setLayoutX(394);
        backButton.setLayoutY(400);
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: black");
        backButton.setOnAction(__ -> Main.switchScene("Home"));

        pane.getChildren().addAll(losePane, backButton);

    }


    /**
     * Show cross on their icon
     *
     * @param losePlayer Turn of the person who lose
     */
    public void otherLoseAnimation(String losePlayer) {
        ImageView crossImage = new ImageView(new Image("/Client/Img/cross.png"));
        crossImage.setFitHeight(150);
        crossImage.setFitWidth(150);
        crossImage.setOpacity(0); // For fade in animation

        int loserTurn = getTurnByName(losePlayer);
        double x = (playerIcons[loserTurn].getFitWidth() - crossImage.getFitWidth()) / 2 + playerIcons[loserTurn].getLayoutX() - 8;
        double y = (playerIcons[loserTurn].getFitHeight() - crossImage.getFitHeight()) / 2 + playerIcons[loserTurn].getLayoutY();
        crossImage.setX(x);
        crossImage.setY(y);
        pane.getChildren().add(crossImage);


        // Animation
        EventHandler<ActionEvent> fadeIn = __ -> {
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setDuration(Duration.millis(400));
            fadeTransition.setNode(crossImage);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);

            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(600));
            scaleTransition.setNode(crossImage);
            scaleTransition.setFromX(1);
            scaleTransition.setFromY(1);
            scaleTransition.setToX(1.3);
            scaleTransition.setToY(1.3);

            fadeTransition.play();
            scaleTransition.play();
        };

        EventHandler<ActionEvent> zoomOut = __ -> {
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(200));
            scaleTransition.setNode(crossImage);
            scaleTransition.setFromX(1.3);
            scaleTransition.setFromY(1.3);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), fadeIn));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(600), zoomOut));
        timeline.play();
    }

    /**
     * When I win, show win animation
     */
    public void meWinAnimation() {
        Pane losePane = new Pane();
        losePane.setPrefWidth(900);
        losePane.setPrefHeight(600);
        losePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Lose label
        Label loseLabel = new Label("YOU WIN");
        loseLabel.getStyleClass().add("eng-font");
        loseLabel.getStylesheets().add("/Client/Style/General.css");
        loseLabel.setFont(Font.font(100));
        loseLabel.setTextFill(Paint.valueOf("white"));
        loseLabel.setLayoutX(175);
        loseLabel.setLayoutY(150);
        losePane.getChildren().add(loseLabel);

        // Back to home page
        JFXButton backButton = new JFXButton("回到主畫面");
        backButton.getStyleClass().add("ch-font");
        backButton.getStylesheets().add("/Client/Style/General.css");
        backButton.setButtonType(JFXButton.ButtonType.RAISED);
        backButton.setRipplerFill(Paint.valueOf("#d9d9d9"));
        backButton.setLayoutX(394);
        backButton.setLayoutY(400);
        backButton.setStyle("-fx-background-color: white; -fx-text-fill: black");
        backButton.setOnAction(__ -> Main.switchScene("Home"));

        pane.getChildren().addAll(losePane, backButton);

        // Color transition
        Timeline timeline = new Timeline();
        String[] colorList = new String[]{"red", "orange", "yellow", "limegreen", "teal", "blue", "violet", "red"};
        for (int i = 0; i < colorList.length; i++) {
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(800 * i),
                            new KeyValue(loseLabel.textFillProperty(), Paint.valueOf(colorList[i]))));
        }
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
     * Count up/down the value in the sea
     *
     * @param nextValue Next value in the sea
     * @param time      Timeline
     */
    public void countingValue(int nextValue, Timeline time) {

        System.out.println("Current Value: " + this.value);
        System.out.println("Next Value: " + nextValue);

        // The value calculating
        int diff = nextValue - this.value;
        if (diff == 0) return;

        int allDuration;
        if (Math.abs(diff) > 80) allDuration = 2000;
        else if (Math.abs(diff) > 50) allDuration = 1500;
        else if (Math.abs(diff) > 10) allDuration = 1000;
        else if (Math.abs(diff) > 5) allDuration = 500;
        else allDuration = 100;

        int diffSecond = allDuration / diff;
        System.out.println("diff: " + diff);
        System.out.println("diff Second: " + diffSecond);

        if (diff > 0) {
            for (int i = 1; i <= diff; i++) {
                System.out.print(i + " ");
                int finalI = i;
                time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + diffSecond * i),
                        event -> valueLabel.setText(String.format("%02d", this.value + finalI))));
            }
            time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + diffSecond * (diff + 1)),
                    __ -> this.value = nextValue));
        } else {
            for (int i = 1; i <= -diff; i++) {
                System.out.print(i + " ");
                int finalI = i;
                time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + (-diffSecond) * i),
                        event -> valueLabel.setText(String.format("%02d", this.value - finalI))));
            }
            time.getKeyFrames().add(new KeyFrame(Duration.millis(2200 + (-diffSecond) * (-diff + 1)),
                    __ -> this.value = nextValue));
        }

        System.out.println();
    }

    public void countingValue(int nextValue) {
        Timeline time = new Timeline();
        countingValue(nextValue, time);
        time.play();
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
    public void draw1CardToMeAnimation(Card card) {

        drawCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", card.toString())));
        drawCardImage.setX(820);
        drawCardImage.setY(310);

        Line line = new Line();
        line.setStartX(848);
        line.setStartY(351);

        line.setEndY(478);
        switch (nextPositionToPlace) {
            case 0 -> line.setEndX(180);
            case 1 -> line.setEndX(320);
            case 2 -> line.setEndX(460);
            case 3 -> line.setEndX(600);
            case 4 -> line.setEndX(740);
        }

        // Move a card from desk the my empty space
        EventHandler<ActionEvent> moving = event -> {

            // Path transition
            PathTransition pathTransition = new PathTransition();
            pathTransition.setNode(drawCardImage);
            pathTransition.setDuration(Duration.millis(500));
            pathTransition.setPath(line);
            pathTransition.setCycleCount(1);
            pathTransition.setInterpolator(Interpolator.EASE_IN);

            // Scale transition
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setNode(drawCardImage);
            scaleTransition.setDuration(Duration.millis(500));
            scaleTransition.setFromX(0.54);
            scaleTransition.setFromY(0.54);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);

            pathTransition.play();
            scaleTransition.play();
        };

        EventHandler<ActionEvent> fin = event -> {
            ImageView imageView = new ImageView(new Image(String.format("/Client/Img/Card/%s.png", card.toString())));
            imageView.setFitHeight(168);
            imageView.setFitWidth(120);
            cardButtons[nextPositionToPlace].setGraphic(imageView);
            drawCardImage.setVisible(false);

            // Add card at server
            myCards[nextPositionToPlace] = card; // Save the card to the array
            cardCount++;
            findNextEmptyPlaceForCard(); // Find the next empty position
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0), event -> drawCardImage.setVisible(true)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), moving));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(501), fin));
        timeline.play();
    }

    /**
     * Draw 5 cards to me
     *
     * @param cards Card array
     */
    void draw5CardsToMeAnimation(Card[] cards) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < cards.length; i++) {
            int finalI = i;

            // Card animation
            int finalI1 = i;
//            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300 * i), __ -> playCardImage.setVisible(true)));

            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300 * i + 1), __ -> {
                drawCardImage.setImage(new Image(String.format("/Client/Img/Card/%s.png", cards[finalI1].toString())));
                drawCardImage.setX(820);
                drawCardImage.setY(310);
                drawCardImage.setVisible(true);
                drawCardImage.setFitWidth(120);
                drawCardImage.setFitHeight(168);

                Line line = new Line();
                line.setStartX(848);
                line.setStartY(351);
                line.setEndX(180 + finalI * 140);
                line.setEndY(478);

                // Path transition
                PathTransition pathTransition = new PathTransition();
                pathTransition.setNode(drawCardImage);
                pathTransition.setDuration(Duration.millis(295));
                pathTransition.setPath(line);
                pathTransition.setCycleCount(1);
                pathTransition.setInterpolator(Interpolator.EASE_IN);
                pathTransition.play();

                // Scale transition
                ScaleTransition scaleTransition = new ScaleTransition();
                scaleTransition.setNode(drawCardImage);
                scaleTransition.setFromX(0.54);
                scaleTransition.setFromY(0.54);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.play();
            }));

            // Set button with a background image card
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300 * (i + 1)), __ -> {
                drawCardImage.setVisible(false);
                ImageView imageView = new ImageView(new Image(String.format("/Client/Img/Card/%s.png", cards[finalI].toString())));
                imageView.setFitHeight(168);
                imageView.setFitWidth(120);
                cardButtons[finalI].setGraphic(imageView);
            }));
        }
        timeline.play();

        // Save 5 cards to client
        cardCount = 5;
        myCards = Arrays.copyOf(cards, cards.length);
        findNextEmptyPlaceForCard();
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

    public void goBack(ActionEvent actionEvent) {
        try {
            GamePageConn.leave();
            Main.switchScene("Home");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                // Lose game (me or other lose the game)
                else if (receivedObject instanceof LoseGameCommand) {

                    LoseGameCommand command = (LoseGameCommand) receivedObject;

                    // I lose
                    if (command.getLosePlayer().equals(UserStatus.getSignInUser())) {

                        // Show lose animation
                        Platform.runLater(GUI::meLoseAnimation);
                        break;
                    }
                    // Other lose
                    else {
                        Platform.runLater(() -> GUI.otherLoseAnimation(command.getLosePlayer()));
                    }

                } else if (receivedObject instanceof WinnerCommand) {
                    Platform.runLater(GUI::meWinAnimation);
                    break;

                } else if (receivedObject instanceof NoRemainCardsWinnerCommand) {

                    NoRemainCardsWinnerCommand command = (NoRemainCardsWinnerCommand) receivedObject;
                    if (command.getPlayerName().equals(UserStatus.getSignInUser())) {
                        Platform.runLater(GUI::meWinAnimation);
                        break;
                    }
                    // Others win the game -> let him disappear in the game
                    else {
                        int winnerTurn = GUI.getTurnByName(command.getPlayerName());
                        Platform.runLater(() -> {
                            GUI.playerIcons[winnerTurn].setOpacity(0.1);
                            GUI.playerNames[winnerTurn].setOpacity(0.1);
                        });
                    }

                } else if (receivedObject instanceof LeaveGameRoomCommand) {
                    // I leave the room, stop this thread
                    break;
                }

            } catch (SocketException ex) {
                System.out.println("Bye Bye!");
                break;
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
            Platform.runLater(() -> GUI.draw5CardsToMeAnimation(cards));
        }

        // Draw one card
        else {
            Card card = cards[0];
            Platform.runLater(() -> GUI.draw1CardToMeAnimation(card)); // Show the drawing card animation
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
        GUI.shineCircle.setVisible(false);

        // Show playing card animation for others
        int turnId = GUI.getTurnByName(command.getPlayer());

        // The card is not played by me. Show the animation of whom to play the card
        if (turnId != 0) {
            Platform.runLater(() -> GUI.otherPlayCardAnimation(turnId, command.getCard(), command.getValue()));
        } else {
            Platform.runLater(() -> GUI.countingValue(command.getValue()));
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
        if (turnId != 0) {
            Platform.runLater(() -> GUI.otherPlayerThinkingAnimation(turnId));
            Platform.runLater(() -> GUI.coverPane.setVisible(true));
        }

        // It's my turn!! Count down the bar
        else {
            Platform.runLater(() -> GUI.countdownAnimation());
            Platform.runLater(() -> GUI.coverPane.setVisible(false));
        }
    }
}
