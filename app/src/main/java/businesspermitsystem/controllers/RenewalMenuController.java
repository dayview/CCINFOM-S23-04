package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class RenewalMenuController {
    @FXML private Button ApplyRenewalButton;
    @FXML private Button RecordPaymentButton;
    @FXML private Button ScheduleInspectionButton;
    @FXML private Button FinalizeRenewalButton;

    @FXML 
    private void applyRenewalButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) ApplyRenewalButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ApplyRenewalButton.fxml", "Apply for Renewal");
    }

    @FXML
    private void recordPaymentButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) RecordPaymentButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/RecordPaymentButton.fxml", "Record Payment");
    }

    @FXML
    private void scheduleInspectionPressed(ActionEvent event) {
        Stage currentStage = (Stage) ScheduleInspectionButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ScheduleInspectionButton.fxml", "Schedule Inspection");
    }

    @FXML
    private void finalizeRenewalButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) FinalizeRenewalButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/FinalizeRenewalButton.fxml", "Finalize Renewal");
    }
}
