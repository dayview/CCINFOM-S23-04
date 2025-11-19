package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.db.InspectionScheduleDAO;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Finalize Renewal view.
 * Handles the final step of the renewal process: recording inspection results and finalizing the renewal.
 */
public class FinalizeRenewalController {

    @FXML private ComboBox<String> renewalComboBox;
    @FXML private ComboBox<String> scheduleComboBox;
    @FXML private ComboBox<String> resultComboBox;
    @FXML private TextArea remarksArea;
    @FXML private Button finalizeButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();
    private InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO();
    private List<InspectionScheduleModel> schedules;

    /**
     * Initializes the controller and sets up event handlers.
     */
    @FXML
    public void initialize() {
        // Load result options
        resultComboBox.getItems().addAll("PASS", "FAIL");
        
        loadRenewals();
        
        // Load schedules when renewal selection changes
        renewalComboBox.setOnAction(e -> {
            if (renewalComboBox.getValue() != null) {
                loadSchedules();
            }
        });
    }

    /**
     * Loads all paid renewal applications that have inspections scheduled.
     */
    private void loadRenewals() {
        try {
            renewalComboBox.getItems().clear();
            
            List<BusinessModel> businesses = service.getAllBusinesses();
            int count = 0;
            
            for (BusinessModel b : businesses) {
                List<PermitRenewalApplicationModel> list = service.getRenewalsByBusiness(b.getBusinessId());
                for (PermitRenewalApplicationModel r : list) {
                    // Only show paid renewals with scheduled inspections
                    if ("paid".equals(r.getStatus()) && service.isInspectionScheduled(r.getRenewalID())) {
                        String displayText = String.format(
                            "%d - %s (Business ID: %d) - ₱%.2f",
                            r.getRenewalID(),
                            b.getBusinessName(),
                            r.getBusinessID(),
                            r.getTotalAmount()
                        );
                        renewalComboBox.getItems().add(displayText);
                        count++;
                    }
                }
            }
            
            if (count == 0) {
                showInfo("No renewals with scheduled inspections found.");
            }
        } catch (Exception e) {
            showError("Failed to load renewals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads inspection schedules for the selected renewal's business.
     * Retrieves schedules that are not yet completed.
     */
    private void loadSchedules() {
        try {
            scheduleComboBox.getItems().clear();
            
            int renewalId = getId(renewalComboBox.getValue());
            PermitRenewalApplicationModel renewal = service.getRenewalByID(renewalId);
            
            if (renewal == null) {
                showError("Renewal not found");
                return;
            }
            
            // Get all schedules - pass null to get all non-completed schedules
            schedules = scheduleDAO.getFilteredSchedules(null);
            
            int count = 0;
            for (InspectionScheduleModel schedule : schedules) {
                // Filter schedules for this renewal's business that are in Scheduled status
                if (schedule.getBusinessID() == renewal.getBusinessID() && 
                    "Scheduled".equalsIgnoreCase(schedule.getStatus())) {
                    
                    String displayText = String.format(
                        "%d - Inspection on %s (Business ID: %d)",
                        schedule.getScheduleID(),
                        schedule.getInspectionDate(),
                        schedule.getBusinessID()
                    );
                    scheduleComboBox.getItems().add(displayText);
                    count++;
                }
            }
            
            // Auto-select the first schedule if available
            if (!scheduleComboBox.getItems().isEmpty()) {
                scheduleComboBox.getSelectionModel().selectFirst();
            }
            
            if (count == 0) {
                showWarning("No pending inspection schedules found for this renewal's business.");
            }
        } catch (Exception e) {
            showError("Failed to load schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Finalize button click.
     * Records the inspection result and updates the permit status accordingly.
     */
    @FXML
    private void onFinalize(ActionEvent event) {
        try {
            // Validate renewal selection
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            // Validate schedule selection
            if (scheduleComboBox.getValue() == null) {
                showWarning("Please select a schedule");
                return;
            }
            
            // Validate result selection
            if (resultComboBox.getValue() == null) {
                showWarning("Please select a result (PASS or FAIL)");
                return;
            }
            
            // Validate remarks
            if (remarksArea.getText().trim().isEmpty()) {
                showWarning("Please enter remarks about the inspection");
                return;
            }
            
            // Validate remarks length
            if (remarksArea.getText().trim().length() < 10) {
                showWarning("Please provide more detailed remarks (minimum 10 characters)");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            int scheduleId = getId(scheduleComboBox.getValue());
            String result = resultComboBox.getValue();
            String remarks = remarksArea.getText().trim();
            
            // Get renewal details for confirmation
            PermitRenewalApplicationModel renewal = service.getRenewalByID(renewalId);
            if (renewal == null) {
                showError("Renewal application not found");
                return;
            }
            
            // Confirm finalization with detailed information
            String resultMessage = "PASS".equals(result) 
                ? "The permit will be RENEWED and marked as active."
                : "The permit will be SUSPENDED due to failed inspection.";
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Finalization");
            confirmAlert.setHeaderText("Finalize Renewal Application");
            confirmAlert.setContentText(String.format(
                "Are you sure you want to finalize this renewal?%n%n" +
                "Renewal ID: %d%n" +
                "Business ID: %d%n" +
                "Result: %s%n%n" +
                "%s%n%n" +
                "This action cannot be undone.",
                renewalId, renewal.getBusinessID(), result, resultMessage
            ));
            
            ButtonType confirmButton = new ButtonType("Finalize", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmAlert.getButtonTypes().setAll(confirmButton, cancelButtonType);
            
            if (confirmAlert.showAndWait().orElse(cancelButtonType) != confirmButton) {
                return;
            }
            
            // Finalize the renewal
            boolean success = service.finalizeRenewal(renewalId, scheduleId, result, remarks);
            
            if (success) {
                String successMessage;
                if ("PASS".equals(result)) {
                    successMessage = String.format(
                        "Renewal APPROVED successfully!%n%n" +
                        "Renewal ID: %d%n" +
                        "Status: APPROVED%n%n" +
                        "The permit has been renewed and is now active.%n" +
                        "The validity period has been extended.",
                        renewalId
                    );
                } else {
                    successMessage = String.format(
                        "Renewal DENIED.%n%n" +
                        "Renewal ID: %d%n" +
                        "Status: DENIED%n%n" +
                        "The permit has been suspended due to failed inspection.%n" +
                        "The business must address the issues and reapply.",
                        renewalId
                    );
                }
                
                showSuccess(successMessage);
                clearFields();
                loadRenewals();
            } else {
                showError("Failed to finalize renewal. Please check the data and try again.");
            }
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Cancel button click.
     * Returns to the Renewal Menu.
     */
    @FXML
    private void onCancel(ActionEvent event) {
        // Confirm if there's unsaved data
        if (renewalComboBox.getValue() != null || 
            resultComboBox.getValue() != null || 
            !remarksArea.getText().trim().isEmpty()) {
            
            boolean proceed = showConfirmation(
                "You have unsaved changes. Are you sure you want to cancel?"
            );
            
            if (!proceed) {
                return;
            }
        }
        
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sm = new SceneManager(stage);
        sm.switchScene("/view/RenewalMenuView.fxml", "Permit Renewal Transaction");
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        renewalComboBox.setValue(null);
        scheduleComboBox.setValue(null);
        scheduleComboBox.getItems().clear();
        resultComboBox.setValue(null);
        remarksArea.clear();
    }

    /**
     * Extracts the ID from a combo box value string.
     * 
     * @param value the combo box value in format "ID - Description"
     * @return the extracted ID
     */
    private int getId(String value) {
        return Integer.parseInt(value.split(" - ")[0]);
    }

    /**
     * Displays an error alert dialog.
     * 
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert dialog.
     * 
     * @param message the warning message to display
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert dialog.
     * 
     * @param message the information message to display
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a success alert dialog.
     * 
     * @param message the success message to display
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Renewal Finalized");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog.
     * 
     * @param message the confirmation message
     * @return true if user clicks OK, false otherwise
     */
    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}