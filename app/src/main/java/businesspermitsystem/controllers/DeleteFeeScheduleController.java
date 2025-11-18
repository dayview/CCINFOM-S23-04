package businesspermitsystem.controllers;

import businesspermitsystem.db.FeeScheduleDAO;
import businesspermitsystem.models.FeeScheduleModel;
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
 * Controller for deleting Fee Schedule records.
 * Searches by ID and confirms before deletion.
 */
public class DeleteFeeScheduleController {

    @FXML private TextField feeScheduleIdField;
    @FXML private Label baseFeeLabel;
    @FXML private Label surchargeRuleLabel;
    @FXML private Label validityMonthsLabel;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private FeeScheduleDAO feeScheduleDAO;
    private FeeScheduleModel currentFeeSchedule;

    /**
     * Initialize controller - set up DAO and disable delete button.
     */
    @FXML
    public void initialize() {
        feeScheduleDAO = new FeeScheduleDAO();
        deleteButton.setDisable(true);

        feeScheduleIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                feeScheduleIdField.setText(oldVal);
            }
        });
    }

    /**
     * Handles Search button - loads Fee Schedule by ID.
     */
    @FXML
    private void onSearch() {
        if (feeScheduleIdField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a Fee Schedule ID.");
            return;
        }

        try {
            int feeScheduleId = Integer.parseInt(feeScheduleIdField.getText().trim());
            currentFeeSchedule = feeScheduleDAO.getFeeScheduleByID(feeScheduleId);

            if (currentFeeSchedule != null) {
                displayFeeScheduleInfo();
                deleteButton.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Fee Schedule found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Fee Schedule ID not found.");
                clearDisplay();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Displays the fee schedule information.
     */
    private void displayFeeScheduleInfo() {
        baseFeeLabel.setText(String.valueOf(currentFeeSchedule.getBaseFee()));
        surchargeRuleLabel.setText(currentFeeSchedule.getSurchargeRule());
        validityMonthsLabel.setText(String.valueOf(currentFeeSchedule.getValidityMonths()));
    }

    /**
     * Handles Delete button - confirms and deletes the record.
     */
    @FXML
    private void onDelete() {
        if (currentFeeSchedule == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please search and load a Fee Schedule first.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Fee Schedule?");
        confirmAlert.setContentText("Are you sure you want to delete Fee Schedule ID " +
                currentFeeSchedule.getID() + "? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = feeScheduleDAO.deleteFeeSchedule(currentFeeSchedule.getID());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Fee Schedule deleted successfully!");
                    closeWindow();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete Fee Schedule.");
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
        baseFeeLabel.setText("-");
        surchargeRuleLabel.setText("-");
        validityMonthsLabel.setText("-");
        currentFeeSchedule = null;
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
