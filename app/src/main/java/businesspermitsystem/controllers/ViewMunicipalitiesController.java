package businesspermitsystem.controllers;

import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.MunicipalityModel;
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
import java.util.Arrays;
import java.util.List;

public class ViewMunicipalitiesController {

    // --- FXML Fields ---
    @FXML private TextField filterNameField;
    @FXML private TextField filterRegionField;
    @FXML private ChoiceBox<String> filterClassChoiceBox;
    
    @FXML private TableView<MunicipalityModel> municipalitiesTable;
    @FXML private TableColumn<MunicipalityModel, Integer> colID;
    @FXML private TableColumn<MunicipalityModel, String> colName;
    @FXML private TableColumn<MunicipalityModel, String> colProvince;
    @FXML private TableColumn<MunicipalityModel, String> colRegion;
    @FXML private TableColumn<MunicipalityModel, String> colClassification;
    @FXML private TableColumn<MunicipalityModel, String> colContact;
    @FXML private TableColumn<MunicipalityModel, String> colBarangay;

    // --- Data Access ---
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO();
    
   
    private ObservableList<MunicipalityModel> masterData = FXCollections.observableArrayList();
    private FilteredList<MunicipalityModel> filteredData;

    
    private static final List<String> CLASSIFICATIONS = Arrays.asList(
        "All",
        "First Class City", "Second Class City", "Third Class City", "Fourth Class City", 
        "Fifth Class City", "Sixth Class City", "First Class Municipality", 
        "Second Class Municipality", "Third Class Municipality", "Fourth Class Municipality", 
        "Fifth Class Municipality", "Sixth Class Municipality", "Highly Urbanized City"
    );

    @FXML
    public void initialize() {
        
        colID.setCellValueFactory(new PropertyValueFactory<>("municipalityID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("municipalityName"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
        colClassification.setCellValueFactory(new PropertyValueFactory<>("classification"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colBarangay.setCellValueFactory(new PropertyValueFactory<>("officeBarangay"));

        
        filterClassChoiceBox.setItems(FXCollections.observableArrayList(CLASSIFICATIONS));
        filterClassChoiceBox.getSelectionModel().select("All");

   
        refreshTable();

        
        filteredData = new FilteredList<>(masterData, p -> true);

        filterNameField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterRegionField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        filterClassChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateFilter());

       
        SortedList<MunicipalityModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(municipalitiesTable.comparatorProperty());

    
        municipalitiesTable.setItems(sortedData);
    }

    @FXML
    private void refreshTable() {
        try {
            masterData.clear();
            List<MunicipalityModel> municipalities = municipalityDAO.getMunicipalities();
            masterData.addAll(municipalities);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", "Failed to load municipalities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateFilter() {
        filteredData.setPredicate(municipality -> {
            
           
            String nameFilter = filterNameField.getText().toLowerCase();
            if (!nameFilter.isEmpty()) {
                if (!municipality.getMunicipalityName().toLowerCase().contains(nameFilter)) {
                    return false; 
                }
            }

            
            String regionFilter = filterRegionField.getText().toLowerCase();
            if (!regionFilter.isEmpty()) {
                boolean matchesRegion = municipality.getRegion().toLowerCase().contains(regionFilter);
                boolean matchesProvince = municipality.getProvince().toLowerCase().contains(regionFilter);
                if (!matchesRegion && !matchesProvince) {
                    return false; 
                }
            }

           
            String classFilter = filterClassChoiceBox.getValue();
            if (classFilter != null && !"All".equals(classFilter)) {
                if (!classFilter.equalsIgnoreCase(municipality.getClassification())) {
                    return false;
                }
            }

            return true;
        });
    }

    @FXML
    private void resetFilters() {
        filterNameField.clear();
        filterRegionField.clear();
        filterClassChoiceBox.getSelectionModel().select("All");
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