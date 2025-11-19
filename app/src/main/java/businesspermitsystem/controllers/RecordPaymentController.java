package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class RecordPaymentController {

    @FXML private ComboBox<String> renewalComboBox;
    @FXML private Label amountDueLabel;
    @FXML private TextField paymentField;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private Button recordButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();
    private List<PermitRenewalApplicationModel> renewals = new ArrayList<>();

    @FXML
    public void initialize() {
        methodComboBox.getItems().addAll("Cash", "Check", "Credit Card", "Bank Transfer", "GCash", "PayMaya");
        loadRenewals();
        
        renewalComboBox.setOnAction(e -> {
            if (renewalComboBox.getValue() != null) {
                showAmount();
            }
        });
    }

    private void loadRenewals() {
        try {
            renewalComboBox.getItems().clear();
            renewals.clear();
            
            List<BusinessModel> businesses = service.getAllBusinesses();
            for (BusinessModel b : businesses) {
                List<PermitRenewalApplicationModel> list = service.getRenewalsByBusiness(b.getBusinessId());
                for (PermitRenewalApplicationModel r : list) {
                    if ("pending".equals(r.getStatus())) {
                        renewals.add(r);
                        renewalComboBox.getItems().add(r.getRenewalID() + " - Business " + r.getBusinessID());
                    }
                }
            }
            
            if (renewalComboBox.getItems().isEmpty()) {
                showInfo("No pending renewals found. Please apply for renewal first.");
            }
        } catch (Exception e) {
            showError("Failed to load renewals: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAmount() {
        try {
            int renewalId = getId(renewalComboBox.getValue());
            
            for (PermitRenewalApplicationModel r : renewals) {
                if (r.getRenewalID() == renewalId) {
                    amountDueLabel.setText(String.format("₱%.2f", r.getTotalAmount()));
                    paymentField.setText(String.format("%.2f", r.getTotalAmount()));
                    break;
                }
            }
        } catch (Exception e) {
            showError("Error displaying amount: " + e.getMessage());
        }
    }

    @FXML
    private void onRecord(ActionEvent event) {
        try {
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            if (paymentField.getText().isEmpty()) {
                showWarning("Please enter payment amount");
                return;
            }
            
            if (methodComboBox.getValue() == null) {
                showWarning("Please select payment method");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            double amount = Double.parseDouble(paymentField.getText());
            String method = methodComboBox.getValue();
            
            // Find the renewal to check amount due
            PermitRenewalApplicationModel renewal = null;
            for (PermitRenewalApplicationModel r : renewals) {
                if (r.getRenewalID() == renewalId) {
                    renewal = r;
                    break;
                }
            }
            
            if (renewal != null && amount < renewal.getTotalAmount()) {
                showWarning(String.format("Payment amount (₱%.2f) is less than amount due (₱%.2f)", 
                    amount, renewal.getTotalAmount()));
                return;
            }
            
            boolean success = service.recordPayment(renewalId, amount, method);
            
            if (success) {
                showInfo(String.format("Payment of ₱%.2f recorded for Renewal ID: %d", amount, renewalId));
                clearFields();
                loadRenewals();
            } else {
                showError("Failed to record payment");
            }
            
        } catch (NumberFormatException e) {
            showError("Invalid amount format. Please enter a valid number.");
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
        paymentField.clear();
        methodComboBox.setValue(null);
        amountDueLabel.setText("₱0.00");
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
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}