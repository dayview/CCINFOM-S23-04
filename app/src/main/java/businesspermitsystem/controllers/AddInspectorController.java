package businesspermitsystem.controllers;

import java.io.IOException;

import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.models.MunicipalityModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 
 */
public class AddInspectorController {

    private InspectorModel inspectorModel;
    private MunicipalityModel municipalityModel;

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
            try {
                SelectMunicipalityController controller = (SelectMunicipalityController) sceneManager.switchSceneWithController("/view/SelectMunicipalityView.fxml", "Select Municipality");
                controller.setReturnFXMLPath(null);
                controller.setReturnWindowTitle("Add Inspector");
            } catch (IOException e) {
                // TODO: handle exception
            }
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

    public void initializeMunicipality(Object inspectorModel, Object municipalityModel) {
        this.municipalityModel = (MunicipalityModel) municipalityModel;
        this.inspectorModel = (InspectorModel) inspectorModel;

        officeLocationButton.setText(municipalityModel.toString());
    }
 }
