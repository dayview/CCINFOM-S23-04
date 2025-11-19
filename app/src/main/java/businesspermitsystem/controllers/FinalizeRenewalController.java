package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class FinalizeRenewalController {

    @FXML private ComboBox<String> renewalComboBox;
    @FXML private ComboBox<String> scheduleComboBox;
    @FXML private ComboBox<String> resultComboBox;
    @FXML private TextArea remarksArea;
    @FXML private Button finalizeButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();

    @FXML
    public void initialize() {
        resultComboBox.getItems().addAll("PASS", "FAIL");
        loadRenewals();
        
        renewalComboBox.setOnAction(e -> {
            if (renewalComboBox.getValue() != null) {
                loadSchedules();
            }
        });
    }

    private void loadRenewals() {
        try {
            renewalComboBox.getItems().clear();
            
            List<BusinessModel> businesses = service.getAllBusinesses();
            for (BusinessModel b : businesses) {
                List<PermitRenewalApplicationModel> list = service.getRenewalsByBusiness(b.getBusinessID());
                for (PermitRenewalApplicationModel r : list) {
                    if ("paid".equals(r.getStatus()) && service.isInspectionScheduled(r.getRenewalID())) {
                        renewalComboBox.getItems().add(r.getRenewalID() + " - Business " + r.getBusinessID());
                    }
                }
            }
        } catch (Exception e) {
            showError("Failed to load renewals: " + e.getMessage());
        }
    }

    private void loadSchedules() {
        scheduleComboBox.getItems().clear();
        scheduleComboBox.getItems().addAll(
            "1 - Schedule 2024-01-15",
            "2 - Schedule 2024-01-20"
        );
    }

    @FXML
    private void onFinalize(ActionEvent event) {
        try {
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            if (scheduleComboBox.getValue() == null) {
                showWarning("Please select a schedule");
                return;
            }
            
            if (resultComboBox.getValue() == null) {
                showWarning("Please select a result");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            int scheduleId = getId(scheduleComboBox.getValue());
            String result = resultComboBox.getValue();
            String remarks = remarksArea.getText();
            
            boolean success = service.finalizeRenewal(renewalId, scheduleId, result, remarks);
            
            if (success) {
                String msg = "PASS".equals(result) ? 
                    "Renewal approved! Permit renewed." : 
                    "Renewal denied. Permit suspended.";
                showInfo(msg);
                clearFields();
                loadRenewals();
            } else {
                showError("Failed to finalize renewal");
            }
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sm = new SceneManager(stage);
        sm.switchScene("/view/MainView.fxml", "Main Menu");
    }

    private void clearFields() {
        renewalComboBox.setValue(null);
        scheduleComboBox.setValue(null);
        resultComboBox.setValue(null);
        remarksArea.clear();
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