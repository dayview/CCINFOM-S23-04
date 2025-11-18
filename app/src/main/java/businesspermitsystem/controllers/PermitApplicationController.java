package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PermitApplicationModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PermitApplicationController {

    @FXML private Label businessNameLabel;
    @FXML private Label permitTypeLabel;

    @FXML private DatePicker applicationDatePicker;
    @FXML private TextField baseFeeField;
    @FXML private TextField surchargeField;
    @FXML private TextField totalFeeField;
    @FXML private TextArea remarksArea;

    @FXML private Button submitButton;
    @FXML private Button backButton;

    private BusinessModel selectedBusiness;
    private PermitTypeModel selectedPermit;

    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();

    @FXML
    public void initialize() {

        // Load session data
        selectedBusiness = SessionStorage.getSelectedBusiness();
        selectedPermit = SessionStorage.getSelectedPermitType();

        if (selectedBusiness == null || selectedPermit == null) {
            System.err.println("ERROR: Step 3 was opened without session data!");
            return;
        }

        // displays the label
        businessNameLabel.setText(selectedBusiness.getBusinessName());
        permitTypeLabel.setText(selectedPermit.getPermitName());

        // displays the base fee
        baseFeeField.setText(selectedPermit.getBaseFee().toString());

        // sets default values
        surchargeField.setText("0.00");
        totalFeeField.setText(baseFeeField.getText());

        applicationDatePicker.setValue(LocalDate.now());

        // the recalculates based on the choice
        surchargeField.textProperty().addListener((obs, oldVal, newVal) -> computeTotal());
    }

    private void computeTotal() {
        try {
            BigDecimal base = new BigDecimal(baseFeeField.getText());
            BigDecimal surcharge = new BigDecimal(surchargeField.getText());
            BigDecimal total = base.add(surcharge);
            totalFeeField.setText(total.toString());
        } catch (Exception e) {
            totalFeeField.setText("ERROR");
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/PermitTypeSelectionView.fxml", "Select Permit Type");
    }

    @FXML
    private void handleSubmit() {

        PermitApplicationModel app = new PermitApplicationModel();

        app.setBusinessId(selectedBusiness.getBusinessId());
        app.setPermitTypeId(selectedPermit.getPermitTypeId());

        app.setApplicationDate(applicationDatePicker.getValue());
        app.setApprovalDate(null);  // since initial does not have approved status

        // Expiration date (application date + validity)
        app.setExpirationDate(
                applicationDatePicker.getValue().plusMonths(selectedPermit.getValidityMonths())
        );

        app.setStatus("Pending");

        app.setBaseFee(selectedPermit.getBaseFee());
        app.setSurcharge(new BigDecimal(surchargeField.getText()));
        app.setTotalFee(new BigDecimal(totalFeeField.getText()));

        app.setRemarks(remarksArea.getText());

        boolean saved = applicationDAO.addPermitApplication(app);

        if (saved) {
            showAlert("Success", "Permit application submitted successfully!", Alert.AlertType.INFORMATION);

            Stage stage = (Stage) submitButton.getScene().getWindow();
            SceneManager sceneManager = new SceneManager(stage);
            sceneManager.switchScene("/view/MainView.fxml", "Main Menu");

        } else {
            showAlert("Error", "Failed to save permit application.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
