package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Optional;

/**
 * Controller for deleting Permit Type records.
 * Searches by ID and confirms before deletion.
 */
public class DeletePermitTypeController {

    @FXML private TextField permitTypeIdField;
    @FXML private Label permitNameLabel;
    @FXML private Label feeScheduleLabel;
    @FXML private Label validityMonthsLabel;
    @FXML private Label documentRequirementsLabel;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private PermitTypeDAO permitTypeDAO;
    private PermitTypeModel currentPermitType;

    /**
     * Initialize controller - set up DAO and disable delete button.
     */
    @FXML
    public void initialize() {
        permitTypeDAO = new PermitTypeDAO();
        deleteButton.setDisable(true);

        permitTypeIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                permitTypeIdField.setText(oldVal);
            }
        });
    }

    /**
     * Handles Search button - loads Permit Type by ID.
     */
    @FXML
    private void onSearch() {
        if (permitTypeIdField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a Permit Type ID.");
            return;
        }

        try {
            int permitTypeId = Integer.parseInt(permitTypeIdField.getText().trim());
            currentPermitType = permitTypeDAO.getPermitTypeByID(permitTypeId);

            if (currentPermitType != null) {
                displayPermitTypeInfo();
                deleteButton.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit Type found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Permit Type ID not found.");
                clearDisplay();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Displays the permit type information.
     */
    private void displayPermitTypeInfo() {
        permitNameLabel.setText(currentPermitType.getName());

        if (currentPermitType.getFeeSchedule() != null) {
            feeScheduleLabel.setText("ID: " + currentPermitType.getFeeSchedule().getID() +
                    " - Base Fee: " + currentPermitType.getFeeSchedule().getBaseFee());
        } else {
            feeScheduleLabel.setText("N/A");
        }

        validityMonthsLabel.setText(String.valueOf(currentPermitType.getValidityMonths()));
        documentRequirementsLabel.setText(currentPermitType.getDocumentRequirements());
    }

    /**
     * Handles Delete button - confirms and deletes the record.
     */
    @FXML
    private void onDelete() {
        if (currentPermitType == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please search and load a Permit Type first.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Permit Type?");
        confirmAlert.setContentText("Are you sure you want to delete Permit Type '" +
                currentPermitType.getName() + "'? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = permitTypeDAO.deletePermitType(currentPermitType.getID());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Permit Type deleted successfully!");
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete Permit Type.");
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
        permitNameLabel.setText("-");
        feeScheduleLabel.setText("-");
        validityMonthsLabel.setText("-");
        documentRequirementsLabel.setText("-");
        currentPermitType = null;
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
