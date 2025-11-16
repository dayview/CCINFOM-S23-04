package businesspermitsystem.controllers;

import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.MunicipalityModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the Delete Municipality UI. Allows the user to select a
 * Municipality ID, view details for verification, and confirm the permanent deletion
 * of the record from the database.
 */
public class DeleteMunicipalityController {

    // Data Access Object
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO();
    
    // In-memory cache for fast lookup
    private Map<Integer, MunicipalityModel> allMunicipalitiesMap; 
    private MunicipalityModel selectedMunicipality;

    // FXML Injections
    @FXML private ChoiceBox<Integer> municipalityIDChoiceBox;
    @FXML private Label municipalityNameLabel;
    @FXML private Label provinceRegionLabel;
    @FXML private Label classificationLabel;
    @FXML private Label contactLabel;
    
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    /**
     * Initializes the controller, loads all municipality data, and sets up the listener.
     */
    @FXML
    public void initialize() {
        confirmButton.setDisable(true);
        loadAllMunicipalitiesAndSetupUI();
        
        // Listener for when a Municipality ID is selected
        municipalityIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                
                loadMunicipalityDataFromMap(newValue);
            } else {
                clearDetails();
            }
        });
    }

    /**
     * Loads all Municipality records from the database into the map and populates the ChoiceBox.
     */
    private void loadAllMunicipalitiesAndSetupUI() {
        try {
           
            List<MunicipalityModel> municipalitiesList = municipalityDAO.getMunicipalities();
            
            if (municipalitiesList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Municipalities", "There are no municipalities currently registered to delete.");
                allMunicipalitiesMap = Map.of(); 
            } else {
               
                allMunicipalitiesMap = municipalitiesList.stream()
                    .collect(Collectors.toMap(MunicipalityModel::getMunicipalityID, municipality -> municipality));
            }
            
          
            List<Integer> ids = allMunicipalitiesMap.keySet().stream()
                                                .sorted()
                                                .collect(Collectors.toList());

            municipalityIDChoiceBox.setItems(FXCollections.observableArrayList(ids));
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Load Error", "Failed to load all municipality data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches and displays the details of the selected municipality ID from the in-memory map.
     * @param municipalityID The ID of the municipality to load.
     */
    private void loadMunicipalityDataFromMap(int municipalityID) {
        clearDetails();
        
        selectedMunicipality = allMunicipalitiesMap.get(municipalityID);
        
        if (selectedMunicipality != null) {
            
            municipalityNameLabel.setText(selectedMunicipality.getMunicipalityName());
            provinceRegionLabel.setText(selectedMunicipality.getProvince() + " (" + selectedMunicipality.getRegion() + ")");
            classificationLabel.setText(selectedMunicipality.getClassification());
            contactLabel.setText(selectedMunicipality.getContactNumber());
            
            confirmButton.setDisable(false);
        } else {
            showAlert(Alert.AlertType.WARNING, "Not Found", "Could not find municipality with ID: " + municipalityID);
            confirmButton.setDisable(true);
        }
    }

    /**
     * Clears all detail labels and resets the selected municipality model.
     */
    private void clearDetails() {
        municipalityNameLabel.setText("");
        provinceRegionLabel.setText("");
        classificationLabel.setText("");
        contactLabel.setText("");
        selectedMunicipality = null;
        confirmButton.setDisable(true);
    }

    /**
     * Handles the action when the Confirm Delete button is pressed.
     */
    @FXML
    private void confirmButtonPressed() {
        if (selectedMunicipality == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a Municipality ID to delete.");
            return;
        }

        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Permanent Deletion");
        alert.setHeaderText("Delete Municipality Record?");
        alert.setContentText("Are you sure you want to delete '" + selectedMunicipality.getMunicipalityName() + "' (ID: " + selectedMunicipality.getMunicipalityID() + ")? This action is permanent and may affect linked Inspector and Business records.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Execute Deletion
            try {
                int municipalityIdToDelete = selectedMunicipality.getMunicipalityID();
                municipalityDAO.deleteMunicipality(municipalityIdToDelete);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Municipality " + municipalityIdToDelete + " has been successfully deleted.");
                
               
                clearDetails();
                municipalityIDChoiceBox.getSelectionModel().clearSelection();
                loadAllMunicipalitiesAndSetupUI(); 
                
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete municipality: " + e.getMessage() + ". Check for dependent records (Inspectors/Businesses).");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the action when the Cancel button is pressed, closing the window.
     */
    @FXML
    private void cancelButtonPressed() {
        // Get the current stage (window) and close it
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Helper method to display JavaFX Alert dialogs.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}