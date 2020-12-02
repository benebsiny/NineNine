package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
