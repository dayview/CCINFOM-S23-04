package businesspermitsystem.controllers;

import businesspermitsystem.db.FeeScheduleDAO;
import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.models.FeeScheduleModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;

/**
 * Controller for updating existing Permit Type records.
 * Searches by ID, loads data, and saves modifications.
 */
public class UpdatePermitTypeController {

    @FXML private TextField permitTypeIdField;
    @FXML private TextField permitNameField;
    @FXML private ComboBox<String> feeScheduleComboBox;
    @FXML private TextField validityMonthsField;
    @FXML private TextArea documentRequirementsArea;
    @FXML private Button searchButton;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;

    private PermitTypeDAO permitTypeDAO;
    private FeeScheduleDAO feeScheduleDAO;
    private PermitTypeModel currentPermitType;
    private List<FeeScheduleModel> feeSchedules;

    /**
     * Initialize controller - set up DAOs and validation.
     */
    @FXML
    public void initialize() {
        permitTypeDAO = new PermitTypeDAO();
        feeScheduleDAO = new FeeScheduleDAO();

        setFieldsDisabled(true);

        loadFeeSchedules();

        permitTypeIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                permitTypeIdField.setText(oldVal);
            }
        });

        validityMonthsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                validityMonthsField.setText(oldVal);
            }
        });
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
                loadPermitTypeData();
                setFieldsDisabled(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit Type found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Permit Type ID not found.");
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Loads the current permit type data into the form fields.
     */
    private void loadPermitTypeData() {
        permitNameField.setText(currentPermitType.getName());
        validityMonthsField.setText(String.valueOf(currentPermitType.getValidityMonths()));
        documentRequirementsArea.setText(currentPermitType.getDocumentRequirements());

        FeeScheduleModel currentFee = currentPermitType.getFeeSchedule();
        if (currentFee != null) {
            for (int i = 0; i < feeSchedules.size(); i++) {
                if (feeSchedules.get(i).getID() == currentFee.getID()) {
                    feeScheduleComboBox.getSelectionModel().select(i);
                    break;
                }
            }
        }
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
            String permitName = permitNameField.getText().trim();
            int validityMonths = Integer.parseInt(validityMonthsField.getText().trim());
            String documentRequirements = documentRequirementsArea.getText().trim();

            int selectedIndex = feeScheduleComboBox.getSelectionModel().getSelectedIndex();
            FeeScheduleModel selectedFeeSchedule = feeSchedules.get(selectedIndex);

            PermitTypeModel updatedPermitType = new PermitTypeModel(
                    currentPermitType.getID(),
                    permitName,
                    selectedFeeSchedule,
                    documentRequirements,
                    validityMonths
            );

            boolean success = permitTypeDAO.updatePermitType(updatedPermitType);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit Type updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Permit Type.");
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
        if (currentPermitType == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please search and load a Permit Type first.");
            return false;
        }

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
     * Enables or disables input fields.
     */
    private void setFieldsDisabled(boolean disabled) {
        permitNameField.setDisable(disabled);
        feeScheduleComboBox.setDisable(disabled);
        validityMonthsField.setDisable(disabled);
        documentRequirementsArea.setDisable(disabled);
        updateButton.setDisable(disabled);
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        permitNameField.clear();
        feeScheduleComboBox.getSelectionModel().clearSelection();
        validityMonthsField.clear();
        documentRequirementsArea.clear();
        currentPermitType = null;
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
