package Client.View;

import Client.Status.PlayerStatus;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GamePageController3 extends GamePageController {

    public ImageView turn1Icon;
    public ImageView turn2Icon;
    public Text firstPlayerLabel;
    public Text secondPlayerLabel;
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


    @FXML
    void initialize() {
        playerIcons = new ImageView[]{null, turn1Icon, turn2Icon};
        playerNames = new Text[]{null, firstPlayerLabel, secondPlayerLabel};

        otherCards = new Rectangle[][]{
                null,
                {firstPFirstC, firstPSecondC, firstPThirdC, firstPForthC, firstPFifthC},
                {secondPFirstC, secondPSecondC, secondPThirdC, secondPForthC, secondPFifthC},
        };

        // Get players
        for (int i = 1; i < PlayerStatus.getTurnPlayers().length; i++) {
            playerNames[i].setText(PlayerStatus.getTurnPlayers()[i]);
        }

        super.init(playerIcons, playerNames, otherCards);
    }
}
