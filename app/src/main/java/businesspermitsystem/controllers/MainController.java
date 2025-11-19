package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    @FXML private Button inspectionScheduleButton;
    @FXML private Button businessStatusUpdateButton;

    // BUSINESS BUTTONS
    @FXML private Button addBusinessButton;
    @FXML private Button removeBusinessButton;
    @FXML private Button updateBusinessButton;

    // OWNERS BUTTONS
    @FXML private Button addOwnerButton;
    @FXML private Button updateOwnerButton;
    @FXML private Button deleteOwnerButton;

    // FEE SCHEDULE BUTTONS
    @FXML private Button addFeeScheduleButton;
    @FXML private Button updateFeeScheduleButton;
    @FXML private Button deleteFeeScheduleButton;

    // PERMIT TYPE BUTTONS
    @FXML private Button addPermitTypeButton;
    @FXML private Button updatePermitTypeButton;
    @FXML private Button deletePermitTypeButton;

    // PERMIT BUTTONS
    @FXML private Button addPermitButton;
    @FXML private Button updatePermitButton;
    @FXML private Button deletePermitButton;

    // INSPECTORS BUTTONS
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
        sceneManager.switchScene("/view/BusinessSelectionView.fxml",
                "New Business Registration and Others");
    }

    @FXML
    private void initialPermitIssuanceButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) initialPermitIssuanceButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InitialPermitMenuView.fxml",
                "Initial Permit Issuance");
    }

    @FXML
    private void permitRenewalButtonPressed(ActionEvent event) {

    }

    @FXML
    private void inspectionScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InspectionScheduleView.fxml", "Inspection Schedules And Clearance");
    }

    @FXML
    private void businessStatusUpdateButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) businessStatusUpdateButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/BusinessStatusUpdate.fxml", "Business Status Update Transaction");
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
    private void addOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddOwnerView.fxml", "Add Owner");
    }

    @FXML
    private void updateOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateOwnerView.fxml", "Update Owner");
    }

    @FXML
    private void deleteOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeleteOwnerView.fxml", "Delete Owner");
    }    

    // FEE SCHEDULE ACTIONS
    @FXML
    private void addFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddFeeSchedule.fxml", "Add Fee Schedule");
    }

    @FXML
    private void updateFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateFeeSchedule.fxml", "Update Fee Schedule");
    }

    @FXML
    private void deleteFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeleteFeeSchedule.fxml", "Delete Fee Schedule");
    }

    // PERMIT TYPE ACTIONS
    @FXML
    private void addPermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addPermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddPermitType.fxml", "Add Permit Type");
    }

    @FXML
    private void updatePermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updatePermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdatePermitType.fxml", "Update Permit Type");
    }

    @FXML
    private void deletePermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deletePermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeletePermitType.fxml", "Delete Permit Type");
    }

    // PERMIT ACTIONS
    @FXML
    private void addPermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addPermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddPermit.fxml", "Add Permit");
    }

    @FXML
    private void updatePermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updatePermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdatePermit.fxml", "Update Permit");
    }

    @FXML
    private void deletePermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deletePermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeletePermit.fxml", "Delete Permit");
    }

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