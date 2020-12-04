package Client;

import Client.Status.WindowStatus;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;

public class Main extends Application {

    private static Stage stage;
    private static Socket socket;

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;
        Main.socket = new Socket("127.0.0.1", 8888);


        Parent root = FXMLLoader.load(getClass().getResource("View/Home.fxml"));
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


    /**
     * Change the scene of the application
     *
     * @param sceneName - Scene name
     */
    public static void switchScene(String sceneName) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("View/" + sceneName + ".fxml"));
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the scene, then show the message at the above of the application
     *
     * @param sceneName Scene Name
     * @param message   Message
     * @param severity  Severity of the message, it may be SUCCESS, ERROR
     */
    public static void switchScene(String sceneName, String message, WindowStatus.MessageSeverity severity) {
        WindowStatus.setWindowMessage(message, severity);
        switchScene(sceneName);
    }

    /**
     * Check if there's a message to show at the top of the scene.
     * Call this function at the initial of the loading scene.
     *
     * @param pane Pane of the scene
     */
    public static void checkMessage(AnchorPane pane) {
        if (WindowStatus.getWindowMessage() != null) {

            Pane messagePane = new Pane();

            switch (WindowStatus.getSeverity()) {
                case SUCCESS -> messagePane.setStyle("-fx-background-color: #55fa55");
                case ERROR -> messagePane.setStyle("-fx-background-color: #fa5555");
            }

            messagePane.setPrefHeight(25);
            messagePane.setPrefWidth(900);
            messagePane.setLayoutY(-26);
            Label label = new Label(WindowStatus.getWindowMessage());
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 2px; -fx-font-weight: bold");
            messagePane.getChildren().add(label);
            pane.getChildren().add(messagePane);

            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new KeyValue(messagePane.layoutYProperty(), 0)));
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(6000), new KeyValue(messagePane.layoutYProperty(), 0)));
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(7000), new KeyValue(messagePane.layoutYProperty(), -26)));

            timeline.play();

            WindowStatus.clear();
        }
    }

    /**
     * Get the socket which connect to server
     *
     * @return - Socket connected to server
     */
    public static Socket getServer() {
        return socket;
    }

    public static void setServer(Socket socket) {
        Main.socket = socket;
    }
}
