package businesspermitsystem.controllers;

import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * controller for deleting an existing Owner record
 * allows selection of Owner id and displays details for verification before deletion
 */
public class DeleteOwnerController {

    @FXML private ChoiceBox<Integer> ownerIDChoiceBox;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField contactNoField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> govIdTypeComboBox;
    @FXML private TextField govIdNoField;
    @FXML private TextField tinField;
    @FXML private TextField homeAddressField;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private final OwnerDAO ownerDAO = new OwnerDAO();
    private OwnerModel selectedOwner = null;

    @FXML
    public void initialize() {
        // load dropdown options for gov ids
        govIdTypeComboBox.getItems().addAll("Passport", "Driver's License", "Professional Regulation Commission (PRC) ID",
        "Postal ID", "Voter's ID", "Unified Multi-Purpose ID (UMID)", "NBI Clearance", "Police Clearance",
        "SSS ID", "Senior Citizen ID", "Government Service Insurance System (GSIS) ID");
        
        // load owner ids
        loadOwnerIDs();
        
        // listener for owner id selection
        ownerIDChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadOwnerDetails(newValue);
                confirmButton.setDisable(false);
            } else {
                clearFields();
                confirmButton.setDisable(true);
            }
        });
        
        confirmButton.setDisable(true);
    }
    
    /**
     * gets all Owner ids from the database
     */
    private void loadOwnerIDs() {
        List<OwnerModel> owners = ownerDAO.getAllOwners();
        List<Integer> ids = owners.stream()
                            .map(OwnerModel::getOwnerID)
                            .collect(Collectors.toList());
        
        ownerIDChoiceBox.setItems(FXCollections.observableArrayList(ids));
    }

    /**
     * gets and displays the details of the selected owner id
     */
    private void loadOwnerDetails(int ownerID) {
        clearFields();
        
        selectedOwner = ownerDAO.getOwnerByID(ownerID);
        
        if (selectedOwner != null) {
            lastNameField.setText(selectedOwner.getLastName());
            firstNameField.setText(selectedOwner.getFirstName());
            middleNameField.setText(selectedOwner.getMiddleName());
            contactNoField.setText(selectedOwner.getContactNo());
            emailField.setText(selectedOwner.getEmail());
            govIdNoField.setText(selectedOwner.getGovID_no());
            tinField.setText(selectedOwner.getTin());
            homeAddressField.setText(selectedOwner.getHomeAddress());
            
            // Select the government ID type if it exists
            String currentGovIdType = selectedOwner.getGovID_type();
            if (currentGovIdType != null && govIdTypeComboBox.getItems().contains(currentGovIdType)) {
                govIdTypeComboBox.getSelectionModel().select(currentGovIdType);
            } else {
                govIdTypeComboBox.getSelectionModel().clearSelection();
            }
        }
    }

    /**
     * clears all input fields and resets the selected owner
     */
    private void clearFields() {
        lastNameField.clear();
        firstNameField.clear();
        middleNameField.clear();
        contactNoField.clear();
        emailField.clear();
        govIdTypeComboBox.getSelectionModel().clearSelection();
        govIdNoField.clear();
        tinField.clear();
        homeAddressField.clear();
        selectedOwner = null;
    }

    /**
     * handles the delete button press, permanently removing the record from the database
     */
    @FXML
    private void confirmButtonPressed() {
        if (selectedOwner == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an Owner ID to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Permanent Deletion");
        alert.setHeaderText("Delete Owner Record?");
        alert.setContentText("Are you sure you want to delete owner '" + selectedOwner.getFirstName() + " " + 
            selectedOwner.getLastName() + "' (ID: " + selectedOwner.getOwnerID() + ")? This action is permanent and may affect linked Application and Business records.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = ownerDAO.deleteOwner(selectedOwner.getOwnerID());

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Owner successfully deleted!");
                    
                    // Refresh and clear
                    clearFields();
                    ownerIDChoiceBox.getSelectionModel().clearSelection();
                    loadOwnerIDs();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete owner. The owner may have linked records.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * closes window when cancel is pressed
     */
    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * Utility method for showing alerts.
     * Source: https://www.geeksforgeeks.org/java/javafx-alert-with-examples/
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}