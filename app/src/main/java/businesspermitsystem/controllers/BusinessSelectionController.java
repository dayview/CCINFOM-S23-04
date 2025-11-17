package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class BusinessSelectionController {

    @FXML private ComboBox<BusinessModel> cmbBusinesses;

    private BusinessDAO businessDAO = new BusinessDAO();
    private BusinessModel selectedBusiness;

    @FXML
    public void initialize() {
        // Load existing businesses
        List<BusinessModel> businesses = businessDAO.getAllBusinesses();
        cmbBusinesses.getItems().addAll(businesses);

        // Show them as text
        cmbBusinesses.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(BusinessModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getBusinessName());
            }
        });
    }

    @FXML
    private void handleNewBusiness() {
        Stage s = (Stage) cmbBusinesses.getScene().getWindow();
        SceneManager sm = new SceneManager(s);
        sm.switchScene("/view/addBusinessView.fxml", "New Business");
    }

    @FXML
    private void handleNextBusiness() {
        selectedBusiness = cmbBusinesses.getValue();
        if (selectedBusiness == null) return;

        // Save selected business in session
        SessionStorage.setSelectedBusiness(selectedBusiness);

        Stage s = (Stage) cmbBusinesses.getScene().getWindow();
        SceneManager sm = new SceneManager(s);
        sm.switchScene("/view/OwnerSelectionView.fxml", "Select Owner");
    }
}
