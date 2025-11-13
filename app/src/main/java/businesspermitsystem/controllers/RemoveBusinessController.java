package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Remove Business view.
 * Handles user input and triggers deletion of a business record
 * based on the provided Business ID.
 */
public class RemoveBusinessController {

    @FXML
    private TextField businessIdField;

    private final BusinessDAO businessDAO = new BusinessDAO();

    /**
     * Called when the user clicks the "Remove" button.
     * Attempts to delete the business with the specified ID.
     */
    @FXML
    private void handleRemoveBusiness() {
        try {
            int id = Integer.parseInt(businessIdField.getText().trim());
            boolean deleted = businessDAO.deleteBusiness(id);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Remove Business");
            alert.setHeaderText(null);
            alert.setContentText(deleted ? "Business successfully removed!" : "No business found with the provided ID.");
            alert.showAndWait();

            if (deleted) {
                Stage stage = (Stage) businessIdField.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(stage);
                sceneManager.switchScene("/view/mainView.fxml", "Business Permit System");
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric Business ID.");
        } catch (Exception e) {
            showError("An error occurred while deleting the business:\n" + e.getMessage());
        }
    }

    /**
     * Called when the user clicks the "Cancel" button.
     * Returns to the main dashboard.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) businessIdField.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/mainView.fxml", "Business Permit System");
    }

    /**
     * Displays an error message in an alert box.
     *
     * @param message error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
