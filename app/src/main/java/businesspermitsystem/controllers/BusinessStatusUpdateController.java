package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.services.BusinessStatusUpdateService;
import businesspermitsystem.services.BusinessStatusUpdateService.StatusUpdateResult;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller for Business Status Update Transaction.
 * Handles UI interactions and delegates business logic to BusinessStatusUpdateService.
 */
public class BusinessStatusUpdateController {
    @FXML private TextField businessIdField;
    @FXML private Button searchButton;
    @FXML private Label businessNameLabel;
    @FXML private Label tradeNameLabel;
    @FXML private Label currentStatusLabel;
    @FXML private Label ownersLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker effectiveDatePicker;
    @FXML private TextArea reasonTextArea;
    @FXML private TextField supportDocField;
    @FXML private ComboBox<String> channelComboBox;
    @FXML private TextField changedByField;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    private final BusinessDAO businessDAO = new BusinessDAO();
    private final BusinessStatusUpdateService statusUpdateService = new BusinessStatusUpdateService();
    private BusinessModel currentBusiness;

    /**
     * Search for business by ID and display information
     */
    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        String businessIdText = businessIdField.getText().trim();

        if (businessIdText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a Business ID");
            return;
        }

        int businessId;
        try {
            businessId = Integer.parseInt(businessIdText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Business ID must be a valid number");
            return;
        }

        try {
            currentBusiness = businessDAO.getBusinessByID(businessId);

            if (currentBusiness == null) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Business not found with ID: " + businessId);
                clearBusinessDisplay();
                return;
            }

            displayBusinessInfo(currentBusiness);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve business: " + e.getMessage());
        }
    }

    /**
     * Execute the Business Status Update Transaction
     */
    @FXML
    private void onUpdateButtonClick(ActionEvent event) {
        if (currentBusiness == null) {
            showAlert(Alert.AlertType.WARNING, "No Business Selected", "Please search for a business first");
            return;
        }

        String validationError = validateInputs();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", validationError);
            return;
        }

        int businessId = currentBusiness.getBusinessId();
        String newStatus = statusComboBox.getValue();
        LocalDate effectiveDate = effectiveDatePicker.getValue();
        String reason = reasonTextArea.getText().trim();
        String supportDoc = supportDocField.getText().trim();
        String channel = channelComboBox.getValue();
        String changedBy = changedByField.getText().trim();

        StatusUpdateResult result = statusUpdateService.updateBusinessStatus(
                businessId, newStatus, effectiveDate, reason, supportDoc, changedBy, channel
        );

        if (result.isSuccess()) {
            showAlert(Alert.AlertType.INFORMATION, "Success", result.getMessage());
            clearForm();
            clearBusinessDisplay();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", result.getMessage());
        }
    }

    /**
     * Cancel and return to main menu
     */
    @FXML
    private void onCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * Display business information in the UI
     */
    private void displayBusinessInfo(BusinessModel business) {
        businessNameLabel.setText(business.getBusinessName());
        tradeNameLabel.setText(business.getTradeName());
        currentStatusLabel.setText(business.getStatus());

        // TODO: Fetch and display owner names when business_owner junction table is available
        ownersLabel.setText("(Owner info not yet implemented)");
    }

    /**
     * Clear business information display
     */
    private void clearBusinessDisplay() {
        businessNameLabel.setText("-");
        tradeNameLabel.setText("-");
        currentStatusLabel.setText("-");
        ownersLabel.setText("-");
        currentBusiness = null;
    }

    /**
     * Validate all required input fields
     * @return error message if validation fails, null if all valid
     */
    private String validateInputs() {
        if (statusComboBox.getValue() == null || statusComboBox.getValue().isEmpty()) {
            return "Please select a new status";
        }

        if (effectiveDatePicker.getValue() == null) {
            return "Please select an effective date";
        }

        if (reasonTextArea.getText().trim().isEmpty()) {
            return "Please provide a reason for the status change";
        }

        if (channelComboBox.getValue() == null || channelComboBox.getValue().isEmpty()) {
            return "Please select a notification channel";
        }

        if (changedByField.getText().trim().isEmpty()) {
            return "Please enter the staff username who is making this change";
        }

        return null;
    }

    /**
     * Clear the update form
     */
    private void clearForm() {
        businessIdField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        effectiveDatePicker.setValue(null);
        reasonTextArea.clear();
        supportDocField.clear();
        channelComboBox.setValue("email");
        changedByField.clear();
    }

    /**
     * Display alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
