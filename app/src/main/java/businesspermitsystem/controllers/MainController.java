package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * 
 */
public class MainController {
    // TRANSACTIONS BUTTONS

    /**
     * 
     */
    @FXML private Button newBusinessRegistrationButton;

    @FXML private Button initialPermitIssuanceButton;

    /**
     * 
     */
    @FXML private Button permitRenewalButton;

    /**
     * 
     */
    @FXML private Button inspectionScheduleButton;

    /**
     * 
     */
    @FXML private Button businessStatusUpdateButton;

    // BUSINESS BUTTONS

    @FXML private Button addBusinessButton;

    @FXML private Button removeBusinessButton;

    @FXML private Button updateBusinessButton;

    // OWNERS BUTTONS
    @FXML private Button addOwnerButton;

    // PERMIT TYPE AND FEE SCHEDULE BUTTONS

    // INSPECTORS BUTTONS

    /**
     * 
     */
    @FXML private Button addInspectorButton;

    /**
     * 
     */
    @FXML private Button updateInspectorButton;

    /**
     * 
     */
    @FXML private Button deleteInspectorButton;

    // MUNICIPALITY BUTTONS

    /**
     * 
     */
    @FXML private Button addMunicipalityButton;

    /**
     * 
     */
    @FXML private Button updateMunicipalityButton;

    /**
     * 
     */
    @FXML private Button deleteMunicipalityButton;
    

    // ------------------------------------------------------------------------------------------------

    // TRANSACTIONS ACTIONS

    @FXML
    private void newBusinessRegistrationButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) newBusinessRegistrationButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/BusinessSelectionView.fxml", "New Business Registration and Others");
    }


    @FXML
    private void initialPermitIssuanceButtonPressed(ActionEvent event){
        Stage currentStage = (Stage) initialPermitIssuanceButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InitialPermitIssuanceView.fxml", "Initital Permit Issuance");
    }

    @FXML
    private void permitRenewalButtonPressed(ActionEvent event) {

    }

    @FXML
    private void inspectionScheduleButtonPressed(ActionEvent event) {

    }

    @FXML
    private void businessStatusUpdateButtonPressed(ActionEvent event) {

    }
    // BUSINESS ACTIONS
    @FXML
    private void addBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addBusinessView.fxml", "Add Business");
    }

    @FXML
    private void removeBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/removeBusinessView.fxml", "Remove Business");
    }

    @FXML
    private void updateBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateBusinessView.fxml", "Update Business");
    }



    // OWNERS ACTIONS
    @FXML
    private void addOwnerButtonPressed(ActionEvent event){
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addOwnerView.fxml", "Add Owner");
    }


    // PERMIT TYPE AND FEE SCHEDULE ACTIONS

    // INSPECTORS ACTIONS

    /**
     * 
     */
    @FXML
    private void addInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addInspectorView.fxml", "Add Inspector");
    }

    /**
     * 
     */
    @FXML
    private void updateInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/updateInspectorView.fxml", "Update Inspector");
    }

    /**
     * 
     */
    @FXML
    private void deleteInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/deleteInspectorView.fxml", "Delete Inspector");
    }
    
    // MUNICIPALITY ACTIONS

    /**
     * 
     */
    @FXML
    private void addMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addMunicipalityView.fxml", "Add Municipality");
    }

    /**
     * 
     */
    @FXML
    private void updateMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/updateMunicipalityView.fxml", "Update Municipality");
    }

    /**
     * 
     */
    @FXML
    private void deleteMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/deleteMunicipalityView.fxml", "Delete Municipality");
    }
}
