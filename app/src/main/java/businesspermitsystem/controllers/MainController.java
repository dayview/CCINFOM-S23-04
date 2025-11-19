package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Main controller for the Business Permit System
 */
public class MainController {

    // TRANSACTIONS BUTTONS
    @FXML private Button newBusinessRegistrationButton;
    @FXML private Button initialPermitIssuanceButton;
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
    @FXML private Button updateInspectorButton;
    @FXML private Button deleteInspectorButton;

    // MUNICIPALITY BUTTONS
    @FXML private Button addMunicipalityButton;
    @FXML private Button updateMunicipalityButton;
    @FXML private Button deleteMunicipalityButton;

    // REPORTS BUTTONS
    @FXML private Button generatePaymentsCollectedButton;
    @FXML private Button generateInspectionResultsButton;
    @FXML private Button generatePermitsIssuedButton;
    @FXML private Button generateComprehensiveComplianceButton;

    // ------------------------------------------------------------------------------------------------

    // TRANSACTIONS ACTIONS

    /**
     * Handles the New Business Registration button action
     */
    @FXML
    private void newBusinessRegistrationButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) newBusinessRegistrationButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/BusinessSelectionView.fxml",
                "New Business Registration and Others");
    }

    /**
     * Handles the Initial Permit Issuance button action
     */
    @FXML
    private void initialPermitIssuanceButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) initialPermitIssuanceButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InitialPermitMenuView.fxml",
                "Initial Permit Issuance");
    }

    /**
     * Handles the Permit Renewal button action
     */
    @FXML
    private void permitRenewalButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) permitRenewalButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/RenewalMenuView.fxml", "Permit Renewal Transaction");
    }

    /**
     * Handles the Inspection Schedule button action
     */
    @FXML
    private void inspectionScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) inspectionScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InspectionScheduleView.fxml", "Inspection Schedules And Clearance");
    }

    /**
     * Handles the Business Status Update button action
     */
    @FXML
    private void businessStatusUpdateButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) businessStatusUpdateButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/BusinessStatusUpdate.fxml", "Business Status Update Transaction");
    }

    // BUSINESS ACTIONS

    /**
     * Handles the Add Business button action
     */
    @FXML
    private void addBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addBusinessView.fxml", "Add Business");
    }

    /**
     * Handles the Remove Business button action
     */
    @FXML
    private void removeBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/removeBusinessView.fxml", "Remove Business");
    }

    /**
     * Handles the Update Business button action
     */
    @FXML
    private void updateBusinessButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addBusinessButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateBusinessView.fxml", "Update Business");
    }

    // OWNERS ACTIONS

    /**
     * Handles the Add Owner button action
     */
    @FXML
    private void addOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddOwnerView.fxml", "Add Owner");
    }

    /**
     * Handles the Update Owner button action
     */
    @FXML
    private void updateOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateOwnerView.fxml", "Update Owner");
    }

    /**
     * Handles the Delete Owner button action
     */
    @FXML
    private void deleteOwnerButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addOwnerButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeleteOwnerView.fxml", "Delete Owner");
    }

    // FEE SCHEDULE ACTIONS

    /**
     * Handles the Add Fee Schedule button action
     */
    @FXML
    private void addFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddFeeSchedule.fxml", "Add Fee Schedule");
    }

    /**
     * Handles the Update Fee Schedule button action
     */
    @FXML
    private void updateFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdateFeeSchedule.fxml", "Update Fee Schedule");
    }

    /**
     * Handles the Delete Fee Schedule button action
     */
    @FXML
    private void deleteFeeScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteFeeScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeleteFeeSchedule.fxml", "Delete Fee Schedule");
    }

    // PERMIT TYPE ACTIONS

    /**
     * Handles the Add Permit Type button action
     */
    @FXML
    private void addPermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addPermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddPermitType.fxml", "Add Permit Type");
    }

    /**
     * Handles the Update Permit Type button action
     */
    @FXML
    private void updatePermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updatePermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdatePermitType.fxml", "Update Permit Type");
    }

    /**
     * Handles the Delete Permit Type button action
     */
    @FXML
    private void deletePermitTypeButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deletePermitTypeButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeletePermitType.fxml", "Delete Permit Type");
    }

    // PERMIT ACTIONS

    /**
     * Handles the Add Permit button action
     */
    @FXML
    private void addPermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addPermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/AddPermit.fxml", "Add Permit");
    }

    /**
     * Handles the Update Permit button action
     */
    @FXML
    private void updatePermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updatePermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/UpdatePermit.fxml", "Update Permit");
    }

    /**
     * Handles the Delete Permit button action
     */
    @FXML
    private void deletePermitButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deletePermitButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/DeletePermit.fxml", "Delete Permit");
    }

    // INSPECTORS ACTIONS

    /**
     * Handles the Add Inspector button action
     */
    @FXML
    private void addInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addInspectorView.fxml", "Add Inspector");
    }

    /**
     * Handles the Update Inspector button action
     */
    @FXML
    private void updateInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/updateInspectorView.fxml", "Update Inspector");
    }

    /**
     * Handles the Delete Inspector button action
     */
    @FXML
    private void deleteInspectorButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteInspectorButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/deleteInspectorView.fxml", "Delete Inspector");
    }

    // MUNICIPALITY ACTIONS

    /**
     * Handles the Add Municipality button action
     */
    @FXML
    private void addMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) addMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/addMunicipalityView.fxml", "Add Municipality");
    }

    /**
     * Handles the Update Municipality button action
     */
    @FXML
    private void updateMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) updateMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/updateMunicipalityView.fxml", "Update Municipality");
    }

    /**
     * Handles the Delete Municipality button action
     */
    @FXML
    private void deleteMunicipalityButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) deleteMunicipalityButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/deleteMunicipalityView.fxml", "Delete Municipality");
    }

    // REPORTS ACTIONS

    /**
     * Handles the Generate Payments Collected Report button action
     */
    @FXML
    private void generatePaymentsCollectedButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) generatePaymentsCollectedButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/PaymentsCollectedReportView.fxml", "Payments Collected Report");
    }

    /**
     * Handles the Generate Inspection Results Report button action
     */
    @FXML
    private void generateInspectionResultsButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) generateInspectionResultsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InspectionResultsView.fxml", "Inspection Results Report");
    }

    /**
     * Handles the Generate Permits Issued Report button action
     */
    @FXML
    private void generatePermitsIssuedButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) generatePermitsIssuedButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/PermitsIssuedReportView.fxml", "Permits Issued Report");
    }

    /**
     * Handles the Generate Comprehensive Compliance Report button action
     */
    @FXML
    private void generateComprehensiveComplianceButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) generateComprehensiveComplianceButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ComprehensiveComplianceReportView.fxml", "Comprehensive Compliance Report");
    }
}