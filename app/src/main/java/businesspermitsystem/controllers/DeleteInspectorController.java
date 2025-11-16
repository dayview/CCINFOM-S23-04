package businesspermitsystem.controllers;

import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.InspectorModel;
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
 * Controller for the Delete Inspector UI. Allows the user to select an
 * Inspector ID, view details for verification, and confirm the permanent deletion
 * of the inspector record from the database.
 * NOTE: This version is optimized to fetch all inspector data once upon initialization
 * by utilizing InspectorDAO.getInspectors() and converting the list to a Map for fast lookup.
 */
public class DeleteInspectorController {

    // FXML Injections from the .fxml file
    @FXML private ChoiceBox<Integer> inspectorIDChoiceBox;
    @FXML private Label lastNameLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label middleNameLabel;
    @FXML private Label designationLabel;
    @FXML private Label licenseNumberLabel;
    @FXML private Label officeLocationLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    // Data Access Objects and State
    private InspectorDAO inspectorDAO;
    private MunicipalityDAO municipalityDAO;
    private Map<Integer, InspectorModel> allInspectorsMap; 
    private InspectorModel selectedInspector;

    /**
     * Initializes the controller. Sets up DAOs, loads all inspector IDs,
     * and adds a listener to the ChoiceBox to load data on selection.
     */
    @FXML
    public void initialize() {
        inspectorDAO = new InspectorDAO();
        municipalityDAO = new MunicipalityDAO();
        confirmButton.setDisable(true);
        
        loadAllInspectorsAndSetupUI();
        
       
        inspectorIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                
                loadInspectorDataFromMap(newValue);
            } else {
                clearInspectorDetails();
            }
        });
    }

    /**
     * Loads all Inspector records from the database using inspectorDAO.getInspectors(),
     * converts the list into an internal map, and populates the ChoiceBox.
     */
    private void loadAllInspectorsAndSetupUI() {

        try {
            List<InspectorModel> inspectorsList = inspectorDAO.getInspectors();
            
            if (inspectorsList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Inspectors", "There are no inspectors currently registered to delete.");
                allInspectorsMap = Map.of(); 
            } else {
             
                allInspectorsMap = inspectorsList.stream()
                    .collect(Collectors.toMap(InspectorModel::getInspectorID, inspector -> inspector));
            }
            
            
            List<Integer> ids = allInspectorsMap.keySet().stream()
                                                .sorted()
                                                .collect(Collectors.toList());

            inspectorIDChoiceBox.setItems(FXCollections.observableArrayList(ids));
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Load Error", "Failed to load all inspector data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches and displays the details of the selected inspector ID from the 
     * in-memory map. No new database query is performed here.
     * @param inspectorID The ID of the inspector to load.
     */
    private void loadInspectorDataFromMap(int inspectorID) {
        clearInspectorDetails();
        
        
        selectedInspector = allInspectorsMap.get(inspectorID);
        
        try {
            if (selectedInspector != null) {
            
                MunicipalityModel officeLocation = municipalityDAO.getMunicipalityById(selectedInspector.getMunicipalityID());

              
                lastNameLabel.setText(selectedInspector.getLastName());
                firstNameLabel.setText(selectedInspector.getFirstName());
                middleNameLabel.setText(selectedInspector.getMiddleName());
                designationLabel.setText(selectedInspector.getDesignation());
                licenseNumberLabel.setText(selectedInspector.getLicenseNumber());

                if (officeLocation != null) {
                    officeLocationLabel.setText(officeLocation.getMunicipalityName() + ", " + officeLocation.getRegion());
                } else {
                    officeLocationLabel.setText("N/A (Municipality ID: " + selectedInspector.getMunicipalityID() + ")");
                }
                
                confirmButton.setDisable(false);
            } else {
                showAlert(Alert.AlertType.WARNING, "Inspector Not Found", "Could not find inspector with ID: " + inspectorID);
                confirmButton.setDisable(true);
            }
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Query Error", "Failed to retrieve municipality details: " + e.getMessage());
            confirmButton.setDisable(true);
            e.printStackTrace();
        }
    }

    /**
     * Clears all detail labels and resets the selected inspector model.
     */
    private void clearInspectorDetails() {
        lastNameLabel.setText("");
        firstNameLabel.setText("");
        middleNameLabel.setText("");
        designationLabel.setText("");
        licenseNumberLabel.setText("");
        officeLocationLabel.setText("");
        selectedInspector = null;
        confirmButton.setDisable(true);
    }

    /**
     * Handles the action when the Confirm Delete button is pressed.
     * Displays a confirmation dialog before proceeding with deletion.
     */
    @FXML
    private void confirmButtonPressed() {
        if (selectedInspector == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select an Inspector ID to delete.");
            return;
        }

        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Permanently Delete Inspector?");
        alert.setContentText("Are you sure you want to delete Inspector " + selectedInspector.getFirstName() + " " + selectedInspector.getLastName() + " (ID: " + selectedInspector.getInspectorID() + ")? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
        
            try {
                int inspectorIdToDelete = selectedInspector.getInspectorID();
                inspectorDAO.deleteInspector(inspectorIdToDelete);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Inspector " + inspectorIdToDelete + " has been successfully deleted.");
                
                
                clearInspectorDetails();
                inspectorIDChoiceBox.getSelectionModel().clearSelection();
                
               
                loadAllInspectorsAndSetupUI(); 
                
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete inspector: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the action when the Cancel button is pressed, closing the window.
     */
    @FXML
    private void cancelButtonPressed() {
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