package businesspermitsystem.controllers;

import businesspermitsystem.db.DatabaseConnector;
import businesspermitsystem.utils.SceneManager;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
public class LoginController implements Initializable {

    @FXML private TextField urlField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button connectButton;
    @FXML private Button cancelButton;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding disableBinding = urlField.textProperty().isEmpty()
                        .or(usernameField.textProperty().isEmpty())
                        .or(passwordField.textProperty().isEmpty())
                        .or(isLoading);

        connectButton.disableProperty().bind(isLoading);
        cancelButton.disableProperty().bind(isLoading);
        urlField.disableProperty().bind(isLoading);
        usernameField.disableProperty().bind(isLoading);
        passwordField.disableProperty().bind(isLoading);
        progressIndicator.visibleProperty().bind(isLoading);

        urlField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateUrl(newVal);
        });

        passwordField.setOnKeyPressed(this::handleKeyPressed);

        urlField.setText("jdbc:mysql://localhost:3306/business_database");
    }

    private void validateUrl(String url) {
        if (!url.isEmpty() && !url.startsWith("jdbc:mysql://")) {
            showStatus("URL should start with 'jdbc:mysql://'", "warning");
        } else if (statusLabel.getStyle().contains("orange")) {
            clearStatus();
        }
    }

    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !connectButton.isDisabled()) {
            handleConnect(new ActionEvent());
        } else if (event.getCode() == KeyCode.ESCAPE) {
            handleCancel(new ActionEvent());
        }
    }

    @FXML
    private void handleConnect(ActionEvent event) {
        if (!validateInput()) {
            showAlert("Validation Error", "Please check your input fields.", Alert.AlertType.ERROR);
            return;
        }

        isLoading.set(true);
        showStatus("Connecting to database...", "info");

        Task<Connection> connectionTask = createConnectionTask();

        connectionTask.setOnSucceeded(e -> {
            isLoading.set(false);
            Connection conn = connectionTask.getValue();

            if (conn != null) {
                showStatus("Connected successfully!", "success");
                showAlert("Success", "Database connection established successfully.", Alert.AlertType.INFORMATION);

                try {
                    Stage currentStage = (Stage) connectButton.getScene().getWindow();
                    SceneManager sceneManager = new SceneManager(currentStage);
                    sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
                } catch (Exception ex) {
                    showAlert("Navigation Error", "Failed to load main view: " + ex.getMessage(),
                            Alert.AlertType.ERROR);
                    ex.printStackTrace();
                }
            } else {
                showStatus("Connection failed", "error");
            }
        });

        connectionTask.setOnFailed(e -> {
            isLoading.set(false);
            Throwable exception = connectionTask.getException();
            String errorMessage = exception.getMessage();

            if (errorMessage.contains("Access denied")) {
                errorMessage = "Invalid username or password";
            } else if (errorMessage.contains("Communications link failure")) {
                errorMessage = "Cannot connect to database server. Check if MySQL is running.";
            } else if (errorMessage.contains("Unknown database")) {
                errorMessage = "Database does not exist. Check the database name in URL.";
            }

            showStatus("Connection failed: " + errorMessage, "error");
            showAlert("Connection Error", "Failed to connection to database:\n" + errorMessage, Alert.AlertType.ERROR);
        });

        Thread connectionThread = new Thread(connectionTask);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private Task<Connection> createConnectionTask() {
        return new Task<>() {
            @Override
            protected Connection call() throws Exception {
                String url = urlField.getText().trim();
                String username = usernameField.getText().trim();
                String password = passwordField.getText();

                return DatabaseConnector.getConnection(url, username, password);
            }
        };
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        DatabaseConnector.closeConnection();
        Platform.exit();
    }

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
       try{
            DatabaseConnector.getConnection(url, username, password);

            if (DatabaseConnector.connection != null) {
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

    private boolean validateInput() {
        String url = urlField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (url.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (!url.startsWith("jdbc:mysql://")) {
            return false;
        }

        return true;
    }

    private void showStatus(String message, String type) {
        statusLabel.setText(message);

        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                break;
            case "warning":
                statusLabel.setStyle("-fx-text-fill: #f39c12;");
                break;
            default:
                statusLabel.setStyle("");
        }
    }

    private void clearStatus() {
        statusLabel.setText("");
        statusLabel.setStyle("");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Connection getConnection() {
        return DatabaseConnector.connection;
    }
}