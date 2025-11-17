package businesspermitsystem.controllers;

import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddOwnerController {

    @FXML private TextField txtLastName;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtMiddleName;
    @FXML private TextField txtContactNo;
    @FXML private TextField txtEmail;
    @FXML private TextField txtGovIDType;
    @FXML private TextField txtGovIDNo;
    @FXML private TextField txtTIN;
    @FXML private TextField txtHomeAddress;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final OwnerDAO ownerDAO = new OwnerDAO();

    @FXML
    private void handleSave() {
        try {
            // checks if the inputs are valid or not
            if (txtLastName.getText().isEmpty() || txtFirstName.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "First name and last name are required.");
                return;
            }

            if (txtContactNo.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "Contact number is required.");
                return;
            }

            if (txtEmail.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "Email is required.");
                return;
            }

            // create a new owner model
            OwnerModel owner = new OwnerModel(
                    0,
                    txtLastName.getText(),
                    txtFirstName.getText(),
                    txtMiddleName.getText(),
                    txtContactNo.getText(),
                    txtEmail.getText(),
                    txtGovIDType.getText(),
                    txtGovIDNo.getText(),
                    txtTIN.getText(),
                    txtHomeAddress.getText()
            );

            boolean success = ownerDAO.addOwner(owner);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Owner successfully added!");

                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                SceneManager sm = new SceneManager(currentStage);
                sm.switchScene("/view/MainView.fxml", "Main Menu");

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add owner. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sm = new SceneManager(currentStage);
        sm.switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * Helper method to show alerts.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
