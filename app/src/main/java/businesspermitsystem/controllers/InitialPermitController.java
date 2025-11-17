package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.OwnerDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.OwnerModel;
import businesspermitsystem.utils.SceneManager;

import businesspermitsystem.utils.SessionStorage;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class InitialPermitController {

    @FXML private TextField searchField;
    @FXML private ComboBox<BusinessModel> businessComboBox;
    @FXML private Label ownerListLabel;
    @FXML private Button nextButton;
    @FXML private Button cancelButton; // <-- REQUIRED for handleCancel

    private final BusinessDAO businessDAO = new BusinessDAO();
    private final OwnerDAO ownerDAO = new OwnerDAO();

    private FilteredList<BusinessModel> filteredList;

    @FXML
    public void initialize() {
        // Load businesses that have at least 1 owner
        List<BusinessModel> businesses = businessDAO.getBusinessesWithOwnerCount();
        filteredList = new FilteredList<>(FXCollections.observableArrayList(businesses), p -> true);
        businessComboBox.setItems(filteredList);

        setupSearch();
        setupSelectionListener();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(b -> {
                if (newVal == null || newVal.isBlank()) return true;

                String filter = newVal.toLowerCase();

                return b.getBusinessName().toLowerCase().contains(filter) ||
                        (b.getTradeName() != null &&
                                b.getTradeName().toLowerCase().contains(filter));
            });
        });
    }

    private void setupSelectionListener() {
        businessComboBox.valueProperty().addListener((obs, oldVal, business) -> {
            if (business == null) {
                ownerListLabel.setText("");
                nextButton.setDisable(true);
                return;
            }

            // Load ALL owners for this business
            List<OwnerModel> owners = ownerDAO.getOwnersByBusinessId(business.getBusinessId());

            StringBuilder sb = new StringBuilder();
            for (OwnerModel o : owners) {
                sb.append(o.getFirstName())
                        .append(" ")
                        .append(o.getLastName())
                        .append("\n");
            }

            ownerListLabel.setText(sb.toString());
            nextButton.setDisable(false);
        });
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }

    @FXML
    private void handleNext(ActionEvent event) {
        BusinessModel selectedBusiness = businessComboBox.getValue();

        if (selectedBusiness == null) {
            return; // button should already be disabled
        }

        // Store selected business to session
        SessionStorage.setSelectedBusiness(selectedBusiness);

        // Navigate to Step 2
        Stage currentStage = (Stage) nextButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/PermitTypeSelectionView.fxml", "Select Permit Type");
    }

}
