package businesspermitsystem.controllers;

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
    @FXML private ChoiceBox<String> classificationNameChoiceBox;

    /**
     * 
     */
    @FXML private Button classificationNameResetButton;

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
    
    private void confirmButtonPressed(ActionEvent event) {

    }

    private void cancelButtonPressed(ActionEvent event) {

    }
}
