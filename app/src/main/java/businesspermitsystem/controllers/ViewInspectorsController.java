package businesspermitsystem.controllers;

import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.models.InspectorModel;
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

public class ViewInspectorsController {

    // --- FXML Fields ---
    @FXML private TextField filterNameField;
    @FXML private TextField filterDesignationField;
    @FXML private ChoiceBox<String> filterStatusChoiceBox;
    
    @FXML private TableView<InspectorModel> inspectorsTable;
    @FXML private TableColumn<InspectorModel, Integer> colID;
    @FXML private TableColumn<InspectorModel, String> colLastName;
    @FXML private TableColumn<InspectorModel, String> colFirstName;
    @FXML private TableColumn<InspectorModel, String> colMiddleName;
    @FXML private TableColumn<InspectorModel, String> colDesignation;
    @FXML private TableColumn<InspectorModel, String> colLicense;
    @FXML private TableColumn<InspectorModel, Boolean> colActive;
    @FXML private TableColumn<InspectorModel, Integer> colMunicipalityID;

    // --- Data Access ---
    private final InspectorDAO inspectorDAO = new InspectorDAO();
    
    // --- Collections for Filtering ---
    private ObservableList<InspectorModel> masterData = FXCollections.observableArrayList();
    private FilteredList<InspectorModel> filteredData;

    @FXML
    public void initialize() {
        // 1. Initialize Columns
        colID.setCellValueFactory(new PropertyValueFactory<>("inspectorID"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        colMunicipalityID.setCellValueFactory(new PropertyValueFactory<>("municipalityID"));

        // 2. Initialize Filter ChoiceBox
        filterStatusChoiceBox.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        filterStatusChoiceBox.getSelectionModel().select("All");

        // 3. Load Data
        refreshTable();

        // 4. Setup Filtering Logic
        filteredData = new FilteredList<>(masterData, p -> true);

        // Add Listeners to inputs
        filterNameField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterDesignationField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterStatusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        // 5. Wrap in SortedList
        SortedList<InspectorModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inspectorsTable.comparatorProperty());

        // 6. Set items
        inspectorsTable.setItems(sortedData);
    }

    @FXML
    private void refreshTable() {
        try {
            masterData.clear();
            List<InspectorModel> inspectors = inspectorDAO.getInspectors();
            masterData.addAll(inspectors);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to load inspectors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateFilter() {
        filteredData.setPredicate(inspector -> {
            
            // 1. Filter by Name (First or Last)
            String nameFilter = filterNameField.getText().toLowerCase();
            if (!nameFilter.isEmpty()) {
                boolean matchesFirst = inspector.getFirstName().toLowerCase().contains(nameFilter);
                boolean matchesLast = inspector.getLastName().toLowerCase().contains(nameFilter);
                if (!matchesFirst && !matchesLast) {
                    return false; 
                }
            }

            // 2. Filter by Designation
            String roleFilter = filterDesignationField.getText().toLowerCase();
            if (!roleFilter.isEmpty()) {
                if (!inspector.getDesignation().toLowerCase().contains(roleFilter)) {
                    return false; 
                }
            }

            // 3. Filter by Status (Active/Inactive)
            String statusFilter = filterStatusChoiceBox.getValue();
            if ("Active".equals(statusFilter)) {
                if (!inspector.isActive()) return false;
            } else if ("Inactive".equals(statusFilter)) {
                if (inspector.isActive()) return false;
            }

            return true;
        });
    }

    @FXML
    private void resetFilters() {
        filterNameField.clear();
        filterDesignationField.clear();
        filterStatusChoiceBox.getSelectionModel().select("All");
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) filterNameField.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit System");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}