package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.InspectionResultDAO;
import businesspermitsystem.db.InspectionScheduleDAO; 
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.InspectionResultModel;
import businesspermitsystem.models.InspectionScheduleModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportFindingsController {

    // --- FXML Fields ---
    @FXML private ChoiceBox<Integer> scheduleIDChoiceBox;
    @FXML private ChoiceBox<String> resultChoiceBox;
    @FXML private TextArea remarksTextArea;
    @FXML private Button reportButton;
    @FXML private Label businessDetailsLabel;
    @FXML private Label inspectionDateLabel;
    
    @FXML private DatePicker filterDatePicker;
    @FXML private Button clearDateFilterButton;

    // --- Data Access Objects (DAOs) ---
    private final InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO();
    private final InspectionResultDAO resultDAO = new InspectionResultDAO();
    private final BusinessDAO businessDAO = new BusinessDAO(); 
    
    
    private Map<Integer, Integer> scheduleToBusinessMap; 
    
    @FXML
    private void initialize() {
        // Populate the Result ChoiceBox with fixed values
        resultChoiceBox.setItems(FXCollections.observableArrayList("Passed", "Failed", "Conditionally Approved", "Pending Re-Inspection"));
        
        // Set default filter state (e.g., filter schedules up to today) and load data
        filterDatePicker.setValue(LocalDate.now()); 
        loadSchedules(); 
        
        // Listener to reload schedules when the user changes the filter date
        filterDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            loadSchedules();
        });
        
        // Listener to load business details when a schedule is selected
        scheduleIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newScheduleID) -> {
            if (newScheduleID != null) {
                handleScheduleSelection(newScheduleID);
            } else {
                clearDetails();
            }
        });
        
        setReportFieldsDisabled(true);
    }

    /**
     * Loads schedules using the date set in the filterDatePicker. 
     * Always excludes schedules marked 'Complete'.
     */
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
    
    /**
     * Clears the DatePicker value and reloads the unfiltered list (non-completed only).
     */
    @FXML
    private void clearDateFilter() {
       
        filterDatePicker.setValue(null); 
    }

    /**
     * Handles the selection of a Schedule ID, loads related business info, and checks for existing reports.
     */
    private void handleScheduleSelection(int scheduleID) {
        clearDetails();
        setReportFieldsDisabled(true);

        try {
            
            if (resultDAO.resultExistsForSchedule(scheduleID)) {
                showAlert(AlertType.WARNING, "Report Exists", 
                          "Findings for Schedule ID " + scheduleID + " have already been filed. Cannot file a duplicate report.");
                return;
            }

           
            int businessID = scheduleToBusinessMap.get(scheduleID);
            BusinessModel business = businessDAO.getBusinessByID(businessID);
            InspectionScheduleModel schedule = scheduleDAO.getScheduleByID(scheduleID);

           
            businessDetailsLabel.setText("Business Name: " + business.getBusinessName() + " (" + business.getTradeName() + ")");
            inspectionDateLabel.setText("Inspection Schedule: " + schedule.getInspectionDate().toString());
            
            setReportFieldsDisabled(false);

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Data Retrieval Error", "Failed to retrieve schedule or business data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the Report Findings button is pressed.
     */
    @FXML
    private void reportButtonPressed() {
        Integer scheduleID = scheduleIDChoiceBox.getSelectionModel().getSelectedItem();
        String result = resultChoiceBox.getSelectionModel().getSelectedItem();
        String remarks = remarksTextArea.getText();

        if (scheduleID == null || result == null || remarks.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Missing Information", "Please select a Schedule, select a Result, and provide Remarks.");
            return;
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

            InspectionScheduleModel scheduleToUpdate = scheduleDAO.getScheduleByID(scheduleID);
            scheduleToUpdate.setStatus("Complete");
            scheduleDAO.updateSchedule(scheduleToUpdate);

            showAlert(AlertType.INFORMATION, "Success", 
                       "Findings filed successfully for Schedule ID " + scheduleID + ". Schedule marked as complete.");

           
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
        // Clear selection and detail fields
        businessDetailsLabel.setText("N/A");
        inspectionDateLabel.setText("N/A");
        remarksTextArea.clear();
        resultChoiceBox.getSelectionModel().clearSelection();
    }

    private void setReportFieldsDisabled(boolean disabled) {
        resultChoiceBox.setDisable(disabled);
        remarksTextArea.setDisable(disabled);
        reportButton.setDisable(disabled);
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