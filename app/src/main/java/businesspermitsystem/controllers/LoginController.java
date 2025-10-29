package businesspermitsystem.controllers;

import java.sql.Connection;
import java.sql.SQLException;

import businesspermitsystem.db.DatabaseConnector;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the SQL login screen.
 * 
 * This controller handles user input for database connection credentials,
 * tests the connection, and transitions to the main dashboard scene
 * upon successful login.
 * 
 * 
 * FXML Fields are automatically injected by the FXMLLoader.
 * 
 */
public class LoginController {

    /**
     * Text field for database URL input.
     */
    @FXML private TextField urlField;

    /**
     * Text field for database username input.
     */
    @FXML private TextField usernameField;
    
    /**
     * Password field for database password input.
     * */
    @FXML private TextField passwordField;

    /**
     * Button used to trigger database connection.
     * */
    @FXML private Button connectButton;

    /**
     * Handles the Connect button click event.
     * 
     * Attempts to establish a connection to the database using
     * the credentials provided by the user. If successful, the
     * application transitions to the main scene.
     */
    @FXML
    private void connectButtonPressed(ActionEvent event) {
        String url = urlField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
       try (Connection connection = DatabaseConnector.testConnection(url, username, password)) {
            if (connection != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Database connection successful!");
                Stage currentStage = (Stage) connectButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Connection Failed", "Error: " + e.getMessage());
        }
    }

    /**
     * 
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
