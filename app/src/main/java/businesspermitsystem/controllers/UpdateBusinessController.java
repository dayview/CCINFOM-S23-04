package businesspermitsystem.controllers;

import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.utils.SceneManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateBusinessController{

    @FXML private TextField businessNameField;
    @FXML private TextField tradeNameField;
    @FXML private TextField streetAddressField;
    @FXML private TextField barangayField;
    @FXML private TextField businessTypeField;
    @FXML private TextField taxIdField;
    @FXML private TextField municipalityField;
    @FXML private TextField businessIdField;

    @FXML private DatePicker startDatePicker;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button Button;

    // make an object for the businessDao
    private final BusinessDAO businessDAO = new BusinessDAO();

    //to keep track of the loaded business model
    private BusinessModel loadedBusiness;
    private int businessId;

    public void loadBusinessData(int businessId){

        this.businessId = businessId;

        try{
            loadedBusiness = businessDAO.getBusinessByID(businessId);

            if(loadedBusiness == null){

                showAlert(Alert.AlertType.ERROR, "Error","Business not found");
                return;

            }

            businessNameField.setText(loadedBusiness.getBusinessName());
            tradeNameField.setText(loadedBusiness.getTradeName());
            streetAddressField.setText(loadedBusiness.getStreetAddress());
            barangayField.setText(loadedBusiness.getBarangay());
            businessTypeField.setText(loadedBusiness.getBusinessType());
            taxIdField.setText(loadedBusiness.getTaxId());
            municipalityField.setText(String.valueOf(loadedBusiness.getMunicipalityId()));
            startDatePicker.setValue(loadedBusiness.getStartDate());


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Unable to load the business data");
        }
    }

    @FXML
    private void handleLoadBusiness(ActionEvent event) {
        String idText = businessIdField.getText();

        if (!idText.matches("\\d+")) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Business ID must be a number.");
            return;
        }

        int id = Integer.parseInt(idText);
        loadBusinessData(id);
    }


    @FXML
    private void onSave(ActionEvent event){
        try{
            if(businessNameField.getText().isEmpty() || tradeNameField.getText().isEmpty()){
                showAlert(Alert.AlertType.WARNING, "Error","Business Name and Trade name are required");
                return;
            }

            LocalDate selectedDate = startDatePicker.getValue();

            if(selectedDate == null){
                showAlert(Alert.AlertType.WARNING,"Error","Please select a valid date");
                return;
            }

            int municipalityId;
            String text = municipalityField.getText();
            if(text.matches("\\d+")){
                municipalityId = Integer.parseInt(text);
            } else {
                showAlert(Alert.AlertType.WARNING,"Error","Municipality ID must be a valid number");
                return;
            }

            loadedBusiness.setBusinessName(businessNameField.getText());
            loadedBusiness.setTradeName(tradeNameField.getText());
            loadedBusiness.setStreetAddress(streetAddressField.getText());
            loadedBusiness.setBarangay(barangayField.getText());
            loadedBusiness.setBusinessType(businessTypeField.getText());
            loadedBusiness.setTaxId(taxIdField.getText());
            loadedBusiness.setStartDate(selectedDate);
            loadedBusiness.setMunicipalityId(municipalityId);

            boolean success = businessDAO.updateBusiness(loadedBusiness);

            if(success){
                showAlert(Alert.AlertType.INFORMATION, "Success","Business successfully updated");

                Stage currentStage = (Stage) saveButton.getScene().getWindow();
                SceneManager sceneManager = new SceneManager(currentStage);
                sceneManager.switchScene("/view/MainView.fxml", "Main Menu");

            }else {
                showAlert(Alert.AlertType.ERROR, "Error","Failed to update businesstry again");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error","Unexpected error" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void onCancel(ActionEvent event){
        Stage currentStage = (Stage) saveButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Main Menu");
    }
}