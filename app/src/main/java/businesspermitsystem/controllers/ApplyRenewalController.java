package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class ApplyRenewalController {

    @FXML private ComboBox<String> businessComboBox;
    @FXML private ComboBox<String> permitComboBox;
    @FXML private Label renewalFeeLabel;
    @FXML private Label surchargeLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TextArea detailsArea;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();
    private List<BusinessModel> businesses;
    private List<PermitModel> permits;

    @FXML
    public void initialize() {
        loadBusinesses();
        
        businessComboBox.setOnAction(e -> {
            if (businessComboBox.getValue() != null) {
                loadPermits();
            }
        });
        
        permitComboBox.setOnAction(e -> {
            if (permitComboBox.getValue() != null) {
                calculateFees();
            }
        });
    }

    private void loadBusinesses() {
        try {
            businesses = service.getAllBusinesses();
            businessComboBox.getItems().clear();
            
            for (BusinessModel b : businesses) {
                if ("Active".equals(b.getStatus())) {
                    businessComboBox.getItems().add(b.getBusinessId() + " - " + b.getBusinessName());
                }
            }
            
            if (businessComboBox.getItems().isEmpty()) {
                showInfo("No active businesses found.");
            }
        } catch (Exception e) {
            showError("Failed to load businesses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadPermits() {
        try {
            int businessId = getId(businessComboBox.getValue());
            permits = service.getPermitsByBusiness(businessId);
            permitComboBox.getItems().clear();
            
            if (permits == null || permits.isEmpty()) {
                showWarning("No permits found for this business");
                return;
            }
            
            for (PermitModel p : permits) {
                String status = p.getStatus() != null ? p.getStatus() : "unknown";
                permitComboBox.getItems().add(p.getPermitID() + " - Status: " + status);
            }
        } catch (Exception e) {
            showError("Failed to load permits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateFees() {
        try {
            int permitId = getId(permitComboBox.getValue());
            PermitModel permit = null;
            
            for (PermitModel p : permits) {
                if (p.getPermitID() == permitId) {
                    permit = p;
                    break;
                }
            }
            
            if (permit == null) {
                showError("Selected permit not found");
                return;
            }
            
            // Base renewal fee - should come from permit type
            double renewalFee = 5000.0;
            double surcharge = service.calculateSurcharge(permit, null);
            double total = renewalFee + surcharge;
            
            renewalFeeLabel.setText(String.format("₱%.2f", renewalFee));
            surchargeLabel.setText(String.format("₱%.2f", surcharge));
            totalAmountLabel.setText(String.format("₱%.2f", total));
            
            String expiryInfo = permit.getStatusEffectiveDate() != null ? 
                permit.getStatusEffectiveDate().toString() : "N/A";
            
            String details = String.format(
                "Business ID: %d\nPermit ID: %d\nCurrent Status: %s\nExpiry: %s\n\n" +
                "Renewal Fee: ₱%.2f\nSurcharge: ₱%.2f\nTotal Amount: ₱%.2f",
                permit.getBusinessID(),
                permit.getPermitID(),
                permit.getStatus(),
                expiryInfo,
                renewalFee, surcharge, total
            );
            detailsArea.setText(details);
            
        } catch (Exception e) {
            showError("Failed to calculate fees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onApply(ActionEvent event) {
        try {
            if (businessComboBox.getValue() == null) {
                showWarning("Please select a business");
                return;
            }
            
            if (permitComboBox.getValue() == null) {
                showWarning("Please select a permit");
                return;
            }
            
            int businessId = getId(businessComboBox.getValue());
            int permitId = getId(permitComboBox.getValue());
            
            int renewalId = service.applyForRenewal(businessId, permitId);
            
            if (renewalId > 0) {
                showInfo("Renewal application created successfully!\nRenewal ID: " + renewalId + 
                        "\n\nNext step: Record Payment");
                clearFields();
                loadBusinesses();
            } else {
                showError("Failed to create renewal application");
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
        businessComboBox.setValue(null);
        permitComboBox.setValue(null);
        permitComboBox.getItems().clear();
        renewalFeeLabel.setText("₱0.00");
        surchargeLabel.setText("₱0.00");
        totalAmountLabel.setText("₱0.00");
        detailsArea.clear();
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