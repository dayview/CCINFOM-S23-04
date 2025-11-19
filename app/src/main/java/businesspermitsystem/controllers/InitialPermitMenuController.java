package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the Initial Permit Menu.
 *
 * Provides navigation to:
 * - Applying for a new permit
 * - Viewing applications ready for payment
 * - Scheduling an inspector
 * - Recording inspection results
 * - Updating permit status
 * - Returning to the main menu
 */
public class InitialPermitMenuController {

    @FXML private Button applyPermitButton;
    @FXML private Button paymentListButton;
    @FXML private Button scheduleInspectorButton;
    @FXML private Button recordInspectionButton;
    @FXML private Button updatePermitStatusButton;
    @FXML private Button backButton;

    /**
     * Navigates to the initial permit application screen.
     */
    @FXML
    private void handleApply() {
        Stage currentStage = (Stage) applyPermitButton.getScene().getWindow();
        new SceneManager(currentStage).switchScene("/view/InitialPermitIssuanceView.fxml", "Initial Permit");
    }

    /**
     * Navigates to the list of permit applications that require payment.
     */
    @FXML
    private void handlePaymentList() {
        Stage currentStage = (Stage) paymentListButton.getScene().getWindow();
        new SceneManager(currentStage).switchScene("/view/PermitPaymentListView.fxml", "Applications for Payment");
    }

    /**
     * Navigates to the inspector scheduling interface.
     */
    @FXML
    private void handleScheduleInspector() {
        Stage stage = (Stage) scheduleInspectorButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/InspectorScheduleView.fxml", "Schedule Inspection");
    }

    /**
     * Navigates to the inspection result recording screen.
     */
    @FXML
    private void handleRecordInspection() {
        Stage stage = (Stage) recordInspectionButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/RecordInspectorResult.fxml", "Record Inspection Result");
    }

    /**
     * Navigates to the permit status update screen.
     */
    @FXML
    private void handleUpdateStatus() {
        Stage stage = (Stage) updatePermitStatusButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/PermitStatusUpdateView.fxml", "Update Permit Status");
    }

    /**
     * Returns the user to the main menu.
     */
    @FXML
    private void handleBack() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        new SceneManager(currentStage).switchScene("/view/MainView.fxml", "Main Menu");
    }
}
