package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for the Apply Renewal view.
 * Handles the first step of the renewal process: creating a renewal application.
 */
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

    /**
     * Initializes the controller and sets up event handlers.
     */
    @FXML
    public void initialize() {
        loadBusinesses();
        
        // Reload permits when business selection changes
        businessComboBox.setOnAction(e -> {
            if (businessComboBox.getValue() != null) {
                loadPermits();
                clearFeeLabels();
            }
        });
        
        // Calculate fees when permit selection changes
        permitComboBox.setOnAction(e -> {
            if (permitComboBox.getValue() != null) {
                calculateFees();
            }
        });
    }

    /**
     * Loads all active businesses into the business combo box.
     */
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

    /**
     * Loads all permits for the selected business.
     * Only shows permits that can be renewed (active or expired but not suspended).
     */
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
                String permitInfo = String.format("%d - Type: %d - Status: %s", 
                    p.getPermitID(), p.getPermitTypeID(), status);
                permitComboBox.getItems().add(permitInfo);
            }
        } catch (Exception e) {
            showError("Failed to load permits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calculates and displays renewal fees including surcharges.
     * Updates all fee labels and the details text area.
     */
    private void calculateFees() {
        try {
            int permitId = getId(permitComboBox.getValue());
            PermitModel permit = null;
            
            // Find the selected permit
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
            
            // Get permit type for base fee
            PermitTypeModel type = service.getPermitTypeByID(permit.getPermitTypeID());
            if (type == null) {
                showError("Permit type not found");
                return;
            }
            
            // Calculate fees
            double baseFee = type.getFeeSchedule().getBaseFee();
            double surcharge = service.calculateSurcharge(permit, type);
            double total = baseFee + surcharge;
            
            // Update labels
            renewalFeeLabel.setText(String.format("₱%.2f", baseFee));
            surchargeLabel.setText(String.format("₱%.2f", surcharge));
            totalAmountLabel.setText(String.format("₱%.2f", total));
            
            // Build details information
            StringBuilder details = new StringBuilder();
            details.append(String.format("Business ID: %d%n", permit.getBusinessID()));
            details.append(String.format("Permit ID: %d%n", permit.getPermitID()));
            details.append(String.format("Permit Type: %s%n", type.getName()));
            details.append(String.format("Current Status: %s%n", permit.getStatus()));
            
            if (permit.getValidityStart() != null) {
                details.append(String.format("Valid From: %tF%n", permit.getValidityStart()));
            }
            if (permit.getValidityEnd() != null) {
                details.append(String.format("Valid Until: %tF%n", permit.getValidityEnd()));
            }
            
            details.append(String.format("%nRenewal Fee: ₱%.2f%n", baseFee));
            details.append(String.format("Surcharge: ₱%.2f%n", surcharge));
            
            if (surcharge > 0) {
                details.append("(Late renewal penalty applied)%n");
            }
            
            details.append(String.format("Total Amount: ₱%.2f%n", total));
            details.append(String.format("%nValidity: %d months", type.getValidityMonths()));
            
            detailsArea.setText(details.toString());
            
        } catch (Exception e) {
            showError("Failed to calculate fees: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the Apply button click.
     * Creates a new renewal application and displays the renewal ID.
     */
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
                showInfo(String.format(
                    "Renewal application created successfully!%n%n" +
                    "Renewal ID: %d%n" +
                    "Total Amount Due: %s%n%n" +
                    "Next step: Record Payment", 
                    renewalId, totalAmountLabel.getText()
                ));
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
     * Clears all input fields and labels.
     */
    private void clearFields() {
        businessComboBox.setValue(null);
        permitComboBox.setValue(null);
        permitComboBox.getItems().clear();
        clearFeeLabels();
        detailsArea.clear();
    }

    /**
     * Clears all fee display labels.
     */
    private void clearFeeLabels() {
        renewalFeeLabel.setText("₱0.00");
        surchargeLabel.setText("₱0.00");
        totalAmountLabel.setText("₱0.00");
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
}