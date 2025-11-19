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
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for the Initial Permit workflow (Step 1):
 * Selecting a business with exactly one owner.
 *
 * Loads eligible businesses, displays their owners,
 * and forwards the user to the permit type selection screen.
 */
public class InitialPermitController {

    @FXML private ComboBox<BusinessModel> businessComboBox;
    @FXML private Label ownerListLabel;
    @FXML private Button nextButton;
    @FXML private Button cancelButton;

    private final BusinessDAO businessDAO = new BusinessDAO();
    private final OwnerDAO ownerDAO = new OwnerDAO();

    private FilteredList<BusinessModel> filteredList;

    /**
     * Initializes the controller by loading businesses and preparing UI listeners.
     */
    @FXML
    public void initialize() {
        // Loads the businesses that have 1 owner only
        List<BusinessModel> businesses = businessDAO.getBusinessesWithOwnerCount();
        filteredList = new FilteredList<>(FXCollections.observableArrayList(businesses), p -> true);
        businessComboBox.setItems(filteredList);

        setupSelectionListener();
    }

    /**
     * Sets up a listener that displays the list of owners
     * when a business is selected from the dropdown.
     */
    private void setupSelectionListener() {
        businessComboBox.valueProperty().addListener((obs, oldVal, business) -> {
            if (business == null) {
                ownerListLabel.setText("");
                nextButton.setDisable(true);
                return;
            }

            // loads the owners of a selected business
            List<OwnerModel> owners = ownerDAO.getOwnersByBusinessId(business.getBusinessId());

            StringBuilder sb = new StringBuilder();
            for (OwnerModel o : owners) {
                sb.append(o.getFirstName()).append(" ").append(o.getLastName()).append("\n");
            }

            ownerListLabel.setText(sb.toString());
            nextButton.setDisable(false);
        });
    }

    /**
     * Returns the user back to the Initial Permit Menu.
     *
     * @param event the button click event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/InitialPermitMenuView.fxml", "Main Menu");
    }

    /**
     * Saves the selected business to SessionStorage
     * and proceeds to the permit type selection view.
     *
     * @param event the button click event
     */
    @FXML
    private void handleNext(ActionEvent event) {
        BusinessModel selectedBusiness = businessComboBox.getValue();

        if (selectedBusiness == null) {
            return;
        }
        // Store business for later usage
        SessionStorage.setSelectedBusiness(selectedBusiness);

        Stage currentStage = (Stage) nextButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/PermitTypeSelectionView.fxml", "Select Permit Type");
    }

}
