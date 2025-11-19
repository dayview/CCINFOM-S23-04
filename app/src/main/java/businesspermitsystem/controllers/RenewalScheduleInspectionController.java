package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Controller for the Schedule Inspection view.
 * Handles the third step of the renewal process: scheduling inspections for paid renewals.
 */
public class RenewalScheduleInspectionController {
 
    @FXML private ComboBox<String> renewalComboBox;
    @FXML private ComboBox<String> inspectorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Button scheduleButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();
    private InspectorDAO inspectorDAO = new InspectorDAO();
    private List<InspectorModel> inspectors;

    /**
     * Initializes the controller and loads data.
     */
    @FXML
    public void initialize() {
        loadRenewals();
        loadInspectors();
        
        // Set minimum date to today
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        // Check inspector availability when date changes
        datePicker.setOnAction(e -> {
            if (datePicker.getValue() != null && inspectorComboBox.getValue() != null) {
                checkInspectorAvailability();
            }
        });
    }

    /**
     * Loads all paid renewal applications that are ready for inspection scheduling.
     */
    private void loadRenewals() {
        try {
            renewalComboBox.getItems().clear();
            
            List<BusinessModel> businesses = service.getAllBusinesses();
            int count = 0;
            
            for (BusinessModel b : businesses) {
                List<PermitRenewalApplicationModel> list = service.getRenewalsByBusiness(b.getBusinessId());
                for (PermitRenewalApplicationModel r : list) {
                    // Only show paid renewals that don't have inspection scheduled yet
                    if ("paid".equals(r.getStatus()) && !service.isInspectionScheduled(r.getRenewalID())) {
                        String displayText = String.format(
                            "%d - %s (Business ID: %d)",
                            r.getRenewalID(),
                            b.getBusinessName(),
                            r.getBusinessID()
                        );
                        renewalComboBox.getItems().add(displayText);
                        count++;
                    }
                }
            }
            
            if (count == 0) {
                showInfo("No paid renewals awaiting inspection scheduling.");
            }
        } catch (Exception e) {
            showError("Failed to load renewals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads all active inspectors from the database.
     */
    private void loadInspectors() {
        try {
            inspectorComboBox.getItems().clear();
            inspectors = inspectorDAO.getInspectors();
            
            for (InspectorModel inspector : inspectors) {
                if (inspector.isActive()) {
                    String displayText = String.format(
                        "%d - %s %s (%s)",
                        inspector.getInspectorID(),
                        inspector.getFirstName(),
                        inspector.getLastName(),
                        inspector.getDesignation()
                    );
                    inspectorComboBox.getItems().add(displayText);
                }
            }
            
            if (inspectorComboBox.getItems().isEmpty()) {
                showWarning("No active inspectors found in the system.");
            }
        } catch (Exception e) {
            showError("Failed to load inspectors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if the selected inspector is available on the selected date.
     */
    private void checkInspectorAvailability() {
        try {
            int inspectorId = getId(inspectorComboBox.getValue());
            LocalDate date = datePicker.getValue();
            
            boolean available = inspectorDAO.isAvailable(inspectorId, date);
            
            if (!available) {
                showWarning(String.format(
                    "Inspector is already scheduled for an inspection on %s.%n" +
                    "Please select a different inspector or date.",
                    date
                ));
            }
        } catch (Exception e) {
            // Silently catch - this is just a helper check
            e.printStackTrace();
        }
    }

    /**
     * Handles the Schedule button click.
     * Creates an inspection schedule for the selected renewal.
     */
    @FXML
    private void onSchedule(ActionEvent event) {
        try {
            // Validate renewal selection
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            // Validate inspector selection
            if (inspectorComboBox.getValue() == null) {
                showWarning("Please select an inspector");
                return;
            }
            
            // Validate date selection
            if (datePicker.getValue() == null) {
                showWarning("Please select a date");
                return;
            }
            
            LocalDate selectedDate = datePicker.getValue();
            
            // Validate date is not in the past
            if (selectedDate.isBefore(LocalDate.now())) {
                showWarning("Cannot schedule inspection in the past");
                return;
            }
            
            // Validate date is not too far in the future (e.g., within 90 days)
            if (selectedDate.isAfter(LocalDate.now().plusDays(90))) {
                showWarning("Cannot schedule inspection more than 90 days in advance");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            int inspectorId = getId(inspectorComboBox.getValue());
            Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // Check inspector availability
            if (!inspectorDAO.isAvailable(inspectorId, selectedDate)) {
                showWarning(String.format(
                    "Inspector is already scheduled for another inspection on %s.%n" +
                    "Please select a different inspector or date.",
                    selectedDate
                ));
                return;
            }
            
            // Get inspector name for confirmation
            String inspectorName = inspectorComboBox.getValue().substring(
                inspectorComboBox.getValue().indexOf("-") + 2
            );
            
            // Confirm scheduling
            boolean proceed = showConfirmation(String.format(
                "Schedule inspection for:%n%n" +
                "Renewal ID: %d%n" +
                "Inspector: %s%n" +
                "Date: %s%n%n" +
                "Continue?",
                renewalId, inspectorName, selectedDate
            ));
            
            if (!proceed) {
                return;
            }
            
            // Schedule inspection
            boolean success = service.scheduleInspection(renewalId, inspectorId, date);
            
            if (success) {
                showInfo(String.format(
                    "Inspection scheduled successfully!%n%n" +
                    "Renewal ID: %d%n" +
                    "Inspector: %s%n" +
                    "Scheduled Date: %s%n%n" +
                    "Next step: Finalize Renewal after inspection",
                    renewalId, inspectorName, selectedDate
                ));
                clearFields();
                loadRenewals();
            } else {
                showError("Failed to schedule inspection");
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
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sm = new SceneManager(stage);
        sm.switchScene("/view/RenewalMenuView.fxml", "Permit Renewal Transaction");
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        renewalComboBox.setValue(null);
        inspectorComboBox.setValue(null);
        datePicker.setValue(null);
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
        alert.setTitle("Inspection Scheduled");
        alert.setHeaderText(null);
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
        alert.setTitle("Confirm Schedule");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}