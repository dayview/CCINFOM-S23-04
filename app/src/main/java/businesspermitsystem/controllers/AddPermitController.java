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
 * Controller for adding a new Permit record.
 * Links Business to PermitType and sets permit status.
 */
public class AddPermitController {

    @FXML private TextField businessIdField;
    @FXML private ComboBox<String> permitTypeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker statusEffectiveDatePicker;
    @FXML private TextArea noteArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private PermitDAO permitDAO;
    private PermitTypeDAO permitTypeDAO;
    private List<PermitTypeModel> permitTypes;

    /**
     * Initialize controller - set default date and load permit types.
     */
    @FXML
    public void initialize() {
        permitDAO = new PermitDAO();
        permitTypeDAO = new PermitTypeDAO();

        statusEffectiveDatePicker.setValue(LocalDate.now());

        businessIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                businessIdField.setText(oldVal);
            }
        });

        loadPermitTypes();
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
     * Handles Save button click - validates and creates new Permit.
     */
    @FXML
    private void onSave() {
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

            PermitModel permit = new PermitModel(
                    0,
                    businessId,
                    permitTypeId,
                    status,
                    (java.sql.Date) effectiveDateUtil,
                    note
            );

            boolean success = permitDAO.addPermit(permit);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permit added successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Permit to database.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid Business ID.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save Permit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates all input fields before saving.
     */
    private boolean validateInput() {
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
