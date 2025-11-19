package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitStatusUpdateDAO;
import businesspermitsystem.models.PermitStatusUpdateModel;
import businesspermitsystem.utils.SceneManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PermitStatusUpdateController {

    @FXML private ComboBox<PermitStatusUpdateModel> businessComboBox;
    @FXML private DatePicker validityStartPicker;
    @FXML private DatePicker validityEndPicker;

    @FXML private Button issueButton;
    @FXML private Button cancelButton;

    private final PermitStatusUpdateDAO dao = new PermitStatusUpdateDAO();

    @FXML
    public void initialize() {
        List<PermitStatusUpdateModel> list = dao.getEligibleForIssuance();

        if (list != null) {
            businessComboBox.getItems().addAll(list);
        }
    }

    @FXML
    private void handleIssuePermit() {

        PermitStatusUpdateModel selected = businessComboBox.getValue();
        LocalDate start = validityStartPicker.getValue();
        LocalDate end = validityEndPicker.getValue();

        if (selected == null || start == null || end == null) {
            showAlert("Missing Fields", "Fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        if (end.isBefore(start)) {
            showAlert("Invalid Date", "Validity end cannot be before start.", Alert.AlertType.WARNING);
            return;
        }

        boolean success = dao.issuePermit(selected.getBusinessId(), selected.getPermitTypeId(), Date.valueOf(start), Date.valueOf(end), "Initial permit issued.");

        if (success) {
            showAlert("Success", "Permit issued for: " + selected.getBusinessName(), Alert.AlertType.INFORMATION);

            Stage s = (Stage) issueButton.getScene().getWindow();
            new SceneManager(s).switchScene("/view/MainView.fxml", "Main Menu");
        } else {
            showAlert("Error", "Permit issuance failed.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        Stage s = (Stage) cancelButton.getScene().getWindow();
        new SceneManager(s).switchScene("/view/InitialPermitMenuView.fxml", "Main Menu");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
