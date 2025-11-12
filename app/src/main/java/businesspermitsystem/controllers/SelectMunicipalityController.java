package businesspermitsystem.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

/**
 * 
 */
public class SelectMunicipalityController {

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
     * Runs after all the FXML objects are loaded. Sets up the choices box contents
     */
    @FXML
    private void initialize() {

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
