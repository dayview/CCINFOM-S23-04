package businesspermitsystem.controllers;

import javafx.event.ActionEvent;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * 
 */
public class InspectionScheduleController {
    @FXML private Button createScheduleButton;
    @FXML private Button viewScheduleButton;
    @FXML private Button reportFindingsButton;
    @FXML private Button viewResultsButton;
    @FXML private Button cancelButton;


    @FXML
    private void createScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) createScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ScheduleInspectionView.fxml", "Schedule An Inspection");
    }
    @FXML
    private void viewScheduleButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) reportFindingsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ViewInspectionSchedules.fxml", "Schedules");
    }

    @FXML
    private void reportFindingsButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) reportFindingsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ReportFindingsView.fxml", "Report Findings");
    }

    @FXML
    private void viewResultsButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) reportFindingsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ViewInspectionResults.fxml", "Results");
    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
}
