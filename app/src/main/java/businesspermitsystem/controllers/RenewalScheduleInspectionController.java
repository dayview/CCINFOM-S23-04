package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class RenewalScheduleInspectionController {
 
    @FXML private ComboBox<String> renewalComboBox;
    @FXML private ComboBox<String> inspectorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Button scheduleButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();

    @FXML
    public void initialize() {
        loadRenewals();
        loadInspectors();
    }

    private void loadRenewals() {
        try {
            renewalComboBox.getItems().clear();
            
            List<BusinessModel> businesses = service.getAllBusinesses();
            for (BusinessModel b : businesses) {
                List<PermitRenewalApplicationModel> list = service.getRenewalsByBusiness(b.getBusinessId());
                for (PermitRenewalApplicationModel r : list) {
                    if ("paid".equals(r.getStatus())) {
                        renewalComboBox.getItems().add(r.getRenewalID() + " - Business " + r.getBusinessID());
                    }
                }
            }
        } catch (Exception e) {
            showError("Failed to load renewals: " + e.getMessage());
        }
    }

    private void loadInspectors() {
        try {
            inspectorComboBox.getItems().clear();
            // This should load from database - placeholder values for now
            inspectorComboBox.getItems().addAll(
                "1 - Juan Dela Cruz",
                "2 - Maria Santos",
                "3 - Pedro Reyes"
            );
        } catch (Exception e) {
            showError("Failed to load inspectors: " + e.getMessage());
        }
    }

    @FXML
    private void onSchedule(ActionEvent event) {
        try {
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            if (inspectorComboBox.getValue() == null) {
                showWarning("Please select an inspector");
                return;
            }
            
            if (datePicker.getValue() == null) {
                showWarning("Please select a date");
                return;
            }
            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate.isBefore(LocalDate.now())) {
                showWarning("Cannot schedule inspection in the past");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            int inspectorId = getId(inspectorComboBox.getValue());
            Date date = Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            boolean success = service.scheduleInspection(renewalId, inspectorId, date);
            
            if (success) {
                showInfo("Inspection scheduled for Renewal ID: " + renewalId + " on " + selectedDate);
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

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sm = new SceneManager(stage);
        sm.switchScene("/view/RenewalMenuView.fxml", "Permit Renewal Transaction");
    }

    private void clearFields() {
        renewalComboBox.setValue(null);
        inspectorComboBox.setValue(null);
        datePicker.setValue(null);
    }

    private int getId(String value) {
        return Integer.parseInt(value.split(" - ")[0]);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}