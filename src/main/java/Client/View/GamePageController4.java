package Client.View;

import Client.Status.PlayerStatus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GamePageController4 extends GamePageController {

    public ImageView otherPlayCardImage;
    public ImageView mePlayCardImage;
    public ImageView drawCardImage;
    public Circle shineCircle;
    public Rectangle countdownBar;
    public Label valueLabel;
    public ImageView deskCardImage;

    public Pane coverPane;
    public ImageView turn1Icon;
    public ImageView turn2Icon;
    public ImageView turn3Icon;
    public Pane card5Cover;

    public Text firstPlayerLabel;
    public Text secondPlayerLabel;
    public Text thirdPlayerLabel;
    public AnchorPane pane;
    public StackPane stackPane;
    public Rectangle firstPFirstC;
    public Rectangle firstPSecondC;
    public Rectangle firstPThirdC;
    public Rectangle firstPForthC;
    public Rectangle firstPFifthC;
    public Rectangle secondPFirstC;
    public Rectangle secondPSecondC;
    public Rectangle secondPThirdC;
    public Rectangle secondPForthC;
    public Rectangle secondPFifthC;
    public Rectangle thirdPFirstC;
    public Rectangle thirdPSecondC;
    public Rectangle thirdPThirdC;
    public Rectangle thirdPForthC;
    public Rectangle thirdPFifthC;

    ImageView[] playerIcons;
    Text[] playerNames;
    Rectangle[][] otherCards;

    @FXML
    void initialize() {

        playerIcons = new ImageView[]{null, turn1Icon, turn2Icon, turn3Icon};
        playerNames = new Text[]{null, firstPlayerLabel, secondPlayerLabel, thirdPlayerLabel};

        otherCards = new Rectangle[][]{
                null,
                {firstPFirstC, firstPSecondC, firstPThirdC, firstPForthC, firstPFifthC},
                {secondPFirstC, secondPSecondC, secondPThirdC, secondPForthC, secondPFifthC},
                {thirdPFirstC, thirdPSecondC, thirdPThirdC, thirdPForthC, thirdPFifthC}
        };

        // Get players
        for (int i = 1; i < PlayerStatus.getTurnPlayers().length; i++) {
            playerNames[i].setText(PlayerStatus.getTurnPlayers()[i]);
        }

        super.init(playerIcons, playerNames, otherCards);
    }
}
