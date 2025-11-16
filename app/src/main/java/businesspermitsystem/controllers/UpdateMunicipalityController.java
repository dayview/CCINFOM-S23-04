package businesspermitsystem.controllers;

import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.MunicipalityModel;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for updating an existing Municipality record.
 * Allows selection of a Municipality ID and modification of its details.
 */
public class UpdateMunicipalityController {

    // Data Access Object
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO();
    private MunicipalityModel selectedMunicipality = null;

    // FXML Injections
    @FXML private ChoiceBox<Integer> municipalityIDChoiceBox;
    
    @FXML private TextField municipalityNameTextField;
    @FXML private TextField provinceTextField;
    @FXML private TextField regionTextField;
    @FXML private ChoiceBox<String> classificationChoiceBox; 
    @FXML private TextField contactNumberTextField;
    @FXML private TextField officeStreetTextField;
    @FXML private TextField officeBarangayTextField;
    @FXML private TextField officeZipcodeTextField;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    
    // Standard LGU Classifications
    private static final List<String> CLASSIFICATIONS = Arrays.asList(
        "First Class City", "Second Class City", "Third Class City", "Fourth Class City", 
        "Fifth Class City", "Sixth Class City", "First Class Municipality", 
        "Second Class Municipality", "Third Class Municipality", "Fourth Class Municipality", 
        "Fifth Class Municipality", "Sixth Class Municipality", "Highly Urbanized City"
    );

    /**
     * Initializes the controller, loads IDs, sets up the classification ChoiceBox,
     * and adds a listener to load data when an ID is selected.
     */
    @FXML
    public void initialize() {
        
        classificationChoiceBox.setItems(FXCollections.observableArrayList(CLASSIFICATIONS));
        
        
        loadMunicipalityIDs();
        
        
        municipalityIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadMunicipalityDetails(newValue);
                confirmButton.setDisable(false);
            } else {
                clearFields();
                confirmButton.setDisable(true);
            }
        });
        
        confirmButton.setDisable(true);
    }
    
    /**
     * Fetches all Municipality IDs from the database and populates the ID ChoiceBox.
     */
    private void loadMunicipalityIDs() {
        try {
            List<Integer> ids = municipalityDAO.getMunicipalities().stream()
                                .map(MunicipalityModel::getMunicipalityID)
                                .collect(Collectors.toList());
            
            municipalityIDChoiceBox.setItems(FXCollections.observableArrayList(ids));
            
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Load Error", "Failed to load Municipality IDs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches and displays the details of the selected municipality ID.
     * @param municipalityID The ID of the municipality to load.
     */
    private void loadMunicipalityDetails(int municipalityID) {
        clearFields();
        try {
            selectedMunicipality = municipalityDAO.getMunicipalityById(municipalityID);
            
            if (selectedMunicipality != null) {
                municipalityNameTextField.setText(selectedMunicipality.getMunicipalityName());
                provinceTextField.setText(selectedMunicipality.getProvince());
                regionTextField.setText(selectedMunicipality.getRegion());
                contactNumberTextField.setText(selectedMunicipality.getContactNumber());
                officeStreetTextField.setText(selectedMunicipality.getOfficeStreet());
                officeBarangayTextField.setText(selectedMunicipality.getOfficeBarangay());
                officeZipcodeTextField.setText(selectedMunicipality.getOfficeZipCode());
                
                // Select the classification if it exists in the list
                String currentClassification = selectedMunicipality.getClassification();
                if (currentClassification != null && CLASSIFICATIONS.contains(currentClassification)) {
                     classificationChoiceBox.getSelectionModel().select(currentClassification);
                } else {
                    classificationChoiceBox.getSelectionModel().clearSelection();
                }
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to load details for ID " + municipalityID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clears all input fields and resets the selected municipality.
     */
    private void clearFields() {
        municipalityNameTextField.clear();
        provinceTextField.clear();
        regionTextField.clear();
        classificationChoiceBox.getSelectionModel().clearSelection();
        contactNumberTextField.clear();
        officeStreetTextField.clear();
        officeBarangayTextField.clear();
        officeZipcodeTextField.clear();
        selectedMunicipality = null;
    }

    /**
     * Handles the update button press, saving changes to the database.
     */
    @FXML
    public void confirmButtonPressed() {
        if (selectedMunicipality == null) {
            showAlert(AlertType.ERROR, "Selection Error", "Please select a Municipality ID to update.");
            return;
        }

        
        String name = municipalityNameTextField.getText();
        String province = provinceTextField.getText();
        String region = regionTextField.getText();
        String contact = contactNumberTextField.getText(); 
        String classification = classificationChoiceBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || province.isEmpty() || region.isEmpty() || contact.isEmpty() || classification == null) {
            showAlert(AlertType.ERROR, "Validation Error", "Please fill in all required fields (Name, Province, Region, Contact Number, Classification).");
            return;
        }
        
        
        String street = officeStreetTextField.getText().isEmpty() ? null : officeStreetTextField.getText();
        String barangay = officeBarangayTextField.getText().isEmpty() ? null : officeBarangayTextField.getText();
        String zipCode = officeZipcodeTextField.getText().isEmpty() ? null : officeZipcodeTextField.getText();
        
        try {
           
            MunicipalityModel updatedMunicipality = new MunicipalityModel(
                selectedMunicipality.getMunicipalityID(), 
                name,
                province,
                region,
                classification,
                contact,
                street,
                barangay,
                zipCode
            );

        
            municipalityDAO.updateMunicipality(updatedMunicipality);

            showAlert(AlertType.INFORMATION, "Success", "Municipality '" + name + "' (ID: " + updatedMunicipality.getMunicipalityID() + ") updated successfully!");
            
           
            clearFields();
            municipalityIDChoiceBox.getSelectionModel().clearSelection();
            loadMunicipalityIDs();
            
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to update municipality: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the cancel button press by closing the current window (Stage).
     */
    @FXML
    public void cancelButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
    
    /**
     * Helper method to display JavaFX Alert dialogs.
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}