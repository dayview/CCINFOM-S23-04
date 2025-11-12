package businesspermitsystem.controllers;


import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.MunicipalityModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

/**
 * 
 */
public class SelectMunicipalityController {

    ArrayList<MunicipalityModel> municipalityModels = new ArrayList<>();

    /**
     * 
     */
    @FXML private ChoiceBox<Integer> municipalityIDChoiceBox;

    /**
     * 
     */
    @FXML private Button municipalityIDResetButton;

    /**
     * 
     */
    @FXML private ChoiceBox<String> municipalityNameChoiceBox;

    /**
     * 
     */
    @FXML private Button municipalityNameResetButton;

    /**
     * 
     */
    @FXML private ChoiceBox<String> classificationChoiceBox;

    /**
     * 
     */
    @FXML private Button classificationResetButton;

    /**
     * 
     */
    @FXML private ChoiceBox<String> regionChoiceBox;
    
    /**
     * 
     */
    @FXML private Button regionResetButton;

    /**
     * 
     */
    @FXML private ChoiceBox<String> provinceChoiceBox;

    /**
     * 
     */
    @FXML private Button provinceResetButton;

    /**
     * 
     */
    @FXML private Button confirmButton;

    /**
     * 
     */
    @FXML private Button cancelButton;

    //--------------------------------------------------------------------

    public SelectMunicipalityController() {
        System.out.println("SelectMunicipalityController constructed");
    }

    /**
     * Runs after all the FXML objects are loaded. Sets up the choices box contents by creating the MunicipalityDAO
     */
    @FXML
    private void initialize() {
        System.out.println("Initialize method called SelectMunicipalityController");

        MunicipalityDAO municipalityDAO = new MunicipalityDAO();
        try {
            municipalityModels = municipalityDAO.getMunicipalities();

            // clear previous contents 
            municipalityIDChoiceBox.getItems().clear();
            municipalityNameChoiceBox.getItems().clear();
            classificationChoiceBox.getItems().clear();
            regionChoiceBox.getItems().clear();
            provinceChoiceBox.getItems().clear();

            for (MunicipalityModel model : municipalityModels) {
                municipalityIDChoiceBox.getItems().add(model.getMunicipalityID());
                municipalityNameChoiceBox.getItems().add(model.getMunicipalityName());
            }

            ArrayList<String> classifications = new ArrayList<>();
            ArrayList<String> regions = new ArrayList<>();
            ArrayList<String> provinces = new ArrayList<>();

            // Avoid Duplicates
            for (MunicipalityModel model : municipalityModels) {
                if (!classifications.contains(model.getClassification()))
                    classifications.add(model.getClassification());
                if (!regions.contains(model.getRegion()))
                    regions.add(model.getRegion());
                if (!provinces.contains(model.getProvince()))
                    provinces.add(model.getProvince());
            }

            classificationChoiceBox.getItems().addAll(classifications);
            regionChoiceBox.getItems().addAll(regions);
            provinceChoiceBox.getItems().addAll(provinces);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    //--------------------------------------------------------------------
    
    @FXML
    private void municipalityIDResetButtonPressed(ActionEvent event) {

    }

    @FXML
    private void municipalityNameResetButtonPressed(ActionEvent event) {
        
    }

    @FXML
    private void classificationResetButtonPressed(ActionEvent event) {
        
    }

    @FXML
    private void regionResetButtonPressed(ActionEvent event) {
        
    }

    @FXML
    private void provinceResetButtonPressed(ActionEvent event) {
        
    }

    @FXML
    private void confirmButtonPressed(ActionEvent event) {

    }

    @FXML
    private void cancelButtonPressed(ActionEvent event) {

    }
}
