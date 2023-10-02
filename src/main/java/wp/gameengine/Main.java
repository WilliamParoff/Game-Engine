package wp.gameengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL sceneSource = Objects.requireNonNull(Main.class.getResource("main.fxml"));

        FXMLLoader sceneLoader = new FXMLLoader(sceneSource);
        Scene scene = new Scene(sceneLoader.load());

        stage.setTitle("Game Engine");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
