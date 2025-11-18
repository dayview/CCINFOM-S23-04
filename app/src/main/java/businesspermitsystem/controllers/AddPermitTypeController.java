package businesspermitsystem.controllers;

import businesspermitsystem.db.FeeScheduleDAO;
import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.models.FeeScheduleModel;
import businesspermitsystem.models.PermitTypeModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;

/**
 * Controller for adding a new Permit Type record.
 * Links to FeeSchedule and validates permit type data.
 */
public class AddPermitTypeController {

    @FXML private TextField permitNameField;
    @FXML private ComboBox<String> feeScheduleComboBox;
    @FXML private TextField validityMonthsField;
    @FXML private TextArea documentRequirementsArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private PermitTypeDAO permitTypeDAO;
    private FeeScheduleDAO feeScheduleDAO;
    private List<FeeScheduleModel> feeSchedules;

    /**
     * Initialize controller - load fee schedules into ComboBox.
     */
    @FXML
    public void initialize() {
        permitTypeDAO = new PermitTypeDAO();
        feeScheduleDAO = new FeeScheduleDAO();

        validityMonthsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                validityMonthsField.setText(oldVal);
            }
        });

        loadFeeSchedules();
    }

    /**
     * Loads all fee schedules into the ComboBox.
     */
    private void loadFeeSchedules() {
        feeSchedules = feeScheduleDAO.getAllFeeSchedules();
        feeScheduleComboBox.getItems().clear();

        for (FeeScheduleModel fee : feeSchedules) {
            String displayText = String.format("ID: %d - Base Fee: %.2f - Validity: %d months",
                    fee.getID(), fee.getBaseFee(), fee.getValidityMonths());
            feeScheduleComboBox.getItems().add(displayText);
        }
    }

    /**
     * Handles Save button click - validates and creates new PermitType.
     */
    @FXML
    private void onSave() {
        if (!validateInput()) {
            return;
        }

        try {
            String permitName = permitNameField.getText().trim();
            int validityMonths = Integer.parseInt(validityMonthsField.getText().trim());
            String documentRequirements = documentRequirementsArea.getText().trim();

            int selectedIndex = feeScheduleComboBox.getSelectionModel().getSelectedIndex();
            FeeScheduleModel selectedFeeSchedule = feeSchedules.get(selectedIndex);

            PermitTypeModel permitType = new PermitTypeModel(
                    0,
                    permitName,
                    selectedFeeSchedule,
                    documentRequirements,
                    validityMonths
            );

            boolean success = permitTypeDAO.addPermitType(permitType);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit Type added successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Permit Type to database.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Permit Type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates all input fields before saving.
     */
    private boolean validateInput() {
        if (permitNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Permit Name is required.");
            return false;
        }

        if (feeScheduleComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Fee Schedule must be selected.");
            return false;
        }

        if (validityMonthsField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Validity (Months) is required.");
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
