package businesspermitsystem.controllers;

import businesspermitsystem.db.InspectionResultDAO;
import businesspermitsystem.models.InspectionResultModel;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ViewInspectionResultsController {

    //  FXML Fields
    @FXML private ChoiceBox<String> filterResultChoiceBox;
    @FXML private TextField filterScheduleIdField;
    
    @FXML private TableView<InspectionResultModel> resultsTable;
    @FXML private TableColumn<InspectionResultModel, Integer> colInspectionID;
    @FXML private TableColumn<InspectionResultModel, Integer> colScheduleID;
    @FXML private TableColumn<InspectionResultModel, String> colResult;
    @FXML private TableColumn<InspectionResultModel, String> colRemarks;

    //  Data Access
    private final InspectionResultDAO resultDAO = new InspectionResultDAO();
    
    // Collections for Filtering
    private ObservableList<InspectionResultModel> masterData = FXCollections.observableArrayList();
    private FilteredList<InspectionResultModel> filteredData;

    @FXML
    public void initialize() {
        
        colInspectionID.setCellValueFactory(new PropertyValueFactory<>("inspectionId"));
        colScheduleID.setCellValueFactory(new PropertyValueFactory<>("scheduleId"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("result"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

       
        filterResultChoiceBox.setItems(FXCollections.observableArrayList(
            "All", "Passed", "Failed", "Conditionally Approved", "Pending Re-Inspection"
        ));
        filterResultChoiceBox.getSelectionModel().select("All");

     
        refreshTable();

        
        filteredData = new FilteredList<>(masterData, p -> true);

        
        filterResultChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterScheduleIdField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

       
        SortedList<InspectionResultModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(resultsTable.comparatorProperty());

        
        resultsTable.setItems(sortedData);
    }

    @FXML
    private void refreshTable() {
        try {
            masterData.clear();
            // Assuming getAllResults exists in your DAO as per previous discussions
            List<InspectionResultModel> results = resultDAO.getAllResults();
            masterData.addAll(results);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to load inspection results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The core filtering logic. Called whenever a filter input changes.
     */
    private void updateFilter() {
        filteredData.setPredicate(resultRecord -> {
            
         
            String resultFilter = filterResultChoiceBox.getValue();
            if (resultFilter != null && !"All".equals(resultFilter)) {
                if (!resultFilter.equalsIgnoreCase(resultRecord.getResult())) {
                    return false; 
                }
            }

        
            String schedIdFilter = filterScheduleIdField.getText();
            if (schedIdFilter != null && !schedIdFilter.isEmpty()) {
                if (!String.valueOf(resultRecord.getScheduleId()).contains(schedIdFilter)) {
                    return false; 
                }
            }

            return true; 
        });
    }

    @FXML
    private void resetFilters() {
        filterResultChoiceBox.getSelectionModel().select("All");
        filterScheduleIdField.clear();
    }

    @FXML
    private void closeWindow() {
        Stage currentStage = (Stage) filterScheduleIdField.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InspectionScheduleView.fxml", "Inspection Schedules And Clearance");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}