package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessOwnerDAO;
import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.utils.SessionStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class OwnerSelectionController {

    @FXML
    private ComboBox<OwnerModel> OwnerDropDown;

    private OwnerDAO ownerDAO = new OwnerDAO();

    @FXML
    public void initialize() {


        OwnerDropDown.getItems().addAll(ownerDAO.getAllOwners());

        //Show the names of all owners as text
        OwnerDropDown.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(OwnerModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLastName() + ", " + item.getFirstName());
            }
        });

        OwnerDropDown.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(OwnerModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLastName() + ", " + item.getFirstName());
            }
        });
    }

    @FXML
    private void handleNewOwner() {
        Stage stage = (Stage) OwnerDropDown.getScene().getWindow();
        SceneManager sm = new SceneManager(stage);

        sm.switchScene("/view/AddOwnerView.fxml", "New Owner");
    }

    @FXML
    private void handleFinish() {
        OwnerModel owner = OwnerDropDown.getValue();
        BusinessModel business = SessionStorage.getSelectedBusiness();

        String ownerFullName = owner.getFirstName() + " " + owner.getLastName();
        String businessName = business.getBusinessName();



        OwnerModel selectedOwner = OwnerDropDown.getValue();
        if (selectedOwner == null) return;

        int businessId = SessionStorage.getSelectedBusiness().getBusinessId();
        int ownerId = selectedOwner.getOwnerID();

        // Attempt to link business + owner
        boolean success = BusinessOwnerDAO.linkBusinessAndOwner(businessId, ownerId);


        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Duplicate Owner");
            alert.setHeaderText("This owner is already linked to this business.\nPlease select another owner.");
            alert.showAndWait();
            return;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successfully Added and Linked Business to Owner");
            alert.setHeaderText(businessName + "has been linked to " + ownerFullName);
            alert.showAndWait();
            Stage stage = (Stage) OwnerDropDown.getScene().getWindow();
            SceneManager sm = new SceneManager(stage);
            sm.switchScene("/view/MainView.fxml", "Main Menu");
        }
    }
}
