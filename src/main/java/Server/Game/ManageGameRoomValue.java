package Server.Game;

import Server.Main;
import Shared.CardEnum.Card;
import Shared.CardEnum.Suit;
import Shared.Command.Game.PlayCommand;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManageGameRoomValue {

    public static void deleteGameRoomPlayer(PlayCommand playCommand){

        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        String deletePlayer = playCommand.getPlayer();

        for (GameRoom gameRoom : gameRoomList) {          //確定玩家的遊戲房間
            if (Arrays.asList(gameRoom.getPlayersName()).contains(deletePlayer)) {

                String[] playersName = gameRoom.getPlayersName();
                ArrayList<String> list = new ArrayList<>(Arrays.asList(playersName.clone()));

                list.remove(deletePlayer);
                gameRoom.setPlayersName((String[])list.toArray(new String[list.size()]));

                Main.setGameRoomList(gameRoomList);
                break;
            }
        }
    }

    public static boolean manageGameRoomValue(PlayCommand playCommand) {
        CopyOnWriteArrayList<GameRoom> gameRoomList = Main.getGameRoomList();
        String playPlayer = playCommand.getPlayer();


        Card card = playCommand.getCard();
        boolean loseFlag = false;


        for (GameRoom gameRoom : gameRoomList) {          //確定玩家的遊戲房間
            if (Arrays.asList(gameRoom.getPlayersName()).contains(playPlayer)) {
                int value;
                if(card.getRank() == 1){

                    if(card.getSuit() == Suit.SPADE){
                        gameRoom.setValue(0);
                    }
                    else{
                        if(gameRoom.getValue() + 1 > 99){
                            loseFlag = true;
                        }
                        else {
                            value = gameRoom.getValue() + 1;
                            gameRoom.setValue(value);
                        }
                    }
                }
                else if (card.getRank() == 2 || card.getRank() == 3 || (card.getRank() >= 6 && card.getRank() <= 9)) {

                    if(gameRoom.getValue() + card.getRank() > 99){
                        loseFlag = true;
                    }
                    else {
                        value = gameRoom.getValue() + card.getRank();
                        gameRoom.setValue(value);
                    }

                }
                else if(card.getRank() == 4){

                    GameRoom.Order order = gameRoom.getOrder();

                    if(order == GameRoom.Order.Clockwise){
                        gameRoom.setOrder(GameRoom.Order.Counterclockwise);
                    }
                    else if(order == GameRoom.Order.Counterclockwise){
                        gameRoom.setOrder(GameRoom.Order.Clockwise);
                    }

                }
                else if(card.getRank() == 10){

                    if(card.getSuit() == Suit.SPADE || card.getSuit() == Suit.CLUB){   //黑色+10
                        if(gameRoom.getValue() + 10 > 99){
                            loseFlag = true;
                        }
                        else{
                            value = gameRoom.getValue() + 10;
                            gameRoom.setValue(value);
                        }

                    }
                    else {                                                             //紅色-10
                        value = gameRoom.getValue() - 10;
                        gameRoom.setValue(value);
                    }


                }
                else if(card.getRank() == 12){

                    if(card.getSuit() == Suit.SPADE || card.getSuit() == Suit.CLUB){   //黑色+20
                        if(gameRoom.getValue() + 20 > 99){
                            loseFlag = true;
                        }
                        else {
                            value = gameRoom.getValue() + 20;
                            gameRoom.setValue(value);
                        }

                    }
                    else {                                                             //紅色-20
                        value = gameRoom.getValue() - 20;
                        gameRoom.setValue(value);
                    }
                }
                else if(card.getRank() == 13){
                    gameRoom.setValue(99);
                }

                break;
            }
        }

        return loseFlag;

    }
}
