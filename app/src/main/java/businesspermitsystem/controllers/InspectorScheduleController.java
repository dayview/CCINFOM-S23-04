package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.db.InspectorScheduleDAO;
import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsible for scheduling inspections for paid permit applications.
 * Handles loading eligible applications, assigning inspectors, and creating schedules.
 */
public class InspectorScheduleController {

    @FXML private ComboBox<ApplicationEntry> applicationComboBox;
    @FXML private ComboBox<InspectorModel> inspectorComboBox;
    @FXML private DatePicker inspectionDatePicker;

    @FXML private Button scheduleButton;
    @FXML private Button cancelButton;

    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();
    private final InspectorDAO inspectorDAO = new InspectorDAO();
    private final InspectorScheduleDAO scheduleDAO = new InspectorScheduleDAO();

    /**
     * Helper class representing a selectable application entry consisting
     * of application ID, business ID, municipality ID, and business name.
     */
    public static class ApplicationEntry {
        public int applicationId;
        public int businessId;
        public int municipalityId;
        public String businessName;

        public ApplicationEntry(int appId, int bizId, int muniId, String businessName) {
            this.applicationId = appId;
            this.businessId = bizId;
            this.municipalityId = muniId;
            this.businessName = businessName;
        }

        @Override
        public String toString() {
            return businessName; // displayed in ComboBox
        }
    }

    /**
     * Initializes the controller by loading all applications eligible for scheduling.
     */
    @FXML
    public void initialize() {

        List<ApplicationEntry> entries = applicationDAO.getPaidApplicationsForScheduling();

        if (entries == null || entries.isEmpty()) {
            showAlert("No Applications", "There are no paid applications to schedule.", Alert.AlertType.INFORMATION);
        }

        applicationComboBox.getItems().addAll(entries);

        // Load inspectors when an application is selected
        applicationComboBox.setOnAction(e -> loadInspectors());
    }

    /**
     * Loads inspectors assigned to the municipality of the selected business.
     */
    private void loadInspectors() {
        inspectorComboBox.getItems().clear();

        ApplicationEntry selected = applicationComboBox.getValue();
        if (selected == null) return;

        try {
            List<InspectorModel> inspectors = inspectorDAO.getInspectorsByMunicipality(selected.municipalityId);

            inspectorComboBox.getItems().addAll(inspectors);

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to load inspectors.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Validates user input and attempts to create a new inspection schedule.
     */
    @FXML
    private void handleSchedule() {

        ApplicationEntry entry = applicationComboBox.getValue();
        InspectorModel inspector = inspectorComboBox.getValue();
        LocalDate date = inspectionDatePicker.getValue();

        if (entry == null || inspector == null || date == null) {
            showAlert("Missing Fields", "Please select an application, inspector, and date.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Check if inspector is available
            boolean available = inspectorDAO.isAvailable(inspector.getInspectorID(), date);
            if (!available) {
                showAlert("Inspector Busy", "The selected inspector already has a schedule on that date.", Alert.AlertType.WARNING);
                return;
            }

            // Save schedule
            boolean saved = scheduleDAO.createSchedule(
                    inspector.getInspectorID(),
                    entry.businessId,
                    date
            );

            if (saved) {
                showAlert("Success", "Inspection schedule created successfully!", Alert.AlertType.INFORMATION);

                Stage stage = (Stage) scheduleButton.getScene().getWindow();
                new SceneManager(stage).switchScene("/view/MainView.fxml", "Main Menu");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save inspection schedule.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Returns to the previous menu.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/InitialPermitMenuView.fxml", "Main Menu");
    }

    /**
     * Displays an alert dialog with the given parameters.
     *
     * @param title alert title
     * @param msg   alert message
     * @param type  alert type (information, warning, error)
     */
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

