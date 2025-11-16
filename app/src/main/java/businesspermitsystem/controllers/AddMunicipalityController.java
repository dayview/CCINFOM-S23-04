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

public class AddMunicipalityController {

    // Data Access Object
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO();

    // FXML Injections
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

    // Standard Philippine LGU Classifications
    private static final List<String> CLASSIFICATIONS = Arrays.asList(
        "First Class City", "Second Class City", "Third Class City", "Fourth Class City", "Fifth Class City", "Sixth Class City",
        "First Class Municipality", "Second Class Municipality", "Third Class Municipality", "Fourth Class Municipality", "Fifth Class Municipality", "Sixth Class Municipality",
        "Highly Urbanized City"
    );

    @FXML
    public void initialize() {
        classificationChoiceBox.setItems(FXCollections.observableArrayList(CLASSIFICATIONS));
        classificationChoiceBox.getSelectionModel().select("First Class City"); 
    }

    /**
     * Handles the confirmation of adding a new municipality.
     * Performs basic validation, creates the model, and calls the DAO to save it.
     */
    @FXML
    public void confirmButtonPressed() {

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
           
            MunicipalityModel newMunicipality = new MunicipalityModel(
                0,
                name,
                province,
                region,
                classification, 
                contact,
                street,
                barangay,
                zipCode
            );

            municipalityDAO.addMunicipality(newMunicipality);

            showAlert(AlertType.INFORMATION, "Success", "Municipality '" + name + "' added successfully!");
            
            cancelButtonPressed(); 

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to add municipality: " + e.getMessage());
            e.printStackTrace();
        }
    Stage currentStage = (Stage) confirmButton.getScene().getWindow();
    SceneManager sceneManager = new SceneManager(currentStage);
    sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }

    /**
     * Handles the cancel button press by closing the current window (Stage).
     */
    @FXML
    public void cancelButtonPressed() {
        Stage currentStage = (Stage) confirmButton.getScene().getWindow();
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