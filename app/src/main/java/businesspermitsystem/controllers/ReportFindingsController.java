package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.InspectionResultDAO;
import businesspermitsystem.db.InspectionScheduleDAO; 
import businesspermitsystem.db.PermitDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.InspectionResultModel;
import businesspermitsystem.models.InspectionScheduleModel;
import businesspermitsystem.models.PermitModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportFindingsController {

    // --- FXML Fields: Schedule/Details ---
    @FXML private ChoiceBox<Integer> scheduleIDChoiceBox;
    @FXML private DatePicker filterDatePicker;
    @FXML private Button clearDateFilterButton;
    @FXML private Label businessDetailsLabel;
    @FXML private Label inspectionDateLabel;
    
    // --- FXML Fields: Findings ---
    @FXML private ChoiceBox<String> resultChoiceBox;
    @FXML private TextArea remarksTextArea;
    @FXML private Button reportButton;
    
    // --- FXML Fields: Dynamic Permit Status Container ---
    @FXML private VBox permitUpdateContainer; 
    
    // --- FXML Fields: Business Status  ---
    @FXML private Label currentBusinessStatusLabel;
    @FXML private ChoiceBox<String> suggestedBusinessStatusChoiceBox; 
    @FXML private CheckBox updateBusinessStatusCheckBox;

    // --- Data Access Objects (DAOs) ---
    private final InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO();
    private final InspectionResultDAO resultDAO = new InspectionResultDAO();
    private final BusinessDAO businessDAO = new BusinessDAO(); 
    private final PermitDAO permitDAO = new PermitDAO(); 
    
    // --- Internal State ---
    private Map<Integer, Integer> scheduleToBusinessMap; 
    private BusinessModel targetBusiness = null; 
    
    private List<PermitUpdateControls> permitControlList = new ArrayList<>(); 


    private static final List<String> PERMIT_STATUSES = List.of("active", "pending", "revoked", "merged", "suspended");
    private static final List<String> BUSINESS_STATUSES = List.of("active", "suspended", "closed");
    
    /**
     * Helper class to link a PermitModel to its dynamic UI controls.
     */
    private static class PermitUpdateControls {
        final PermitModel permit;
        final CheckBox updateCheckBox;
        final ChoiceBox<String> statusChoiceBox;

        public PermitUpdateControls(PermitModel permit, CheckBox updateCheckBox, ChoiceBox<String> statusChoiceBox) {
            this.permit = permit;
            this.updateCheckBox = updateCheckBox;
            this.statusChoiceBox = statusChoiceBox;
        }
    }

    @FXML
    private void initialize() {
      
        resultChoiceBox.setItems(FXCollections.observableArrayList("Passed", "Failed", "Conditionally Approved", "Pending Re-Inspection"));
        suggestedBusinessStatusChoiceBox.setItems(FXCollections.observableArrayList(BUSINESS_STATUSES));

        filterDatePicker.setValue(LocalDate.now()); 
        loadSchedules(); 
        

        filterDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> loadSchedules());
        scheduleIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newScheduleID) -> {
            if (newScheduleID != null) {
                handleScheduleSelection(newScheduleID);
            } else {
                clearDetails();
            }
        });
        
      
        resultChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newResult) -> {
            if (newResult != null) {
                suggestStatusesBasedOnInspectionResult(newResult);
            }
            reportButton.setDisable(remarksTextArea.getText().trim().isEmpty() || newResult == null);
        });
        
        
        remarksTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
             reportButton.setDisable(resultChoiceBox.getSelectionModel().getSelectedItem() == null || newVal.trim().isEmpty());
        });

        setReportFieldsDisabled(true);
    }

    /**
     * Maps the inspection result to suggested final status for both Permit and Business, using specified statuses.
     */
    private void suggestStatusesBasedOnInspectionResult(String inspectionResult) {
        String suggestedPermit = null;
        String suggestedBusiness = null;

        switch (inspectionResult) {
            case "Passed":
                suggestedPermit = "active";
                suggestedBusiness = "active";
                break;
            case "Failed":
                suggestedPermit = "revoked";
                suggestedBusiness = "suspended"; 
                break;
            case "Conditionally Approved":
                suggestedPermit = "suspended"; 
                suggestedBusiness = "suspended";
                break;
            case "Pending Re-Inspection":
                suggestedPermit = "suspended";
                suggestedBusiness = "suspended";
                break;
        }
        

        suggestedBusinessStatusChoiceBox.getSelectionModel().select(suggestedBusiness);
    
        for (PermitUpdateControls control : permitControlList) {
            control.statusChoiceBox.getSelectionModel().select(suggestedPermit);
        }
    }

    private void loadSchedules() {
        try {
            LocalDate selectedDate = filterDatePicker.getValue();
            List<InspectionScheduleModel> schedules = scheduleDAO.getFilteredSchedules(selectedDate);
            
            scheduleToBusinessMap = schedules.stream()
                .collect(Collectors.toMap(
                    InspectionScheduleModel::getScheduleID, 
                    InspectionScheduleModel::getBusinessID
                ));
            
            scheduleIDChoiceBox.setItems(FXCollections.observableArrayList(scheduleToBusinessMap.keySet()).sorted());
            
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Data Load Error", "Failed to load inspection schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void clearDateFilter() {
        filterDatePicker.setValue(null); 
    }

    /**
     * Creates and displays dynamic controls for all permits associated with the business.
     */
    private void buildPermitUpdateControls(int businessID) throws SQLException {
        permitUpdateContainer.getChildren().clear(); 
        permitControlList.clear(); 

        List<PermitModel> permits = permitDAO.getPermitsByBusinessID(businessID);
        
        if (permits.isEmpty()) {
            permitUpdateContainer.getChildren().add(new Label("No permits found for this business."));
            return;
        }

        permitUpdateContainer.getChildren().add(new Label("Check the 'Apply' box for any permit status you wish to change:"));
        
        for (PermitModel permit : permits) {
           
            CheckBox updateBox = new CheckBox("Apply");
            ChoiceBox<String> statusBox = new ChoiceBox<>(FXCollections.observableArrayList(PERMIT_STATUSES)); // Uses specific list
            
            statusBox.getSelectionModel().select(permit.getStatus()); 
            
           
            HBox permitRow = new HBox(15);
            permitRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label detailsLabel = new Label("Permit ID: " + permit.getPermitID() + 
                                           " | Current Status: " + permit.getStatus() + " | New Status:");
            
            permitRow.setStyle("-fx-padding: 5; -fx-border-color: #ccc; -fx-border-width: 1; -fx-background-color: #f7f7f7;");

            permitRow.getChildren().addAll(
                updateBox, 
                detailsLabel, 
                statusBox
            );
            
            
            permitUpdateContainer.getChildren().add(permitRow);
            permitControlList.add(new PermitUpdateControls(permit, updateBox, statusBox));
        }
    }
    
    private void handleScheduleSelection(int scheduleID) {
        clearDetails();
        setReportFieldsDisabled(true);

        try {
            if (resultDAO.resultExistsForSchedule(scheduleID)) {
                showAlert(AlertType.WARNING, "Report Exists", "Findings for Schedule ID " + scheduleID + " have already been filed.");
                return;
            }

            int businessID = scheduleToBusinessMap.get(scheduleID);
            InspectionScheduleModel schedule = scheduleDAO.getScheduleByID(scheduleID);
            
            
            targetBusiness = businessDAO.getBusinessByID(businessID);
            
            
            buildPermitUpdateControls(businessID);

            
            currentBusinessStatusLabel.setText(targetBusiness.getStatus());
            businessDetailsLabel.setText(targetBusiness.getBusinessName() + " (" + targetBusiness.getTradeName() + ")");
            inspectionDateLabel.setText(schedule.getInspectionDate().toString());
            
            setReportFieldsDisabled(false);

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Data Retrieval Error", "Failed to retrieve data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void reportButtonPressed() {
        Integer scheduleID = scheduleIDChoiceBox.getSelectionModel().getSelectedItem();
        String result = resultChoiceBox.getSelectionModel().getSelectedItem();
        String remarks = remarksTextArea.getText();
        
        boolean updateBusiness = updateBusinessStatusCheckBox.isSelected();
        String newBusinessStatus = suggestedBusinessStatusChoiceBox.getSelectionModel().getSelectedItem();

       
        if (scheduleID == null || result == null || remarks.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Missing Information", "Please select Schedule, Result, and provide Remarks.");
            return;
        }
        
        if (updateBusiness && (targetBusiness == null || newBusinessStatus == null)) {
            showAlert(AlertType.ERROR, "Business Update Error", "Cannot update business status. Select a new status or uncheck the business box.");
            return;
        }
        
        
        boolean anyPermitUpdateRequested = false;
        for (PermitUpdateControls control : permitControlList) {
            if (control.updateCheckBox.isSelected()) {
                anyPermitUpdateRequested = true;
                if (control.statusChoiceBox.getSelectionModel().getSelectedItem() == null) {
                    showAlert(AlertType.ERROR, "Permit Status Error", 
                              "A permit is selected for update, but no New Status is chosen for Permit ID " + control.permit.getPermitID() + ".");
                    return;
                }
            }
        }

        try {
            
            InspectionResultModel resultModel = new InspectionResultModel();
            resultModel.setScheduleId(scheduleID);
            resultModel.setResult(result);
            resultModel.setRemarks(remarks);

            if (!resultDAO.addResult(resultModel)) {
                 showAlert(AlertType.ERROR, "Database Error", "Failed to save the inspection findings.");
                 return;
            }
            
            
            StringBuilder successMessage = new StringBuilder("Findings filed successfully. ");
            
            
            for (PermitUpdateControls control : permitControlList) {
                if (control.updateCheckBox.isSelected()) {
                    String newPermitStatus = control.statusChoiceBox.getSelectionModel().getSelectedItem();
                    PermitModel permitToUpdate = control.permit;
                    
                    boolean updated = permitDAO.updatePermitStatus(
                        permitToUpdate.getPermitID(), 
                        newPermitStatus, 
                        new Date(), 
                        "Status changed due to inspection findings: " + remarks 
                    );
                    successMessage.append(updated ? 
                        "Permit ID " + permitToUpdate.getPermitID() + " updated to '" + newPermitStatus + "'. " : 
                        "Permit ID " + permitToUpdate.getPermitID() + " update failed. "
                    );
                }
            }
            if (!anyPermitUpdateRequested) {
                 successMessage.append("No permit statuses were changed. ");
            }
            
         
            if (updateBusiness) {
                targetBusiness.setStatus(newBusinessStatus);
                boolean updated = businessDAO.updateBusiness(targetBusiness);
                successMessage.append(updated ? "Business status updated to '" + newBusinessStatus + "'. " : "Business update failed. ");
            } else {
                 successMessage.append("Business status was not changed. ");
            }

            
            InspectionScheduleModel scheduleToUpdate = scheduleDAO.getScheduleByID(scheduleID);
            scheduleToUpdate.setStatus("Complete");
            scheduleDAO.updateSchedule(scheduleToUpdate);

            showAlert(AlertType.INFORMATION, "Success", successMessage.toString());

            
            loadSchedules(); 
            clearDetails();
            setReportFieldsDisabled(true);

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to process report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Helper Methods ---

    private void clearDetails() {
        businessDetailsLabel.setText("N/A");
        inspectionDateLabel.setText("N/A");
        currentBusinessStatusLabel.setText("N/A");
        targetBusiness = null;
        
        remarksTextArea.clear();
        resultChoiceBox.getSelectionModel().clearSelection();
        suggestedBusinessStatusChoiceBox.getSelectionModel().clearSelection();
        updateBusinessStatusCheckBox.setSelected(false);
        
        // Clear dynamic permit controls
        permitUpdateContainer.getChildren().clear();
        permitControlList.clear();
        permitUpdateContainer.getChildren().add(new Label("Select a schedule to load permit data..."));
    }

    private void setReportFieldsDisabled(boolean disabled) {
        resultChoiceBox.setDisable(disabled);
        remarksTextArea.setDisable(disabled);
        
        permitUpdateContainer.setDisable(disabled);
        
        suggestedBusinessStatusChoiceBox.setDisable(disabled);
        updateBusinessStatusCheckBox.setDisable(disabled);
        
        reportButton.setDisable(true); 
    }

    @FXML
    private void cancelButtonPressed() {
        Stage stage = (Stage) reportButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}