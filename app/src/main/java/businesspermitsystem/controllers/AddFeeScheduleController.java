package businesspermitsystem.controllers;

import businesspermitsystem.db.FeeScheduleDAO;
import businesspermitsystem.models.FeeScheduleModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for adding a new Fee Schedule record.
 * Validates input and creates FeeScheduleModel through DAO.
 */
public class AddFeeScheduleController {

    @FXML private TextField baseFeeField;
    @FXML private TextField surchargeRuleField;
    @FXML private TextField validityMonthsField;
    @FXML private TextArea documentRequirementsArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private FeeScheduleDAO feeScheduleDAO;

    /**
     * Initialize controller - set up DAO and input validation.
     */
    @FXML
    public void initialize() {
        feeScheduleDAO = new FeeScheduleDAO();

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
     * Handles Save button click - validates and creates new FeeSchedule.
     */
    @FXML
    private void onSave() {
        if (!validateInput()) {
            return;
        }

        try {
            double baseFee = Double.parseDouble(baseFeeField.getText().trim());
            String surchargeRule = surchargeRuleField.getText().trim();
            int validityMonths = Integer.parseInt(validityMonthsField.getText().trim());
            String documentRequirements = documentRequirementsArea.getText().trim();

            FeeScheduleModel feeSchedule = new FeeScheduleModel(
                    0,
                    baseFee,
                    surchargeRule,
                    validityMonths,
                    documentRequirements
            );

            boolean success = feeScheduleDAO.addFeeSchedule(feeSchedule);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Fee Schedule added successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Fee Schedule to database.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Fee Schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates all input fields before saving.
     */
    private boolean validateInput() {
        if (baseFeeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Base Fee is required.");
            return false;
        }

        if (validityMonthsField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Validity (Months) is required.");
            return false;
        }

        if (surchargeRuleField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Surcharge Rule is required.");
            return false;
        }

        return true;
    }

    /**
     * Handles Cancel button click - closes the window.
     */
    @FXML
    private void onCancel() {
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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
