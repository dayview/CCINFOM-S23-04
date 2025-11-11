package businesspermitsystem.controllers;

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
}
