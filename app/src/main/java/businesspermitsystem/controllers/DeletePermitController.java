package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitDAO;
import businesspermitsystem.models.PermitModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * Controller for deleting Permit records.
 * Searches by ID and confirms before deletion.
 */
public class DeletePermitController {

    @FXML private TextField permitIdField;
    @FXML private Label businessIdLabel;
    @FXML private Label permitTypeLabel;
    @FXML private Label statusLabel;
    @FXML private Label statusEffectiveDateLabel;
    @FXML private Label noteLabel;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private PermitDAO permitDAO;
    private PermitModel currentPermit;

    /**
     * Initialize controller - set up DAO and disable delete button.
     */
    @FXML
    public void initialize() {
        permitDAO = new PermitDAO();
        deleteButton.setDisable(true);

        permitIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                permitIdField.setText(oldVal);
            }
        });
    }

    /**
     * Handles Search button - loads Permit by ID.
     */
    @FXML
    private void onSearch() {
        if (permitIdField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a Permit ID.");
            return;
        }

        try {
            int permitId = Integer.parseInt(permitIdField.getText().trim());
            currentPermit = permitDAO.getPermitByID(permitId);

            if (currentPermit != null) {
                displayPermitInfo();
                deleteButton.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Permit ID not found.");
                clearDisplay();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Displays the permit information.
     */
    private void displayPermitInfo() {
        businessIdLabel.setText(String.valueOf(currentPermit.getBusinessID()));
        permitTypeLabel.setText(String.valueOf(currentPermit.getPermitTypeID()));
        statusLabel.setText(currentPermit.getStatus());

        if (currentPermit.getStatusEffectiveDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            statusEffectiveDateLabel.setText(sdf.format(currentPermit.getStatusEffectiveDate()));
        } else {
            statusEffectiveDateLabel.setText("N/A");
        }

        noteLabel.setText(currentPermit.getNote() != null ? currentPermit.getNote() : "N/A");
    }

    /**
     * Handles Delete button - confirms and deletes the record.
     */
    @FXML
    private void onDelete() {
        if (currentPermit == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please search and load a Permit first.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Permit?");
        confirmAlert.setContentText("Are you sure you want to delete Permit ID " +
                currentPermit.getPermitID() + "? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = permitDAO.deletePermit(currentPermit.getPermitID());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Permit deleted successfully!");
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete Permit.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears the display labels.
     */
    private void clearDisplay() {
        businessIdLabel.setText("-");
        permitTypeLabel.setText("-");
        statusLabel.setText("-");
        statusEffectiveDateLabel.setText("-");
        noteLabel.setText("-");
        currentPermit = null;
        deleteButton.setDisable(true);
    }

    /**
     * Handles Cancel button - closes the window.
     */
    @FXML
    private void onCancel() {
        closeWindow();
    }

    /**
     * Navigates back to the Main Menu
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit System");
    }

    /**
     * Shows an alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
