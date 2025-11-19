package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PermitApplicationModel;
import businesspermitsystem.models.InitialPermitTypeModel;
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
    private InitialPermitTypeModel selectedPermit;

    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();

    @FXML
    public void initialize() {

        // load the saved data
        selectedBusiness = SessionStorage.getSelectedBusiness();
        selectedPermit = SessionStorage.getSelectedPermitType();

        if (selectedBusiness == null || selectedPermit == null) {
            System.err.println("ERROR: Step 3 loaded without stored session data!");
            return;
        }

        businessNameLabel.setText(selectedBusiness.getBusinessName());
        permitTypeLabel.setText(selectedPermit.getPermitName());

        // Base fee from permit type
        baseFeeField.setText(selectedPermit.getBaseFee().toString());

        surchargeField.setText("0.00");
        totalFeeField.setText(selectedPermit.getBaseFee().toString());

        applicationDatePicker.setValue(LocalDate.now());

        // recalculates based on the surcharges applicable based on user input
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

        // These fields will only be assigned AFTER inspection or approval
        app.setApprovalDate(null);
        app.setExpirationDate(applicationDatePicker.getValue().plusMonths(selectedPermit.getValidityMonths()));

        app.setStatus("For Payment");  // Correct workflow stage

        app.setBaseFee(selectedPermit.getBaseFee());
        app.setSurcharge(new BigDecimal(surchargeField.getText()));
        app.setTotalFee(new BigDecimal(totalFeeField.getText()));

        app.setRemarks(remarksArea.getText());

        // returns application
        int newId = applicationDAO.addPermitApplication(app);

        if (newId > 0) {

            // store new generated application ID
            app.setApplicationId(newId);

            // save to be used later in payment
            SessionStorage.setSelectedApplication(app);

            showAlert("Success", "Permit application submitted successfully!", Alert.AlertType.INFORMATION);

            //go to the payment area
            Stage stage = (Stage) submitButton.getScene().getWindow();
            SceneManager sceneManager = new SceneManager(stage);
            sceneManager.switchScene("/view/InitialPermitMenuView.fxml", "Initial Permit");

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
