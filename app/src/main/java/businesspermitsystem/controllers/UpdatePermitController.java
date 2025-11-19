package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitDAO;
import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.models.PermitModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Controller for updating existing Permit records.
 * Searches by ID, loads data, and saves modifications.
 */
public class UpdatePermitController {

    @FXML private TextField permitIdField;
    @FXML private TextField businessIdField;
    @FXML private ComboBox<String> permitTypeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker statusEffectiveDatePicker;
    @FXML private DatePicker validityStartPicker;
    @FXML private DatePicker validityEndPicker;
    @FXML private TextArea noteArea;
    @FXML private Button searchButton;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;

    private PermitDAO permitDAO;
    private PermitTypeDAO permitTypeDAO;
    private PermitModel currentPermit;
    private List<PermitTypeModel> permitTypes;

    /**
     * Initialize controller - set up DAOs and validation.
     */
    @FXML
    public void initialize() {
        permitDAO = new PermitDAO();
        permitTypeDAO = new PermitTypeDAO();

        setFieldsDisabled(true);

        loadPermitTypes();

        permitIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                permitIdField.setText(oldVal);
            }
        });

        businessIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                businessIdField.setText(oldVal);
            }
        });
    }

    /**
     * Loads all permit types into the ComboBox.
     */
    private void loadPermitTypes() {
        permitTypes = permitTypeDAO.getAllPermitTypes();
        permitTypeComboBox.getItems().clear();

        for (PermitTypeModel type : permitTypes) {
            String displayText = String.format("ID: %d - %s (Validity: %d months)",
                    type.getID(), type.getName(), type.getValidityMonths());
            permitTypeComboBox.getItems().add(displayText);
        }
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
                loadPermitData();
                setFieldsDisabled(false);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Permit ID not found.");
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
        }
    }

    /**
     * Loads the current permit data into the form fields.
     */
    private void loadPermitData() {
        businessIdField.setText(String.valueOf(currentPermit.getBusinessID()));
        statusComboBox.setValue(currentPermit.getStatus());
        noteArea.setText(currentPermit.getNote());

        if (currentPermit.getStatusEffectiveDate() != null) {
            LocalDate localDate = currentPermit.getStatusEffectiveDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            statusEffectiveDatePicker.setValue(localDate);
        }

        if (currentPermit.getValidityStart() != null) {
            LocalDate validityStartLocal = currentPermit.getValidityStart()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            validityStartPicker.setValue(validityStartLocal);
        }

        if (currentPermit.getValidityEnd() != null) {
            LocalDate validityEndLocal = currentPermit.getValidityEnd()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            validityEndPicker.setValue(validityEndLocal);
        }

        int currentPermitTypeId = currentPermit.getPermitTypeID();
        for (int i = 0; i < permitTypes.size(); i++) {
            if (permitTypes.get(i).getID() == currentPermitTypeId) {
                permitTypeComboBox.getSelectionModel().select(i);
                break;
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
            int businessId = Integer.parseInt(businessIdField.getText().trim());
            String status = statusComboBox.getValue();
            LocalDate effectiveDate = statusEffectiveDatePicker.getValue();
            String note = noteArea.getText().trim();

            int selectedIndex = permitTypeComboBox.getSelectionModel().getSelectedIndex();
            PermitTypeModel selectedPermitType = permitTypes.get(selectedIndex);
            int permitTypeId = selectedPermitType.getID();

            Date effectiveDateUtil = Date.from(effectiveDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Convert validity dates
            Date validityStart = null;
            if (validityStartPicker.getValue() != null) {
                validityStart = Date.from(validityStartPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            Date validityEnd = null;
            if (validityEndPicker.getValue() != null) {
                validityEnd = Date.from(validityEndPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            PermitModel updatedPermit = new PermitModel(
                    currentPermit.getPermitID(),
                    businessId,
                    permitTypeId,
                    status,
                    effectiveDateUtil,
                    note,
                    validityStart,
                    validityEnd
            );

            boolean success = permitDAO.updatePermit(updatedPermit);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit updated successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update Permit.");
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
        if (currentPermit == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please search and load a Permit first.");
            return false;
        }

        if (businessIdField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Business ID is required.");
            return false;
        }

        if (permitTypeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Permit Type must be selected.");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Status must be selected.");
            return false;
        }

        if (statusEffectiveDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Status Effective Date is required.");
            return false;
        }

        // Validate validity dates if both are provided
        if (validityStartPicker.getValue() != null && validityEndPicker.getValue() != null) {
            if (validityEndPicker.getValue().isBefore(validityStartPicker.getValue())) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Validity End Date cannot be before Validity Start Date.");
                return false;
            }
        }

        return true;
    }

    /**
     * Enables or disables input fields.
     */
    private void setFieldsDisabled(boolean disabled) {
        businessIdField.setDisable(disabled);
        permitTypeComboBox.setDisable(disabled);
        statusComboBox.setDisable(disabled);
        statusEffectiveDatePicker.setDisable(disabled);
        validityStartPicker.setDisable(disabled);
        validityEndPicker.setDisable(disabled);
        noteArea.setDisable(disabled);
        updateButton.setDisable(disabled);
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        businessIdField.clear();
        permitTypeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        statusEffectiveDatePicker.setValue(null);
        validityStartPicker.setValue(null);
        validityEndPicker.setValue(null);
        noteArea.clear();
        currentPermit = null;
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