/*package businesspermitsystem.controllers;

import businesspermitsystem.models.*;
import businesspermitsystem.services.PermitRenewalService;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Controller for Permit Renewal Transaction
 * Handles all 4 steps: Apply, Payment, Schedule Inspection, Finalize
 * Assigned to: DE LEON, Sofia Ysabela
 */
/*
public class PermitRenewalController {

    // Step 1: Apply for Renewal
    @FXML private ComboBox<String> businessComboBox;
    @FXML private ComboBox<String> permitComboBox;
    @FXML private Label renewalFeeLabel;
    @FXML private Label surchargeLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TextArea renewalDetailsArea;
    @FXML private Button applyButton;

    // Step 2: Payment Recording
    @FXML private ComboBox<String> renewalComboBox;
    @FXML private TextField paymentAmountField;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private Label paymentTotalLabel;
    @FXML private Button recordPaymentButton;

    // Step 3: Inspection Scheduling
    @FXML private ComboBox<String> paidRenewalComboBox;
    @FXML private ComboBox<String> inspectorComboBox;
    @FXML private DatePicker inspectionDatePicker;
    @FXML private Button scheduleInspectionButton;

    // Step 4: Finalize Renewal
    @FXML private ComboBox<String> scheduledRenewalComboBox;
    @FXML private ComboBox<String> inspectionScheduleComboBox;
    @FXML private ComboBox<String> inspectionResultComboBox;
    @FXML private TextArea inspectionRemarksArea;
    @FXML private Button finalizeButton;

    // Status Table
    @FXML private TableView<PermitRenewalApplicationModel> renewalStatusTable;
    @FXML private TableColumn<PermitRenewalApplicationModel, Integer> colRenewalId;
    @FXML private TableColumn<PermitRenewalApplicationModel, Integer> colBusinessId;
    @FXML private TableColumn<PermitRenewalApplicationModel, Date> colApplicationDate;
    @FXML private TableColumn<PermitRenewalApplicationModel, Double> colTotalAmount;
    @FXML private TableColumn<PermitRenewalApplicationModel, String> colStatus;

    @FXML private Button cancelButton;

    private final PermitRenewalService renewalService = new PermitRenewalService();
    
    // Data storage for ComboBox mapping
    private ObservableList<BusinessModel> businesses = FXCollections.observableArrayList();
    private ObservableList<PermitModel> permits = FXCollections.observableArrayList();
    private ObservableList<PermitRenewalApplicationModel> renewals = FXCollections.observableArrayList();
    private ObservableList<InspectorModel> inspectors = FXCollections.observableArrayList();
    private ObservableList<InspectionScheduleModel> schedules = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            // Initialize payment methods
            if (paymentMethodComboBox != null) {
                paymentMethodComboBox.getItems().addAll("Cash", "Check", "Credit Card", "Bank Transfer", "GCash", "PayMaya");
            }

            // Initialize inspection results
            if (inspectionResultComboBox != null) {
                inspectionResultComboBox.getItems().addAll("PASS", "FAIL");
            }

            // Load businesses
            loadBusinesses();

            // Load all renewals for status table
            loadRenewalsTable();

            // Setup table columns
            setupTableColumns();

            // Setup event listeners
            setupEventListeners();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        if (renewalStatusTable != null) {
            colRenewalId.setCellValueFactory(new PropertyValueFactory<>("renewalID"));
            colBusinessId.setCellValueFactory(new PropertyValueFactory<>("businessID"));
            colApplicationDate.setCellValueFactory(new PropertyValueFactory<>("applicationDate"));
            colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
    }

    private void setupEventListeners() {
        // When business is selected, load its permits
        if (businessComboBox != null) {
            businessComboBox.setOnAction(e -> loadPermitsForSelectedBusiness());
        }

        // When permit is selected, calculate fees
        if (permitComboBox != null) {
            permitComboBox.setOnAction(e -> calculateRenewalFees());
        }

        // When renewal is selected for payment, show amount due
        if (renewalComboBox != null) {
            renewalComboBox.setOnAction(e -> displayPaymentAmount());
        }

        // When paid renewal is selected, load its inspection schedules
        if (paidRenewalComboBox != null) {
            paidRenewalComboBox.setOnAction(e -> loadInspectors());
        }

        // When scheduled renewal is selected, load inspection schedules
        if (scheduledRenewalComboBox != null) {
            scheduledRenewalComboBox.setOnAction(e -> loadInspectionSchedules());
        }
    }

    // ========== STEP 1: APPLY FOR RENEWAL ==========

    private void loadBusinesses() {
        try {
            businesses.clear();
            List<BusinessModel> allBusinesses = renewalService.getAllBusinesses();
            
            // Filter only active businesses
            for (BusinessModel business : allBusinesses) {
                if ("Active".equalsIgnoreCase(business.getStatus())) {
                    businesses.add(business);
                }
            }

            if (businessComboBox != null) {
                businessComboBox.getItems().clear();
                for (BusinessModel business : businesses) {
                    businessComboBox.getItems().add(business.getBusinessID() + " - " + business.getBusinessName());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load businesses: " + e.getMessage());
        }
    }

    private void loadPermitsForSelectedBusiness() {
        if (businessComboBox.getValue() == null) return;

        try {
            int businessId = extractId(businessComboBox.getValue());
            permits.clear();
            permits.addAll(renewalService.getPermitsByBusiness(businessId));

            if (permitComboBox != null) {
                permitComboBox.getItems().clear();
                for (PermitModel permit : permits) {
                    permitComboBox.getItems().add(permit.getPermitID() + " - Permit #" + permit.getPermitNo());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load permits: " + e.getMessage());
        }
    }

    private void calculateRenewalFees() {
        if (permitComboBox.getValue() == null) return;

        try {
            int permitId = extractId(permitComboBox.getValue());
            PermitModel permit = permits.stream()
                .filter(p -> p.getPermitID() == permitId)
                .findFirst()
                .orElse(null);

            if (permit == null) return;

            // Get permit type and calculate fees (simulated)
            double renewalFee = 5000.0; // Base renewal fee
            double surcharge = renewalService.calculateSurcharge(permit, null);
            double total = renewalFee + surcharge;

            if (renewalFeeLabel != null) renewalFeeLabel.setText(String.format("₱%.2f", renewalFee));
            if (surchargeLabel != null) surchargeLabel.setText(String.format("₱%.2f", surcharge));
            if (totalAmountLabel != null) totalAmountLabel.setText(String.format("₱%.2f", total));

            if (renewalDetailsArea != null) {
                String details = String.format(
                    "Business ID: %d\nPermit ID: %d\nPermit Number: %s\n" +
                    "Expiry Date: %s\n\nRenewal Fee: ₱%.2f\nSurcharge: ₱%.2f\nTotal: ₱%.2f",
                    permit.getBusinessID(),
                    permit.getPermitID(),
                    permit.getPermitNo(),
                    permit.getStatusEffectiveDate(),
                    renewalFee, surcharge, total
                );
                renewalDetailsArea.setText(details);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to calculate fees: " + e.getMessage());
        }
    }

    @FXML
    private void handleApplyRenewal(ActionEvent event) {
        try {
            // Validate inputs
            if (businessComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a business.");
                return;
            }

            if (permitComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a permit.");
                return;
            }

            int businessId = extractId(businessComboBox.getValue());
            int permitId = extractId(permitComboBox.getValue());

            // Apply for renewal
            int renewalId = renewalService.applyForRenewal(businessId, permitId);

            if (renewalId > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Renewal application created successfully!\nRenewal ID: " + renewalId);
                
                // Refresh data
                loadRenewalsTable();
                loadRenewalsForPayment();
                clearStep1Fields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create renewal application.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to apply for renewal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== STEP 2: RECORD PAYMENT ==========

    private void loadRenewalsForPayment() {
        try {
            if (renewalComboBox != null) {
                renewalComboBox.getItems().clear();
                renewals.clear();

                List<BusinessModel> allBusinesses = renewalService.getAllBusinesses();
                for (BusinessModel business : allBusinesses) {
                    List<PermitRenewalApplicationModel> businessRenewals = 
                        renewalService.getRenewalsByBusiness(business.getBusinessID());
                    
                    for (PermitRenewalApplicationModel renewal : businessRenewals) {
                        if ("pending".equalsIgnoreCase(renewal.getStatus())) {
                            renewals.add(renewal);
                            renewalComboBox.getItems().add(
                                renewal.getRenewalID() + " - Business ID: " + renewal.getBusinessID()
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load renewals: " + e.getMessage());
        }
    }

    private void displayPaymentAmount() {
        if (renewalComboBox.getValue() == null) return;

        try {
            int renewalId = extractId(renewalComboBox.getValue());
            PermitRenewalApplicationModel renewal = renewals.stream()
                .filter(r -> r.getRenewalID() == renewalId)
                .findFirst()
                .orElse(null);

            if (renewal != null && paymentTotalLabel != null) {
                paymentTotalLabel.setText(String.format("Total Amount Due: ₱%.2f", renewal.getTotalAmount()));
                if (paymentAmountField != null) {
                    paymentAmountField.setText(String.format("%.2f", renewal.getTotalAmount()));
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to display amount: " + e.getMessage());
        }
    }

    @FXML
    private void handleRecordPayment(ActionEvent event) {
        try {
            // Validate inputs
            if (renewalComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a renewal application.");
                return;
            }

            if (paymentAmountField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter payment amount.");
                return;
            }

            if (paymentMethodComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select payment method.");
                return;
            }

            int renewalId = extractId(renewalComboBox.getValue());
            double amount = Double.parseDouble(paymentAmountField.getText());
            String method = paymentMethodComboBox.getValue();

            // Record payment
            boolean success = renewalService.recordPayment(renewalId, amount, method);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Payment recorded successfully for Renewal ID: " + renewalId);
                
                // Refresh data
                loadRenewalsTable();
                loadPaidRenewalsForInspection();
                clearStep2Fields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to record payment.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid payment amount format.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== STEP 3: SCHEDULE INSPECTION ==========

    private void loadPaidRenewalsForInspection() {
        try {
            if (paidRenewalComboBox != null) {
                paidRenewalComboBox.getItems().clear();

                List<BusinessModel> allBusinesses = renewalService.getAllBusinesses();
                for (BusinessModel business : allBusinesses) {
                    List<PermitRenewalApplicationModel> businessRenewals = 
                        renewalService.getRenewalsByBusiness(business.getBusinessID());
                    
                    for (PermitRenewalApplicationModel renewal : businessRenewals) {
                        if ("paid".equalsIgnoreCase(renewal.getStatus())) {
                            paidRenewalComboBox.getItems().add(
                                renewal.getRenewalID() + " - Business ID: " + renewal.getBusinessID()
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load paid renewals: " + e.getMessage());
        }
    }

    private void loadInspectors() {
        try {
            // This would normally load from InspectorDAO
            // For now, adding sample data
            if (inspectorComboBox != null) {
                inspectorComboBox.getItems().clear();
                inspectorComboBox.getItems().addAll(
                    "1 - Juan Dela Cruz",
                    "2 - Maria Santos",
                    "3 - Pedro Reyes"
                );
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load inspectors: " + e.getMessage());
        }
    }

    @FXML
    private void handleScheduleInspection(ActionEvent event) {
        try {
            // Validate inputs
            if (paidRenewalComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a paid renewal.");
                return;
            }

            if (inspectorComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an inspector.");
                return;
            }

            if (inspectionDatePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select inspection date.");
                return;
            }

            int renewalId = extractId(paidRenewalComboBox.getValue());
            int inspectorId = extractId(inspectorComboBox.getValue());
            LocalDate localDate = inspectionDatePicker.getValue();
            Date inspectionDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Schedule inspection
            boolean success = renewalService.scheduleInspection(renewalId, inspectorId, inspectionDate);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Inspection scheduled successfully for Renewal ID: " + renewalId);
                
                // Refresh data
                loadRenewalsTable();
                loadScheduledRenewalsForFinalization();
                clearStep3Fields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to schedule inspection.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to schedule inspection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== STEP 4: FINALIZE RENEWAL ==========

    private void loadScheduledRenewalsForFinalization() {
        try {
            if (scheduledRenewalComboBox != null) {
                scheduledRenewalComboBox.getItems().clear();

                List<BusinessModel> allBusinesses = renewalService.getAllBusinesses();
                for (BusinessModel business : allBusinesses) {
                    List<PermitRenewalApplicationModel> businessRenewals = 
                        renewalService.getRenewalsByBusiness(business.getBusinessID());
                    
                    for (PermitRenewalApplicationModel renewal : businessRenewals) {
                        if ("paid".equalsIgnoreCase(renewal.getStatus()) && 
                            renewalService.isInspectionScheduled(renewal.getRenewalID())) {
                            scheduledRenewalComboBox.getItems().add(
                                renewal.getRenewalID() + " - Business ID: " + renewal.getBusinessID()
                            );
                        }
                    }
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load scheduled renewals: " + e.getMessage());
        }
    }

    private void loadInspectionSchedules() {
        try {
            if (inspectionScheduleComboBox != null) {
                inspectionScheduleComboBox.getItems().clear();
                // Load inspection schedules for the selected renewal
                // This would query InspectionScheduleDAO
                inspectionScheduleComboBox.getItems().addAll(
                    "1 - Schedule 2024-01-15",
                    "2 - Schedule 2024-01-20"
                );
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load schedules: " + e.getMessage());
        }
    }

    @FXML
    private void handleFinalizeRenewal(ActionEvent event) {
        try {
            // Validate inputs
            if (scheduledRenewalComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a renewal.");
                return;
            }

            if (inspectionScheduleComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an inspection schedule.");
                return;
            }

            if (inspectionResultComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select inspection result.");
                return;
            }

            int renewalId = extractId(scheduledRenewalComboBox.getValue());
            int scheduleId = extractId(inspectionScheduleComboBox.getValue());
            String result = inspectionResultComboBox.getValue();
            String remarks = inspectionRemarksArea.getText();

            // Finalize renewal
            boolean success = renewalService.finalizeRenewal(renewalId, scheduleId, result, remarks);

            if (success) {
                String statusMessage = "PASS".equalsIgnoreCase(result) ? 
                    "Renewal approved! Permit has been renewed." : 
                    "Renewal denied. Permit suspended due to failed inspection.";
                
                showAlert(Alert.AlertType.INFORMATION, "Success", statusMessage);
                
                // Refresh data
                loadRenewalsTable();
                clearStep4Fields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to finalize renewal.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to finalize renewal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== UTILITY METHODS ==========

    private void loadRenewalsTable() {
        try {
            if (renewalStatusTable != null) {
                ObservableList<PermitRenewalApplicationModel> allRenewals = FXCollections.observableArrayList();
                
                List<BusinessModel> allBusinesses = renewalService.getAllBusinesses();
                for (BusinessModel business : allBusinesses) {
                    allRenewals.addAll(renewalService.getRenewalsByBusiness(business.getBusinessID()));
                }
                
                renewalStatusTable.setItems(allRenewals);
            }

            // Also refresh dropdown lists
            loadRenewalsForPayment();
            loadPaidRenewalsForInspection();
            loadScheduledRenewalsForFinalization();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load renewals table: " + e.getMessage());
        }
    }

    private int extractId(String comboBoxValue) {
        return Integer.parseInt(comboBoxValue.split(" - ")[0]);
    }

    private void clearStep1Fields() {
        if (businessComboBox != null) businessComboBox.setValue(null);
        if (permitComboBox != null) permitComboBox.setValue(null);
        if (renewalFeeLabel != null) renewalFeeLabel.setText("₱0.00");
        if (surchargeLabel != null) surchargeLabel.setText("₱0.00");
        if (totalAmountLabel != null) totalAmountLabel.setText("₱0.00");
        if (renewalDetailsArea != null) renewalDetailsArea.clear();
    }

    private void clearStep2Fields() {
        if (renewalComboBox != null) renewalComboBox.setValue(null);
        if (paymentAmountField != null) paymentAmountField.clear();
        if (paymentMethodComboBox != null) paymentMethodComboBox.setValue(null);
        if (paymentTotalLabel != null) paymentTotalLabel.setText("Total Amount Due: ₱0.00");
    }

    private void clearStep3Fields() {
        if (paidRenewalComboBox != null) paidRenewalComboBox.setValue(null);
        if (inspectorComboBox != null) inspectorComboBox.setValue(null);
        if (inspectionDatePicker != null) inspectionDatePicker.setValue(null);
    }

    private void clearStep4Fields() {
        if (scheduledRenewalComboBox != null) scheduledRenewalComboBox.setValue(null);
        if (inspectionScheduleComboBox != null) inspectionScheduleComboBox.setValue(null);
        if (inspectionResultComboBox != null) inspectionResultComboBox.setValue(null);
        if (inspectionRemarksArea != null) inspectionRemarksArea.clear();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
*/