package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("View/home.fxml"));
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

}