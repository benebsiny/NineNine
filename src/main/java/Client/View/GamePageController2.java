package Client.View;

import Client.Status.PlayerStatus;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GamePageController2 extends GamePageController {

    public ImageView turn1Icon;
    public Text firstPlayerLabel;

    public Rectangle firstPFirstC;
    public Rectangle firstPSecondC;
    public Rectangle firstPThirdC;
    public Rectangle firstPForthC;
    public Rectangle firstPFifthC;

    @FXML
    void initialize() {
        playerIcons = new ImageView[]{null, turn1Icon};
        playerNames = new Text[]{null, firstPlayerLabel};

        otherCards = new Rectangle[][]{
                null,
                {firstPFirstC, firstPSecondC, firstPThirdC, firstPForthC, firstPFifthC},
        };

        // Get players
        for (int i = 1; i < PlayerStatus.getTurnPlayers().length; i++) {
            playerNames[i].setText(PlayerStatus.getTurnPlayers()[i]);
        }

        super.init(playerIcons, playerNames, otherCards);
    }
}
