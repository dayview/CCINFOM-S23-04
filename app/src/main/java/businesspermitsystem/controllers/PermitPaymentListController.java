package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.PermitApplicationDAO;
import businesspermitsystem.db.InitialPermitTypeDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PermitApplicationModel;
import businesspermitsystem.models.InitialPermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.math.BigDecimal;

public class PermitPaymentListController {

    @FXML private TableView<PermitApplicationModel> paymentTable;
    @FXML private TableColumn<PermitApplicationModel, Integer> colAppId;
    @FXML private TableColumn<PermitApplicationModel, String> colBusiness;
    @FXML private TableColumn<PermitApplicationModel, String> colPermit;
    @FXML private TableColumn<PermitApplicationModel, BigDecimal> colFee;
    @FXML private TableColumn<PermitApplicationModel, Void> colAction;
    @FXML private Button backButton;

    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();
    private final BusinessDAO businessDAO = new BusinessDAO();
    private final InitialPermitTypeDAO permitTypeDAO = new InitialPermitTypeDAO();

    @FXML
    public void initialize() {

        // Table mappings
        colAppId.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("totalFee"));

        // Business Name Lookup (Null Safe)
        colBusiness.setCellValueFactory(cell -> {
            BusinessModel business = null;
            try {
                business = businessDAO.getBusinessByID(cell.getValue().getBusinessId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(
                    business != null ? business.getBusinessName() : "UNKNOWN"
            );
        });

        // Permit Type Lookup (Null Safe)
        colPermit.setCellValueFactory(cell -> {
            InitialPermitTypeModel t = permitTypeDAO.getPermitTypeByID(cell.getValue().getPermitTypeId());
            return new SimpleStringProperty(
                    t != null ? t.getPermitName() : "UNKNOWN"
            );
        });

        // Load applications
        paymentTable.getItems().addAll(applicationDAO.getApplicationsForPayment());

        // Add "Pay" button
        addPayButton();
    }

    private void addPayButton() {
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Pay");

            {
                btn.setOnAction(e -> {
                    PermitApplicationModel app = getTableView().getItems().get(getIndex());

                    BusinessModel business;
                    try {
                        business = businessDAO.getBusinessByID(app.getBusinessId());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert("Error", "Business record could not be loaded.", Alert.AlertType.ERROR);
                        return;
                    }

                    InitialPermitTypeModel permitType = permitTypeDAO.getPermitTypeByID(app.getPermitTypeId());
                    if (permitType == null) {
                        showAlert("Error", "Permit type information is missing for this application.", Alert.AlertType.ERROR);
                        return;
                    }

                    // Save to session
                    SessionStorage.setSelectedBusiness(business);
                    SessionStorage.setSelectedPermitType(permitType);
                    SessionStorage.setSelectedApplication(app);

                    // Navigate
                    Stage stage = (Stage) btn.getScene().getWindow();
                    new SceneManager(stage).switchScene("/view/InitialPaymentView.fxml", "Record Payment");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        new SceneManager(stage)
                .switchScene("/view/InitialPermitMenuView.fxml", "Initial Permit Menu");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
