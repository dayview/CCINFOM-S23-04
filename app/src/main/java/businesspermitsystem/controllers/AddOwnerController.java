package businesspermitsystem.controllers;

import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * this controller adds new Owner record
 * handles user input validation and save to the database
 */
public class AddOwnerController {

    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField contactNoField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> govIdTypeComboBox;
    @FXML private TextField govIdNoField;
    @FXML private TextField tinField;
    @FXML private TextField homeAddressField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final OwnerDAO ownerDAO = new OwnerDAO();

    @FXML
    public void initialize() {
        // load dropdown options of ids
        govIdTypeComboBox.getItems().addAll("Passport", "Driver's License", "Professional Regulation Commission (PRC) ID",
        "Postal ID", "Voter's ID", "Unified Multi-Purpose ID (UMID)", "NBI Clearance", "Police Clearance",
        "SSS ID", "Senior Citizen ID", "Government Service Insurance System (GSIS) ID");
    }
    
    /**
     * save new Owner record using the input fields
     */
    @FXML
    private void onSave(ActionEvent event) {
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

            // create new owner from the user inputs
            OwnerModel owner = new OwnerModel(
                0, // auto gen
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

            boolean success = ownerDAO.addOwner(owner);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Owner successfully added!");
                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add owner. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * close window when cancel is pressed
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * validates email format using simple regex pattern
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
        // Allow formats like: +63 912 345 6789, (02) 1234-5678, 09123456789, etc.
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