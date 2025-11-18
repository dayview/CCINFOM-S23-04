package businesspermitsystem.controllers;

import businesspermitsystem.db.FeeScheduleDAO;
import businesspermitsystem.models.FeeScheduleModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for updating existing Fee Schedule records.
 * Searches by ID, loads data, and saves modifications.
 */
public class UpdateFeeScheduleController {

    @FXML private TextField feeScheduleIdField;
    @FXML private TextField baseFeeField;
    @FXML private TextField surchargeRuleField;
    @FXML private TextField validityMonthsField;
    @FXML private TextArea documentRequirementsArea;
    @FXML private Button searchButton;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;

    private FeeScheduleDAO feeScheduleDAO;
    private FeeScheduleModel currentFeeSchedule;

    /**
     * Initialize controller - set up DAO and input validation.
     */
    @FXML
    public void initialize() {
        feeScheduleDAO = new FeeScheduleDAO();

        setFieldsDisabled(true);

        feeScheduleIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                feeScheduleIdField.setText(oldVal);
            }
        });

        baseFeeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                baseFeeField.setText(oldVal);
            }
        });

        validityMonthsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                validityMonthsField.setText(oldVal);
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
                loadFeeScheduleData();
                setFieldsDisabled(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Fee Schedule found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Fee Schedule ID not found.");
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Loads the current fee schedule data into the form fields.
     */
    private void loadFeeScheduleData() {
        baseFeeField.setText(String.valueOf(currentFeeSchedule.getBaseFee()));
        surchargeRuleField.setText(currentFeeSchedule.getSurchargeRule());
        validityMonthsField.setText(String.valueOf(currentFeeSchedule.getValidityMonths()));
        documentRequirementsArea.setText(currentFeeSchedule.getDocumentRequirements());
    }

    /**
     * Handles Update button - saves modifications to database.
     */
    @FXML
    private void onUpdate() {
        if (!validateInput()) {
            return;
        }

        try {
            // Update the current model with new values
            double baseFee = Double.parseDouble(baseFeeField.getText().trim());
            String surchargeRule = surchargeRuleField.getText().trim();
            int validityMonths = Integer.parseInt(validityMonthsField.getText().trim());
            String documentRequirements = documentRequirementsArea.getText().trim();

            FeeScheduleModel updatedFeeSchedule = new FeeScheduleModel(
                    currentFeeSchedule.getID(),
                    baseFee,
                    surchargeRule,
                    validityMonths,
                    documentRequirements
            );

            boolean success = feeScheduleDAO.updateFeeSchedule(updatedFeeSchedule);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Fee Schedule updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Fee Schedule.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates all input fields before updating.
     */
    private boolean validateInput() {
        if (currentFeeSchedule == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please search and load a Fee Schedule first.");
            return false;
        }

        if (baseFeeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Base Fee is required.");
            return false;
        }

        if (surchargeRuleField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Surcharge Rule is required.");
            return false;
        }

        if (validityMonthsField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Validity (Months) is required.");
            return false;
        }

        return true;
    }

    /**
     * Enables or disables input fields.
     */
    private void setFieldsDisabled(boolean disabled) {
        baseFeeField.setDisable(disabled);
        surchargeRuleField.setDisable(disabled);
        validityMonthsField.setDisable(disabled);
        documentRequirementsArea.setDisable(disabled);
        updateButton.setDisable(disabled);
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        baseFeeField.clear();
        surchargeRuleField.clear();
        validityMonthsField.clear();
        documentRequirementsArea.clear();
        currentFeeSchedule = null;
        setFieldsDisabled(true);
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
