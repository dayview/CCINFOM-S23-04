package businesspermitsystem.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    /**
     * The primary stage on which all scenes are displayed.
     */
    private final Stage stage;

    /**
     * Constructs a new SceneManager for the specified stage.
     * 
     * @param stage the JavaFX Stage used to display scenes
     */
    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    /**
     * Switches the currently displayed scene to a new one.
     * 
     * @param fxmlPath the classpath location of the FXML file to load
     * @param title the new title for the window
     */
    public void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene newScene = new Scene(loader.load());
            stage.setTitle(title);
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Switches the currently displayed scene to a new one.
     * 
     * @param fxmlPath the classpath location of the FXML file to load
     * @param title the new title for the window
     * @return the controller of the scene for possible passing of data.
     */
    public Object switchSceneWithController(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene newScene = new Scene(loader.load());
        stage.setTitle(title);
        stage.setScene(newScene);
        stage.show();
        return loader.getController();
    }
}
