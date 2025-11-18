package businesspermitsystem.controllers;

import businesspermitsystem.db.PaymentDAO;
import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PaymentModel;
import businesspermitsystem.models.PermitApplicationModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentController {

    @FXML private Label businessNameLabel;
    @FXML private Label permitTypeLabel;
    @FXML private Label amountDueLabel;

    @FXML private DatePicker paymentDatePicker;
    @FXML private ComboBox<String> paymentModeComboBox;
    @FXML private TextField orNumberField;

    @FXML private Button submitPaymentButton;
    @FXML private Button cancelButton;

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();

    private BusinessModel business;
    private PermitTypeModel permitType;
    private PermitApplicationModel application;

    @FXML
    public void initialize() {

        //load the current session details

        business = SessionStorage.getSelectedBusiness();
        permitType = SessionStorage.getSelectedPermitType();
        application = SessionStorage.getSelectedApplication();

        if (business == null || permitType == null || application == null) {
            System.err.println("ERROR: Payment View opened without session data!");
            return;
        }

        businessNameLabel.setText(business.getBusinessName());
        permitTypeLabel.setText(permitType.getPermitName());
        amountDueLabel.setText(permitType.getBaseFee().toString());

        paymentDatePicker.setValue(LocalDate.now());

        paymentModeComboBox.getItems().addAll("Cash", "GCash", "Bank Transfer", "Credit Card");
        paymentModeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSubmitPayment() {

        if (orNumberField.getText().isEmpty()) {
            showAlert("Missing OR Number", "Please enter the Official Receipt number.", Alert.AlertType.WARNING);
            return;
        }

        PaymentModel payment = new PaymentModel();
        payment.setApplicationId(application.getApplicationId());
        payment.setBusinessId(business.getBusinessId());
        payment.setPermitTypeId(permitType.getPermitTypeId());
        payment.setMunicipalityId(business.getMunicipalityId());

        payment.setPaymentDate(paymentDatePicker.getValue());
        payment.setAmountPaid(permitType.getBaseFee());
        payment.setModeOfPayment(paymentModeComboBox.getValue());
        payment.setOrNumber(orNumberField.getText().trim());

        boolean saved = paymentDAO.addPayment(payment);

        if (saved) {

            //udpates the status to paid

            application.setStatus("Paid");
            applicationDAO.updatePermitApplication(application);

            showAlert("Success", "Payment successfully recorded!", Alert.AlertType.INFORMATION);

            Stage stage = (Stage) submitPaymentButton.getScene().getWindow();
            SceneManager sceneManager = new SceneManager(stage);
            sceneManager.switchScene("/view/MainView.fxml", "Main Menu");

        } else {
            showAlert("ERROR", "Payment failed to save.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
