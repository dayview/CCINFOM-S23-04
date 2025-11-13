package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controller for adding a new Business record.
 * Handles user input validation and saving to the database.
 */
public class AddBusinessController {

    @FXML private TextField businessNameField;
    @FXML private TextField tradeNameField;
    @FXML private TextField streetAddressField;
    @FXML private TextField barangayField;
    @FXML private TextField businessTypeField;
    @FXML private TextField taxIdField;
    @FXML private TextField municipalityIdField; // for municipality input

    @FXML private DatePicker startDatePicker;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final BusinessDAO businessDAO = new BusinessDAO();

    /**
     * Saves a new Business record using the input fields.
     */
    @FXML
    private void onSave(ActionEvent event) {
        try {
            // Validate required fields
            if (businessNameField.getText().isEmpty() || tradeNameField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Business name and trade name are required.");
                return;
            }

            LocalDate selectedDate = startDatePicker.getValue();
            if (selectedDate == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a start date.");
                return;
            }

            int municipalityId;
            try {
                municipalityId = Integer.parseInt(municipalityIdField.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Municipality ID must be a valid number.");
                return;
            }

            // Create a new BusinessModel from user input
            BusinessModel business = new BusinessModel();
            business.setBusinessName(businessNameField.getText());
            business.setTradeName(tradeNameField.getText());
            business.setStreetAddress(streetAddressField.getText());
            business.setBarangay(barangayField.getText());
            business.setBusinessType(businessTypeField.getText());
            business.setTaxId(taxIdField.getText());
            business.setStartDate(selectedDate);
            business.setStatus("Active"); // default status
            business.setMunicipalityId(municipalityId);

            // Save using DAO
            boolean success = businessDAO.addBusiness(business);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Business successfully added!");
                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add business. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /** Closes the current window when cancel is pressed. */
    @FXML
    private void onCancel(ActionEvent event)  {
        closeWindow();
    }

    /** Utility method to close the current stage. */
    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /** Utility method for showing alerts. */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
