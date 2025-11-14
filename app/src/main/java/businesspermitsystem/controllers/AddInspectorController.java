package businesspermitsystem.controllers;

import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;

import businesspermitsystem.db.InspectorDAO;
import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.models.MunicipalityModel;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 
 */
public class AddInspectorController {

    private InspectorModel inspectorData = new InspectorModel(0, "", "", "", "", "", false, 0);
    private MunicipalityModel municipalityModel = null;

    /**
     * 
     */
    @FXML private TextField lastNameTextField;
    /**
     * 
     */
    @FXML private TextField firstNameTextField;
    /**
     * 
     */
    @FXML private TextField middleNameTextField;
    /**
     * 
     */
    @FXML private TextField designationTextField;
    /**
     * 
     */
    @FXML private TextField licenseNumberTextField;
    /**
     * 
     */
    @FXML private Button officeLocationButton;
    /**
     * 
     */
    @FXML private Button confirmButton;
    /**
     * 
     */
    @FXML private Button cancelButton;

    @FXML
    private void initialize() {
        // Restore the selected Municipality if one exists
        if (municipalityModel != null) {
            officeLocationButton.setText(municipalityModel.toString());
        }
    }

    public void restoreUITextFields() {
        lastNameTextField.setText(inspectorData.getLastName());
        firstNameTextField.setText(inspectorData.getFirstName());
        middleNameTextField.setText(inspectorData.getMiddleName());
        designationTextField.setText(inspectorData.getDesignation());
        licenseNumberTextField.setText(inspectorData.getLicenseNumber());
    }

    @FXML
    private void officeLocationButtonPressed() {

        saveTextFieldsToModel();

        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        try {
            AddInspectorSelectMunicipalityController controller = (AddInspectorSelectMunicipalityController) 
                sceneManager.switchSceneWithController("/view/AddInspectorSelectMunicipalityView.fxml", "Select Municipality");
            
            // Set the path to return to (the current view)
            controller.setReturnFXMLPath("/view/AddInspectorView.fxml"); 
            controller.setReturnWindowTitle("Add Inspector");
            
            controller.setInspectorData(this.inspectorData);
            controller.setPreviousMunicipalityModel(this.municipalityModel);
            
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace(); 
        }
    }

    @FXML
    private void confirmButtonPressed() {
        if (isInspectorDataValid()) {
            
            System.out.println("Inspector data is valid. Proceeding to save and switch.");
try {

                if (municipalityModel != null) {
                    inspectorData.setMunicipalityID(municipalityModel.getMunicipalityID());
                }
                
                InspectorDAO inspectorDAO = new InspectorDAO();
                inspectorDAO.addInspector(inspectorData);
                
                // If saving succeeds, show a success message
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Inspector '" + inspectorData.getLastName() + "' successfully added!");
                successAlert.showAndWait();

                // Proceed to switch scene only after successful save
                Stage currentStage = (Stage) confirmButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");

            } catch (SQLException e) {
                // Catch SQLException and inform the user
                e.printStackTrace();
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Failed to Save Inspector Data");
                errorAlert.setContentText("A database error occurred while trying to save the new inspector. Details: " + e.getMessage());
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

    /**
     * Checks if all required string fields in the InspectorModel are non-empty 
     * and that a Municipality has been selected. Shows an Alert if validation fails.
     * @return boolean True if all fields are valid, false otherwise.
     */
    private boolean isInspectorDataValid() {
        // Save the latest data from text fields to the model before validating
        saveTextFieldsToModel(); 
        
        StringBuilder missingFields = new StringBuilder();
        
        // 1. Check required string fields
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

        // 2. Check if a Municipality has been selected
        if (municipalityModel == null) {
            missingFields.append("- Office Location\n");
        }

        if (missingFields.length() > 0) {
            // Validation failed. Show an Alert.
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Required Fields Missing");
            alert.setContentText("Please complete the following fields before confirming:\n" + missingFields.toString());
            alert.showAndWait();
            return false;
        }

        return true; // All checks passed
    }

    /**
     * Helper method to save current text field values to the model.
     */
    private void saveTextFieldsToModel() {
        inspectorData.setLastName(lastNameTextField.getText());
        inspectorData.setFirstName(firstNameTextField.getText());
        inspectorData.setMiddleName(middleNameTextField.getText());
        inspectorData.setDesignation(designationTextField.getText());
        inspectorData.setLicenseNumber(licenseNumberTextField.getText());
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
