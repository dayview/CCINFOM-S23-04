package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 
 */
public class AddInspectorController {

    /**
     * 
     */
    @FXML private TextField lastNameTextField;
    /**
     * 
     */
    @FXML private TextField firstNameTextField;
    /**
     * 
     */
    @FXML private TextField middleNameTextField;
    /**
     * 
     */
    @FXML private TextField designationTextField;
    /**
     * 
     */
    @FXML private TextField licenseNumberTextField;
    /**
     * 
     */
    @FXML private Button officeLocationButton;
    /**
     * 
     */
    @FXML private Button confirmButton;
    /**
     * 
     */
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {

    }

    @FXML
    private void officeLocationButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/SelectMunicipalityView.fxml", "Select Municipality");
    }

    @FXML
    private void confirmButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }

    @FXML
    private void cancelButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
}
