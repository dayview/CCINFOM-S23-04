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
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;


public class PermitPaymentListController {

    @FXML private TableView<PermitApplicationModel> paymentTable;
    @FXML private TableColumn<PermitApplicationModel, Integer> colAppId;
    @FXML private TableColumn<PermitApplicationModel, String> colBusiness;
    @FXML private TableColumn<PermitApplicationModel, String> colPermit;
    @FXML private TableColumn<PermitApplicationModel, String> colFee;
    @FXML private TableColumn<PermitApplicationModel, Void> colAction;
    @FXML private Button backButton;

    private final PermitApplicationDAO applicationDAO = new PermitApplicationDAO();
    private final BusinessDAO businessDAO = new BusinessDAO();
    private final InitialPermitTypeDAO permitTypeDAO = new InitialPermitTypeDAO();

    @FXML
    public void initialize() {
        colAppId.setCellValueFactory(new PropertyValueFactory<>("applicationId"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("totalFee"));

        // business name (lookup)
        colBusiness.setCellValueFactory(cell -> {
            BusinessModel b = null;
            try {
                b = businessDAO.getBusinessByID(cell.getValue().getBusinessId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return new javafx.beans.property.SimpleStringProperty(b.getBusinessName());
        });

        // permit type name (lookup)
        colPermit.setCellValueFactory(cell -> {
            InitialPermitTypeModel t = permitTypeDAO.getPermitTypeByID(cell.getValue().getPermitTypeId());
            return new javafx.beans.property.SimpleStringProperty(t.getPermitName());
        });

        // load data
        paymentTable.getItems().addAll(applicationDAO.getApplicationsForPayment());

        // add Pay button
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Pay");

            {
                btn.setOnAction(e -> {
                    PermitApplicationModel app = getTableView().getItems().get(getIndex());

                    BusinessModel business = null;
                    try {
                        business = businessDAO.getBusinessByID(app.getBusinessId());
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    InitialPermitTypeModel permitType = permitTypeDAO.getPermitTypeByID(app.getPermitTypeId());

                    SessionStorage.setSelectedBusiness(business);
                    SessionStorage.setSelectedPermitType(permitType);
                    SessionStorage.setSelectedApplication(app);

                    Stage stage = (Stage) btn.getScene().getWindow();
                    new SceneManager(stage).switchScene("/view/PaymentView.fxml", "Record Payment");
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
        new SceneManager(stage).switchScene("/view/InitialPermitMenuView.fxml", "Initial Permit Menu");
    }
}
