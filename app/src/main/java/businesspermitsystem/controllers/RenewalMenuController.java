package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RenewalMenuController {
    @FXML private Button applyRenewalButton;
    @FXML private Button recordPaymentButton;
    @FXML private Button scheduleInspectionButton;
    @FXML private Button finalizeRenewalButton;

    @FXML
    private void applyRenewalButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) applyRenewalButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ApplyRenewalButton.fxml", "Apply for Renewal");
    }

    @FXML
    private void recordPaymentButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) recordPaymentButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/RecordPaymentButton.fxml", "Record Payment");
    }

    @FXML
    private void scheduleInspectionPressed(ActionEvent event) {
        Stage currentStage = (Stage) scheduleInspectionButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ScheduleInspectionButton.fxml", "Schedule Inspection");
    }

    @FXML
    private void finalizeRenewalButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) finalizeRenewalButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/FinalizeRenewalButton.fxml", "Finalize Renewal");
    }
}
