package businesspermitsystem.controllers;

import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * 
 */
public class InspectionScheduleController {
    @FXML private Button createScheduleButton;
    @FXML private Button reportFindingsButton;
    @FXML private Button cancelButton;


    @FXML
    private void createScheduleButtonPressed() {
        Stage currentStage = (Stage) createScheduleButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/CreateScheduleView.fxml", "Create Schedule");
    }
    @FXML
    private void reportFindingsButtonPressed() {
        Stage currentStage = (Stage) reportFindingsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/ReportFindingsView.fxml", "Report Findings");
    }
    @FXML
    private void cancelButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
}
