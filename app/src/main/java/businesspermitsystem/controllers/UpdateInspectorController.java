package businesspermitsystem.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.db.MunicipalityDAO; 
import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.models.MunicipalityModel;
import businesspermitsystem.utils.SceneManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateInspectorController {

    private InspectorDAO inspectorDAO = new InspectorDAO();
    private MunicipalityDAO municipalityDAO = new MunicipalityDAO(); 
    private List<InspectorModel> allInspectors = new ArrayList<>();
    
    private InspectorModel inspectorData = new InspectorModel(0, "", "", "", "", "", false, 0);
    private MunicipalityModel municipalityModel = null;
    
    private ChangeListener<Integer> inspectorIDListener; 

    @FXML private ChoiceBox<Integer> inspectorIDChoiceBox;
    @FXML private TextField lastNameTextField;
    @FXML private TextField firstNameTextField;
    @FXML private TextField middleNameTextField;
    @FXML private TextField designationTextField;
    @FXML private TextField licenseNumberTextField;
    @FXML private CheckBox isActiveCheckbox;
    @FXML private Button officeLocationButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // Initialize the ChoiceBox listener (defined here to be attached/detached later)
        inspectorIDListener = (ObservableValue<? extends Integer> obs, Integer oldVal, Integer newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                loadInspectorData(newVal);
            }
        };
        
        loadInspectorsForChoiceBox();
        
        // Restore the selected Municipality text if one exists after an external selection
        if (municipalityModel != null) {
            officeLocationButton.setText(municipalityModel.toString());
        }
        
        // Disable text fields and confirm button until an ID is selected
        setFieldsDisabled(true);
        confirmButton.setDisable(true);
    }
    
    private void setFieldsDisabled(boolean disable) {
        lastNameTextField.setDisable(disable);
        firstNameTextField.setDisable(disable);
        middleNameTextField.setDisable(disable);
        designationTextField.setDisable(disable);
        licenseNumberTextField.setDisable(disable);
        isActiveCheckbox.setDisable(disable);
        officeLocationButton.setDisable(disable);
    }

    /**
     * Loads all inspectors from the database and populates the inspectorIDChoiceBox.
     */
    private void loadInspectorsForChoiceBox() {
        try {
            // Detach listener temporarily to prevent accidental trigger on load
            inspectorIDChoiceBox.getSelectionModel().selectedItemProperty().removeListener(inspectorIDListener);
            
            allInspectors = inspectorDAO.getInspectors();
            inspectorIDChoiceBox.getItems().clear();
            
            List<Integer> ids = allInspectors.stream()
                .map(InspectorModel::getInspectorID)
                .collect(Collectors.toList());
            
            inspectorIDChoiceBox.getItems().addAll(ids);
            
            // Re-attach listener
            inspectorIDChoiceBox.getSelectionModel().selectedItemProperty().addListener(inspectorIDListener);
            
        } catch (SQLException e) {
            Alert alert = new Alert(AlertType.ERROR, "Failed to load inspectors from the database.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Loads the selected inspector's data into the model and updates UI fields.
     * @param inspectorID The ID of the inspector to load.
     */
    private void loadInspectorData(int inspectorID) {
        InspectorModel selectedInspector = allInspectors.stream()
            .filter(i -> i.getInspectorID() == inspectorID)
            .findFirst()
            .orElse(null);
        
        if (selectedInspector != null) {
            this.inspectorData = selectedInspector;
            
            try {
                
                this.municipalityModel = municipalityDAO.getMunicipalityById(selectedInspector.getMunicipalityID());
                
                
                if (this.municipalityModel == null) {
                     this.municipalityModel = new MunicipalityModel(selectedInspector.getMunicipalityID(), "Office ID: " + selectedInspector.getMunicipalityID(), null, null, null, null, null, null, null);
                }
                
                
                restoreUITextFields();
                officeLocationButton.setText(this.municipalityModel.toString());
                setFieldsDisabled(false); 
                confirmButton.setDisable(false); 
                
            } catch (SQLException e) {
                // Fallback on error (e.g., DB is down, or ID is invalid)
                this.municipalityModel = new MunicipalityModel(selectedInspector.getMunicipalityID(), "Office ID: " + selectedInspector.getMunicipalityID(), null, null, null, null, null, null, null);
                restoreUITextFields();
                officeLocationButton.setText("Error loading Municipality: ID " + selectedInspector.getMunicipalityID());
                setFieldsDisabled(false);
                confirmButton.setDisable(false);
                e.printStackTrace();
            }
        }
    }


    public void restoreUITextFields() {
        lastNameTextField.setText(inspectorData.getLastName());
        firstNameTextField.setText(inspectorData.getFirstName());
        middleNameTextField.setText(inspectorData.getMiddleName());
        designationTextField.setText(inspectorData.getDesignation());
        licenseNumberTextField.setText(inspectorData.getLicenseNumber());
        isActiveCheckbox.setSelected(inspectorData.isActive());
    }

    @FXML
    private void officeLocationButtonPressed() {

        saveTextFieldsToModel();

        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        try {
            UpdateInspectorSelectMunicipalityController controller = (UpdateInspectorSelectMunicipalityController) 
                sceneManager.switchSceneWithController("/view/UpdateInspectorSelectMunicipalityView.fxml", "Select Municipality");
            
            
            controller.setReturnFXMLPath("/view/UpdateInspectorView.fxml"); 
            controller.setReturnWindowTitle("Update Inspector");
            
            controller.setInspectorData(this.inspectorData);
            controller.setPreviousMunicipalityModel(this.municipalityModel);
            
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR, "Failed to load Municipality selection view.");
            alert.showAndWait();
            e.printStackTrace(); 
        }
    }

    @FXML
    private void confirmButtonPressed() {
        
        
        if (inspectorIDChoiceBox.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(AlertType.ERROR, "Please select an Inspector ID to update.");
            alert.showAndWait();
            return;
        }
        
        
        if (isInspectorDataValid()) {
            
            System.out.println("Inspector data is valid. Proceeding to update.");
            
            try {
                
                inspectorData.setInspectorID(inspectorIDChoiceBox.getSelectionModel().getSelectedItem());

             
                inspectorData.setMunicipalityID(municipalityModel.getMunicipalityID());
                
               
                inspectorDAO.updateInspector(inspectorData);
                
                
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Inspector ID " + inspectorData.getInspectorID() + " updated successfully!");
                successAlert.showAndWait();

              
                Stage currentStage = (Stage) confirmButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
                
            } catch (SQLException e) {
                
                e.printStackTrace();
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Update Inspector Data");
                errorAlert.setContentText("A database error occurred while trying to update the inspector. Details: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void cancelButtonPressed() {
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }

    private boolean isInspectorDataValid() {
        
        saveTextFieldsToModel(); 
        
        StringBuilder missingFields = new StringBuilder();
        
        if (inspectorData.getLastName() == null || inspectorData.getLastName().trim().isEmpty()) {
            missingFields.append("- Last Name\n");
        }
        if (inspectorData.getFirstName() == null || inspectorData.getFirstName().trim().isEmpty()) {
            missingFields.append("- First Name\n");
        }
        if (inspectorData.getDesignation() == null || inspectorData.getDesignation().trim().isEmpty()) {
            missingFields.append("- Designation\n");
        }
        if (inspectorData.getLicenseNumber() == null || inspectorData.getLicenseNumber().trim().isEmpty()) {
            missingFields.append("- License Number\n");
        }

        if (municipalityModel == null) {
            missingFields.append("- Office Location\n");
        }

        if (missingFields.length() > 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Required Fields Missing");
            alert.setContentText("Please complete the following fields before confirming:\n" + missingFields.toString());
            alert.showAndWait();
            return false;
        }

        return true; 
    }

    private void saveTextFieldsToModel() {
        
        inspectorData.setLastName(lastNameTextField.getText());
        inspectorData.setFirstName(firstNameTextField.getText());
        inspectorData.setMiddleName(middleNameTextField.getText());
        inspectorData.setDesignation(designationTextField.getText());
        inspectorData.setLicenseNumber(licenseNumberTextField.getText());
        inspectorData.setActive(isActiveCheckbox.isSelected());
    }

    public InspectorModel getInspectorData() {
        return inspectorData;
    }

    public void setInspectorData(InspectorModel inspectorData) {
        this.inspectorData = inspectorData;
    }

    public MunicipalityModel getMunicipalityModel() {
        return municipalityModel;
    }

    public void setMunicipalityModel(MunicipalityModel municipalityModel) {
        this.municipalityModel = municipalityModel;
        if (municipalityModel != null) {
            officeLocationButton.setText(municipalityModel.toString());
        }
    }
}