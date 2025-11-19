package businesspermitsystem.controllers;

import businesspermitsystem.db.InspectionScheduleDAO;
import businesspermitsystem.models.InspectionScheduleModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ViewInspectionSchedulesController {

    // --- FXML Fields ---
    @FXML private DatePicker filterDateFrom;
    @FXML private DatePicker filterDateTo;
    @FXML private ChoiceBox<String> filterStatusChoiceBox;
    @FXML private TextField filterBusinessIdField;
    
    @FXML private TableView<InspectionScheduleModel> scheduleTable;
    @FXML private TableColumn<InspectionScheduleModel, Integer> colScheduleID;
    @FXML private TableColumn<InspectionScheduleModel, Integer> colBusinessID;
    @FXML private TableColumn<InspectionScheduleModel, Integer> colInspectorID;
    @FXML private TableColumn<InspectionScheduleModel, String> colDate; // LocalDate binds as String/Object often
    @FXML private TableColumn<InspectionScheduleModel, String> colStatus;

    
    private final InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO();
    
    
    private ObservableList<InspectionScheduleModel> masterData = FXCollections.observableArrayList();
    private FilteredList<InspectionScheduleModel> filteredData;

    @FXML
    public void initialize() {
        
        colScheduleID.setCellValueFactory(new PropertyValueFactory<>("scheduleID"));
        colBusinessID.setCellValueFactory(new PropertyValueFactory<>("businessID"));
        colInspectorID.setCellValueFactory(new PropertyValueFactory<>("inspectorID"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("inspectionDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        
        filterStatusChoiceBox.setItems(FXCollections.observableArrayList(
            "All", "Scheduled", "Complete", "Cancelled"
        ));
        filterStatusChoiceBox.getSelectionModel().select("All");

        
        refreshTable();

        
        
        filteredData = new FilteredList<>(masterData, p -> true);

        
        filterDateFrom.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterDateTo.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterStatusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterBusinessIdField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        
        SortedList<InspectionScheduleModel> sortedData = new SortedList<>(filteredData);
        
        sortedData.comparatorProperty().bind(scheduleTable.comparatorProperty());

        
        scheduleTable.setItems(sortedData);
    }

    @FXML
    private void refreshTable() {
        try {
            masterData.clear();
            List<InspectionScheduleModel> schedules = scheduleDAO.getAllSchedules();
            masterData.addAll(schedules);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to load schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The core filtering logic. Called whenever a filter input changes.
     */
    private void updateFilter() {
        filteredData.setPredicate(schedule -> {
            
            String businessIdFilter = filterBusinessIdField.getText();
            if (businessIdFilter != null && !businessIdFilter.isEmpty()) {
               
                if (!String.valueOf(schedule.getBusinessID()).contains(businessIdFilter)) {
                    return false; 
                }
            }

            String statusFilter = filterStatusChoiceBox.getValue();
            if (statusFilter != null && !"All".equals(statusFilter)) {
                if (!statusFilter.equalsIgnoreCase(schedule.getStatus())) {
                    return false; 
                }
            }

           
            LocalDate date = schedule.getInspectionDate();
            LocalDate from = filterDateFrom.getValue();
            LocalDate to = filterDateTo.getValue();

            if (from != null && date.isBefore(from)) {
                return false; 
            }
            if (to != null && date.isAfter(to)) {
                return false; 
            }

            return true; 
        });
    }

    @FXML
    private void resetFilters() {
        filterDateFrom.setValue(null);
        filterDateTo.setValue(null);
        filterStatusChoiceBox.getSelectionModel().select("All");
        filterBusinessIdField.clear();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) scheduleTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}