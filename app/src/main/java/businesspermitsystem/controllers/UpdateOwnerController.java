package businesspermitsystem.controllers;

import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * controller for updating an existing Owner record
 * allows selection of an Owner ID and modification of details
 */
public class UpdateOwnerController {

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
        // load dropdown options for ids
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
     * get all Owner ids from the database
     */
    private void loadOwnerIDs() {
        List<OwnerModel> owners = ownerDAO.getAllOwners();
        List<Integer> ids = owners.stream()
                            .map(OwnerModel::getOwnerID)
                            .collect(Collectors.toList());
        
        ownerIDChoiceBox.setItems(FXCollections.observableArrayList(ids));
    }

    /**
     * get and display the details of the selected owner id
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
            
            // select the government id type if it exists
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
     * handles the update button press, saving changes to the database
     */
    @FXML
    private void onConfirm(ActionEvent event) {
        if (selectedOwner == null) {
            showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an Owner ID to update.");
            return;
        }

        try {
            // validate required fields
            if (lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Last name and First name are required.");
                return;
            }

            if (contactNoField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Contact number is required.");
                return;
            }

            // validate email format
            String email = emailField.getText();
            if (!email.isEmpty() && !isValidEmail(email)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
                return;
            }

            // validate contact number format
            String contactNumber = contactNoField.getText();
            if (!isValidContactNo(contactNumber)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid contact number.");
                return;
            }

            // validate gov id type choice
            if (govIdTypeComboBox.getValue() == null || govIdTypeComboBox.getValue().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Government ID type is required.");
                return;
            }

            // create updated owner object
            OwnerModel updatedOwner = new OwnerModel(
                selectedOwner.getOwnerID(),
                lastNameField.getText().trim(),
                firstNameField.getText().trim(),
                middleNameField.getText().trim(),
                contactNoField.getText().trim(),
                emailField.getText().trim(),
                govIdTypeComboBox.getValue(),
                govIdNoField.getText().trim(),
                tinField.getText().trim(),
                homeAddressField.getText().trim()
            );

            boolean success = ownerDAO.updateOwner(updatedOwner);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Owner successfully updated!");
                
                // refresh and clear
                clearFields();
                ownerIDChoiceBox.getSelectionModel().clearSelection();
                loadOwnerIDs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update owner. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * closes window when cancel is pressed
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * validates email format using a simple regex pattern
     * source: https://www.geeksforgeeks.org/java/regular-expressions-in-java/
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * validates contact number format (allows digits, spaces, dashes, parentheses, and plus sign)
     * source: https://www.geeksforgeeks.org/java/regular-expressions-in-java/
     */
    private boolean isValidContactNo(String contactNo) {
        String phoneRegex = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,4}[-\\s.]?[0-9]{1,9}$";
        return contactNo.matches(phoneRegex);
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