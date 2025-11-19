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

/**
 * Controller for the Record Payment view.
 * Handles the second step of the renewal process: recording payment for pending renewals.
 */
public class RecordPaymentController {

    @FXML private ComboBox<String> renewalComboBox;
    @FXML private Label amountDueLabel;
    @FXML private TextField paymentField;
    @FXML private ComboBox<String> methodComboBox;
    @FXML private Button recordButton;
    @FXML private Button cancelButton;

    private PermitRenewalService service = new PermitRenewalService();
    private List<PermitRenewalApplicationModel> renewals = new ArrayList<>();

    /**
     * Initializes the controller and sets up event handlers.
     */
    @FXML
    public void initialize() {
        // Load payment methods
        methodComboBox.getItems().addAll(
            "Cash", "Check", "Credit Card", "Debit Card", 
            "Bank Transfer", "GCash", "PayMaya", "Online Banking"
        );
        
        loadRenewals();
        
        // Update amount due when renewal selection changes
        renewalComboBox.setOnAction(e -> {
            if (renewalComboBox.getValue() != null) {
                showAmount();
            }
        });
    }

    /**
     * Loads all pending renewal applications that need payment.
     */
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
                        String displayText = String.format(
                            "%d - Business %d (%s) - ₱%.2f",
                            r.getRenewalID(), 
                            r.getBusinessID(),
                            b.getBusinessName(),
                            r.getTotalAmount()
                        );
                        renewalComboBox.getItems().add(displayText);
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

    /**
     * Displays the amount due for the selected renewal.
     * Also pre-fills the payment field with the exact amount.
     */
    private void showAmount() {
        try {
            int renewalId = getId(renewalComboBox.getValue());
            
            for (PermitRenewalApplicationModel r : renewals) {
                if (r.getRenewalID() == renewalId) {
                    double totalAmount = r.getTotalAmount();
                    amountDueLabel.setText(String.format("₱%.2f", totalAmount));
                    paymentField.setText(String.format("%.2f", totalAmount));
                    break;
                }
            }
        } catch (Exception e) {
            showError("Error displaying amount: " + e.getMessage());
        }
    }

    /**
     * Handles the Record button click.
     * Records the payment and updates the renewal status to 'paid'.
     */
    @FXML
    private void onRecord(ActionEvent event) {
        try {
            // Validate renewal selection
            if (renewalComboBox.getValue() == null) {
                showWarning("Please select a renewal");
                return;
            }
            
            // Validate payment amount
            if (paymentField.getText().isEmpty()) {
                showWarning("Please enter payment amount");
                return;
            }
            
            // Validate payment method
            if (methodComboBox.getValue() == null) {
                showWarning("Please select payment method");
                return;
            }
            
            int renewalId = getId(renewalComboBox.getValue());
            double amount;
            
            try {
                amount = Double.parseDouble(paymentField.getText());
            } catch (NumberFormatException e) {
                showError("Invalid amount format. Please enter a valid number.");
                return;
            }
            
            // Validate amount is positive
            if (amount <= 0) {
                showError("Payment amount must be greater than zero");
                return;
            }
            
            String method = methodComboBox.getValue();
            
            // Find the renewal to check amount due
            PermitRenewalApplicationModel renewal = null;
            for (PermitRenewalApplicationModel r : renewals) {
                if (r.getRenewalID() == renewalId) {
                    renewal = r;
                    break;
                }
            }
            
            // Validate sufficient payment
            if (renewal != null && amount < renewal.getTotalAmount()) {
                boolean proceed = showConfirmation(String.format(
                    "Payment amount (₱%.2f) is less than amount due (₱%.2f).%n%n" +
                    "Insufficient payment will not be accepted. Do you want to correct the amount?", 
                    amount, renewal.getTotalAmount()
                ));
                
                if (proceed) {
                    paymentField.requestFocus();
                    paymentField.selectAll();
                }
                return;
            }
            
            // Record payment
            boolean success = service.recordPayment(renewalId, amount, method);
            
            if (success) {
                String changeMsg = "";
                if (renewal != null && amount > renewal.getTotalAmount()) {
                    double change = amount - renewal.getTotalAmount();
                    changeMsg = String.format("%nChange: ₱%.2f", change);
                }
                
                showInfo(String.format(
                    "Payment recorded successfully!%n%n" +
                    "Renewal ID: %d%n" +
                    "Amount Paid: ₱%.2f%n" +
                    "Payment Method: %s%s%n%n" +
                    "Status: PAID%n" +
                    "Next step: Schedule Inspection", 
                    renewalId, amount, method, changeMsg
                ));
                clearFields();
                loadRenewals();
            } else {
                showError("Failed to record payment");
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
        renewalComboBox.setValue(null);
        paymentField.clear();
        methodComboBox.setValue(null);
        amountDueLabel.setText("₱0.00");
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
        alert.setTitle("Payment Successful");
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
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}