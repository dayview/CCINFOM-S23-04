package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.db.InspectionScheduleDAO; 
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.utils.SceneManager;
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

public class ScheduleInspectionController {

    // FXML Fields 
    @FXML private ChoiceBox<Integer> businessIDChoiceBox;
    @FXML private Label businessStatusLabel;
    @FXML private Label businessMunicipalityLabel;
    @FXML private ChoiceBox<Integer> inspectorIDChoiceBox;
    @FXML private DatePicker inspectionDatePicker;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    // Data Access Objects 
    private final BusinessDAO businessDAO = new BusinessDAO();
    private final InspectorDAO inspectorDAO = new InspectorDAO();
    private final InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO(); 
    
    // --- Internal State ---
    // Stores all Business IDs and their associated Municipality IDs for quick lookup (JURISDICTION check)
    private Map<Integer, Integer> businessLocationMap; 
    
    @FXML
    private void initialize() {
        loadAllBusinessesAndLocations();
        
        setScheduleFieldsDisabled(true);
        
        // Listener to trigger inspector filtering when a business is selected
        businessIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newBusinessID) -> {
            if (newBusinessID != null) {
                try {
                    handleBusinessSelection(newBusinessID);
                } catch (SQLException e) {
                    showAlert(AlertType.ERROR, "Data Error", "Failed to load business details: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                clearDetails();
            }
        });
    }

    /**
     * Loads all business IDs and their corresponding Municipality IDs using the existing BusinessDAO.
     */
    private void loadAllBusinessesAndLocations() {
        try {
            List<BusinessModel> allBusinesses = businessDAO.getAllBusinesses(); 
            
            
            businessLocationMap = allBusinesses.stream()
                .collect(Collectors.toMap(BusinessModel::getBusinessId, BusinessModel::getMunicipalityId));
            
            // Populate the ChoiceBox
            businessIDChoiceBox.setItems(FXCollections.observableArrayList(businessLocationMap.keySet()).sorted());
            
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Data Load Error", "Failed to load business list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the selection of a Business ID, filters inspectors based on location, and updates labels.
     */
    private void handleBusinessSelection(int businessID) throws SQLException {
        setScheduleFieldsDisabled(true);
        clearDetails(); 

        int municipalityID = businessLocationMap.get(businessID);
        // Uses existing BusinessDAO.getBusinessByID method
        BusinessModel selectedBusiness = businessDAO.getBusinessByID(businessID);

        //Update Labels (Status & Location)
        if (selectedBusiness != null) {
            businessStatusLabel.setText(selectedBusiness.getStatus());
        }
        businessMunicipalityLabel.setText("Municipality ID: " + municipalityID); 

        // Filter and Load Eligible Inspectors (JURISDICTION CHECK)
        List<InspectorModel> eligibleInspectors = inspectorDAO.getInspectorsByMunicipality(municipalityID);

        if (eligibleInspectors.isEmpty()) {
            showAlert(AlertType.WARNING, "No Inspectors Available", 
                      "No active inspectors found for Municipality ID " + municipalityID + ". Cannot schedule.");
            return;
        }


        List<Integer> inspectorIDs = eligibleInspectors.stream()
            .map(InspectorModel::getInspectorID)
            .collect(Collectors.toList());
        
        inspectorIDChoiceBox.setItems(FXCollections.observableArrayList(inspectorIDs));


        setScheduleFieldsDisabled(false);
    }

    /**
     * Handles the action when the Confirm Schedule button is pressed.
     */
    @FXML
    private void confirmButtonPressed() {
        Integer businessID = businessIDChoiceBox.getSelectionModel().getSelectedItem();
        Integer inspectorID = inspectorIDChoiceBox.getSelectionModel().getSelectedItem();
        LocalDate inspectionDate = inspectionDatePicker.getValue();

        if (businessID == null || inspectorID == null || inspectionDate == null) {
            showAlert(AlertType.ERROR, "Missing Information", "Please select a Business, Inspector, and Date.");
            return;
        }

        try {
            BusinessModel currentBusiness = businessDAO.getBusinessByID(businessID);


            String status = currentBusiness.getStatus();
            if ("Active".equalsIgnoreCase(status) || "Revoked".equalsIgnoreCase(status)) {
                if (!showConfirmation("Status Alert", "Business Status is '" + status + "'", 
                                      "This business may not require an inspection. Schedule anyway?")) {
                    return; 
                }
            }
            
            if (!inspectorDAO.isAvailable(inspectorID, inspectionDate)) {
                 showAlert(AlertType.ERROR, "Scheduling Conflict", 
                           "Inspector ID " + inspectorID + " is already booked on " + inspectionDate + ".");
                 return;
            }


            InspectionScheduleModel newSchedule = new InspectionScheduleModel();
            newSchedule.setBusinessID(businessID);
            newSchedule.setInspectorID(inspectorID);
            newSchedule.setInspectionDate(inspectionDate);



            if (scheduleDAO.addSchedule(newSchedule)) {

                 currentBusiness.setStatus("Inspection Scheduled");
                 
                showAlert(AlertType.INFORMATION, "Success", "Inspection successfully scheduled for Business ID " + businessID + ".");
                 
            } else {
                 showAlert(AlertType.ERROR, "Database Error", "Failed to save the inspection schedule.");
                 return;
            }
            
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to process schedule: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
    
    // --- Helper Methods ---

    private void clearDetails() {
        businessStatusLabel.setText("N/A");
        businessMunicipalityLabel.setText("N/A");
        inspectorIDChoiceBox.getItems().clear();
    }

    private void setScheduleFieldsDisabled(boolean disabled) {
        inspectorIDChoiceBox.setDisable(disabled);
        inspectionDatePicker.setDisable(disabled);
        confirmButton.setDisable(disabled);
    }

    @FXML
    private void cancelButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
    
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}