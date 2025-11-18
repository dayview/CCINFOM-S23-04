package businesspermitsystem.controllers;

import businesspermitsystem.db.PaymentDAO;
import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PaymentModel;
import businesspermitsystem.models.PermitApplicationModel;
import businesspermitsystem.models.InitialPermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controller responsible for recording payments for permit applications.
 * Loads session data, displays business and fee details, and saves payments.
 */
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
    private InitialPermitTypeModel permitType;
    private PermitApplicationModel application;

    /**
     * Initializes the payment screen by loading business, permit type,
     * and application details stored in the session.
     */
    @FXML
    public void initialize() {

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

    /**
     * Validates the user input and records a new payment.
     * Also updates the related permit application status to "Paid".
     */
    @FXML
    private void handleSubmitPayment() {

        if (orNumberField.getText().isEmpty()) {
            showAlert("Missing OR Number",
                    "Please enter the Official Receipt number.",
                    Alert.AlertType.WARNING);
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

            application.setStatus("Paid");
            applicationDAO.updatePermitApplication(application);

            showAlert("Success",
                    "Payment successfully recorded!",
                    Alert.AlertType.INFORMATION);

            Stage stage = (Stage) submitPaymentButton.getScene().getWindow();
            new SceneManager(stage).switchScene("/view/MainView.fxml", "Main Menu");

        } else {
            showAlert("ERROR", "Payment failed to save.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Returns the user to the main menu without saving any changes.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/MainView.fxml", "Main Menu");
    }

    /**
     * Displays an alert message.
     *
     * @param title alert window title
     * @param msg   message to display
     * @param type  alert type (information, warning, error)
     */
    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
