package businesspermitsystem.controllers;

import businesspermitsystem.db.InitialPermitTypeDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.InitialPermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import java.util.List;

public class PermitTypeSelectionController {

    @FXML private ComboBox<InitialPermitTypeModel> permitTypeComboBox;
    @FXML private Label baseFeeLabel;
    @FXML private Label validityLabel;
    @FXML private Label requirementsLabel;
    @FXML private Button continueButton;
    @FXML private Button backButton;

    private final InitialPermitTypeDAO permitTypeDAO = new InitialPermitTypeDAO();

    private BusinessModel selectedBusiness;

    @FXML
    public void initialize() {

        // loads the selected business
        this.selectedBusiness = SessionStorage.getSelectedBusiness();

        loadPermitTypes();
        setupSelectionListener();

        continueButton.setDisable(true);
    }

    private void loadPermitTypes() {
        List<InitialPermitTypeModel> permitTypes = permitTypeDAO.getAllPermitTypes();
        permitTypeComboBox.setItems(FXCollections.observableArrayList(permitTypes));
    }

    private void setupSelectionListener() {
        permitTypeComboBox.valueProperty().addListener((obs, oldVal, permitType) -> {

            if (permitType == null) {
                baseFeeLabel.setText("");
                validityLabel.setText("");
                requirementsLabel.setText("");
                continueButton.setDisable(true);
                return;
            }

            baseFeeLabel.setText(permitType.getBaseFee().toString());
            validityLabel.setText(permitType.getValidityMonths() + " month(s)");
            requirementsLabel.setText(permitType.getDocumentRequirements());

            continueButton.setDisable(false);
        });
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/InitialPermitIssuanceView.fxml", "Initial Permit Issuance");
    }

    @FXML
    private void handleContinue() {
        InitialPermitTypeModel selectedPermit = permitTypeComboBox.getValue();

        if (selectedPermit == null) {
            return;
        }

        SessionStorage.setSelectedPermitType(selectedPermit);
        Stage stage = (Stage) continueButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);

        sceneManager.switchScene("/view/PermitApplicationView.fxml", "Permit Application"
        );
    }

}
