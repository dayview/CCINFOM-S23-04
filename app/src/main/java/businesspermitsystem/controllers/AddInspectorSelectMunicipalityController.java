package businesspermitsystem.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.InspectorModel;
import businesspermitsystem.models.MunicipalityModel;
import businesspermitsystem.utils.SceneManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.stage.Stage;

/**
 * Controller for selecting a municipality with dynamic filtering.
 * 
 * This controller implements a filter system where selecting values in one
 * ChoiceBox automatically filters and updates the available options in other ChoiceBoxes.
 * When filtering narrows down to a single municipality, all fields auto-lock. Users can
 * reset individual filters, which clears all auto-locked filters while preserving
 * manually selected filters.
 */
public class AddInspectorSelectMunicipalityController {

    private InspectorModel inspectorData;


    private MunicipalityModel previousMunicipalityModel;

    // The FXML path of the previous view.
    private String returnFXMLPath = "";
    
    // The window title of the previous fxml path
    private String returnWindowTitle = "";
    
    /** Complete list of all municipalities loaded from the database */
    private List<MunicipalityModel> municipalityModels = new ArrayList<>();
    
    /** Current filtered list based on active filter selections */
    private List<MunicipalityModel> filteredMunicipalityModels = new ArrayList<>();

    // Flags to track whether each filter was manually selected by the user
    private boolean isIDSelected = false;
    private boolean isNameSelected = false;
    private boolean isClassificationSelected = false;
    private boolean isRegionSelected = false;
    private boolean isProvinceSelected = false;

    // ChangeListeners (defined as fields to allow removal/re-addition for stability)
    private ChangeListener<Integer> idListener;
    private ChangeListener<String> nameListener;
    private ChangeListener<String> classificationListener;
    private ChangeListener<String> regionListener;
    private ChangeListener<String> provinceListener;

    // FXML fields - ChoiceBoxes
    @FXML private ChoiceBox<Integer> municipalityIDChoiceBox;
    @FXML private ChoiceBox<String> municipalityNameChoiceBox;
    @FXML private ChoiceBox<String> classificationChoiceBox;
    @FXML private ChoiceBox<String> regionChoiceBox;
    @FXML private ChoiceBox<String> provinceChoiceBox;

    // FXML fields - Reset Buttons
    @FXML private Button municipalityIDResetButton;
    @FXML private Button municipalityNameResetButton;
    @FXML private Button classificationResetButton;
    @FXML private Button regionResetButton;
    @FXML private Button provinceResetButton;
    
    // FXML fields - Action Buttons
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    //--------------------------------------------------------------------

    /**
     * Constructor for SelectMunicipalityController.
     */
    public AddInspectorSelectMunicipalityController() {
        System.out.println("SelectMunicipalityController constructed");
    }

    /**
     * Initializes the controller after FXML loading is complete.
     * 
     * Loads municipality data from the database
     * Creates and attaches change listeners for each ChoiceBox
     * Performs initial population of all ChoiceBoxes
     * The listeners detect user selections and trigger dynamic filtering
     * to update available options across all ChoiceBoxes.
     */
    @FXML
    private void initialize() {
        System.out.println("Initialize method called SelectMunicipalityController");

        MunicipalityDAO municipalityDAO = new MunicipalityDAO();
        try {
            municipalityModels = municipalityDAO.getMunicipalities();
            filteredMunicipalityModels.addAll(municipalityModels); // Start with all data
            
            // Define listeners that set user interaction flags and trigger updates
            idListener = (obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    isIDSelected = true;
                    updateChoiceBoxes();
                }
            };
            
            nameListener = (obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    isNameSelected = true;
                    updateChoiceBoxes();
                }
            };
            
            classificationListener = (obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    isClassificationSelected = true;
                    updateChoiceBoxes();
                }
            };
            
            regionListener = (obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    isRegionSelected = true;
                    updateChoiceBoxes();
                }
            };
            
            provinceListener = (obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals(oldVal)) {
                    isProvinceSelected = true;
                    updateChoiceBoxes();
                }
            };

            // Attach listeners to each ChoiceBox
            attachAllListeners();

            // Initial population and setup
            updateChoiceBoxes();
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Show error dialog
        }
    }

    /**
     * Filters the municipalities based on current selections and updates all ChoiceBoxes.
     */
    private void updateChoiceBoxes() {
        
     
        detachAllListeners();

       
        Integer selectedID = municipalityIDChoiceBox.getSelectionModel().getSelectedItem();
        String selectedName = municipalityNameChoiceBox.getSelectionModel().getSelectedItem();
        String selectedClassification = classificationChoiceBox.getSelectionModel().getSelectedItem();
        String selectedRegion = regionChoiceBox.getSelectionModel().getSelectedItem();
        String selectedProvince = provinceChoiceBox.getSelectionModel().getSelectedItem();
        
        
        filteredMunicipalityModels = municipalityModels.stream()
            .filter(m -> selectedID == null || selectedID.equals(m.getMunicipalityID()))
            .filter(m -> selectedName == null || 
                         (m.getMunicipalityName() != null && m.getMunicipalityName().equals(selectedName)))
            .filter(m -> selectedClassification == null || 
                         (m.getClassification() != null && m.getClassification().equals(selectedClassification)))
            .filter(m -> selectedRegion == null || 
                         (m.getRegion() != null && m.getRegion().equals(selectedRegion)))
            .filter(m -> selectedProvince == null || 
                         (m.getProvince() != null && m.getProvince().equals(selectedProvince)))
            .collect(Collectors.toCollection(ArrayList::new));

      
        Platform.runLater(() -> {
            
            municipalityIDChoiceBox.getItems().clear();
            municipalityNameChoiceBox.getItems().clear();
            classificationChoiceBox.getItems().clear();
            regionChoiceBox.getItems().clear();
            provinceChoiceBox.getItems().clear();
            
          
            Set<Integer> uniqueIDs = filteredMunicipalityModels.stream()
                    .map(MunicipalityModel::getMunicipalityID)
                    .collect(Collectors.toSet());
            Set<String> uniqueNames = filteredMunicipalityModels.stream()
                    .map(MunicipalityModel::getMunicipalityName)
                    .collect(Collectors.toSet());
            Set<String> uniqueClassifications = filteredMunicipalityModels.stream()
                    .map(MunicipalityModel::getClassification)
                    .collect(Collectors.toSet());
            Set<String> uniqueRegions = filteredMunicipalityModels.stream()
                    .map(MunicipalityModel::getRegion)
                    .collect(Collectors.toSet());
            Set<String> uniqueProvinces = filteredMunicipalityModels.stream()
                    .map(MunicipalityModel::getProvince)
                    .collect(Collectors.toSet());

          
            populateAndHandleSingle(municipalityIDChoiceBox, uniqueIDs, selectedID);
            populateAndHandleSingle(municipalityNameChoiceBox, uniqueNames, selectedName);
            populateAndHandleSingle(classificationChoiceBox, uniqueClassifications, selectedClassification);
            populateAndHandleSingle(regionChoiceBox, uniqueRegions, selectedRegion);
            populateAndHandleSingle(provinceChoiceBox, uniqueProvinces, selectedProvince);
            
        
            municipalityIDResetButton.setVisible(isIDSelected);
            municipalityNameResetButton.setVisible(isNameSelected);
            classificationResetButton.setVisible(isClassificationSelected);
            regionResetButton.setVisible(isRegionSelected);
            provinceResetButton.setVisible(isProvinceSelected);
            
         
            confirmButton.setDisable(filteredMunicipalityModels.size() != 1);

           
            attachAllListeners();
        });
    }

    /**
     * Populates a ChoiceBox with unique values, handles auto-selection, and locks it when only one option exists.
     * 
     * @param <T> The type of items in the ChoiceBox (Integer for ID, String for others)
     * @param choiceBox The ChoiceBox to populate and configure
     * @param uniqueItems Set of unique values to add to the ChoiceBox
     * @param selectedItem The currently selected value (may be null)
     */
    private <T> void populateAndHandleSingle(ChoiceBox<T> choiceBox, Set<T> uniqueItems, T selectedItem) {
        
        SingleSelectionModel<T> selectionModel = choiceBox.getSelectionModel();
        T currentSelection = selectionModel.getSelectedItem();

     
        choiceBox.getItems().addAll(uniqueItems);

        if (uniqueItems.size() == 1) {
  
            T singleItem = uniqueItems.iterator().next();
            
            if (!singleItem.equals(currentSelection)) {
                selectionModel.select(singleItem); 
            }
            choiceBox.setDisable(true);
        } else {
          
            choiceBox.setDisable(false);
            
         
            if (selectedItem != null && uniqueItems.contains(selectedItem) && !selectedItem.equals(currentSelection)) {
                selectionModel.select(selectedItem);
            } else if (selectedItem != null && !uniqueItems.contains(selectedItem)) {
               
                selectionModel.clearSelection();
            }
        }
    }

    /**
     * Clears all filters that were auto-locked (not manually selected by the user).
     * 
     * This method is called when any reset button is pressed. It preserves filters
     * that the user explicitly selected while clearing all auto-locked filters,
     * allowing the user to explore other options.
     * 
     * For example: If the user manually selected ID and Classification, and other
     * fields were auto-locked as a result, this method will clear the auto-locked
     * fields but keep ID and Classification selected.
     */
    private void clearAutoLockedFilters() {
    
        detachAllListeners();
        
      
        if (!isIDSelected) {
            municipalityIDChoiceBox.getSelectionModel().clearSelection();
            municipalityIDChoiceBox.setDisable(false);
        }
        
        if (!isNameSelected) {
            municipalityNameChoiceBox.getSelectionModel().clearSelection();
            municipalityNameChoiceBox.setDisable(false);
        }
        
        if (!isClassificationSelected) {
            classificationChoiceBox.getSelectionModel().clearSelection();
            classificationChoiceBox.setDisable(false);
        }
        
        if (!isRegionSelected) {
            regionChoiceBox.getSelectionModel().clearSelection();
            regionChoiceBox.setDisable(false);
        }
        
        if (!isProvinceSelected) {
            provinceChoiceBox.getSelectionModel().clearSelection();
            provinceChoiceBox.setDisable(false);
        }
        
       
        attachAllListeners();
    }

    /**
     * Detaches all change listeners from the ChoiceBoxes.
     * 
     * This prevents cascading updates when programmatically modifying selections.
     * Should be called before making multiple changes, followed by
     * attachAllListeners() when done.
     */
    private void detachAllListeners() {
        municipalityIDChoiceBox.getSelectionModel().selectedItemProperty().removeListener(idListener);
        municipalityNameChoiceBox.getSelectionModel().selectedItemProperty().removeListener(nameListener);
        classificationChoiceBox.getSelectionModel().selectedItemProperty().removeListener(classificationListener);
        regionChoiceBox.getSelectionModel().selectedItemProperty().removeListener(regionListener);
        provinceChoiceBox.getSelectionModel().selectedItemProperty().removeListener(provinceListener);
    }

    /**
     * Re-attaches all change listeners to the ChoiceBoxes.
     * 
     * This re-enables automatic filtering updates when users make selections.
     * Should be called after detachAllListeners() and programmatic changes are complete.
     */
    private void attachAllListeners() {
        municipalityIDChoiceBox.getSelectionModel().selectedItemProperty().addListener(idListener);
        municipalityNameChoiceBox.getSelectionModel().selectedItemProperty().addListener(nameListener);
        classificationChoiceBox.getSelectionModel().selectedItemProperty().addListener(classificationListener);
        regionChoiceBox.getSelectionModel().selectedItemProperty().addListener(regionListener);
        provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener(provinceListener);
    }

    
    /**
     * Handles the Municipality ID reset button press.
     * 
     * Clears the ID selection, resets the user interaction flag, clears all
     * auto-locked filters, and updates all ChoiceBoxes to reflect the new state.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void municipalityIDResetButtonPressed(ActionEvent event) {
        municipalityIDChoiceBox.getSelectionModel().clearSelection();
        municipalityIDChoiceBox.setDisable(false);
        isIDSelected = false;
        clearAutoLockedFilters();
        updateChoiceBoxes();
    }

    /**
     * Handles the Municipality Name reset button press.
     * 
     * Clears the name selection, resets the user interaction flag, clears all
     * auto-locked filters, and updates all ChoiceBoxes to reflect the new state.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void municipalityNameResetButtonPressed(ActionEvent event) {
        municipalityNameChoiceBox.getSelectionModel().clearSelection();
        municipalityNameChoiceBox.setDisable(false);
        isNameSelected = false;
        clearAutoLockedFilters();
        updateChoiceBoxes();
    }

    /**
     * Handles the Classification reset button press.
     * 
     * Clears the classification selection, resets the user interaction flag, clears all
     * auto-locked filters, and updates all ChoiceBoxes to reflect the new state.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void classificationResetButtonPressed(ActionEvent event) {
        classificationChoiceBox.getSelectionModel().clearSelection();
        classificationChoiceBox.setDisable(false);
        isClassificationSelected = false;
        clearAutoLockedFilters();
        updateChoiceBoxes();
    }

    /**
     * Handles the Region reset button press.
     * 
     * Clears the region selection, resets the user interaction flag, clears all
     * auto-locked filters, and updates all ChoiceBoxes to reflect the new state.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void regionResetButtonPressed(ActionEvent event) {
        regionChoiceBox.getSelectionModel().clearSelection();
        regionChoiceBox.setDisable(false);
        isRegionSelected = false;
        clearAutoLockedFilters();
        updateChoiceBoxes();
    }

    /**
     * Handles the Province reset button press.
     * 
     * Clears the province selection, resets the user interaction flag, clears all
     * auto-locked filters, and updates all ChoiceBoxes to reflect the new state.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void provinceResetButtonPressed(ActionEvent event) {
        provinceChoiceBox.getSelectionModel().clearSelection();
        provinceChoiceBox.setDisable(false);
        isProvinceSelected = false;
        clearAutoLockedFilters();
        updateChoiceBoxes();
    }


    
    /**
     * Handles the Confirm button press.
     * 
     * This method is called when the user confirms their municipality selection.
     * The button is only enabled when exactly one municipality matches the current filters.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void confirmButtonPressed(ActionEvent event) {
        if (filteredMunicipalityModels.size() == 1) {
            MunicipalityModel selected = filteredMunicipalityModels.get(0);
            System.out.println("Confirmed selection: " + selected.getMunicipalityName() + 
                               " (ID: " + selected.getMunicipalityID() + ")");


            Stage currentStage = (Stage) confirmButton.getScene().getWindow();
            SceneManager sceneManager = new SceneManager(currentStage);
            try {
                AddInspectorController controller = (AddInspectorController) sceneManager.switchSceneWithController(returnFXMLPath, returnWindowTitle);
                controller.setInspectorData(this.inspectorData);
                controller.setMunicipalityModel(selected);
                controller.restoreUITextFields();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } else {
            System.out.println("Error: Cannot confirm. Filtered list size is " + filteredMunicipalityModels.size());
        }
    }

    /**
     * Handles the Cancel button press.
     * 
     * This method is called when the user cancels the municipality selection process.
     * 
     * @param event The action event triggered by the button press
     */
    @FXML
    private void cancelButtonPressed(ActionEvent event) {
        System.out.println("Selection cancelled.");

        Stage currentStage = (Stage) confirmButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        try {
            AddInspectorController controller = (AddInspectorController) sceneManager.switchSceneWithController(returnFXMLPath, returnWindowTitle);
            controller.setInspectorData(this.inspectorData);
            controller.setMunicipalityModel(previousMunicipalityModel);
            controller.restoreUITextFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public String getReturnFXMLPath() {
		return returnFXMLPath;
	}

	public void setReturnFXMLPath(String returnFXMLPath) {
		this.returnFXMLPath = returnFXMLPath;
	}

	public String getReturnWindowTitle() {
		return returnWindowTitle;
	}

	public void setReturnWindowTitle(String returnWindowTitle) {
		this.returnWindowTitle = returnWindowTitle;
	}

    public void setPreviousMunicipalityModel(MunicipalityModel model) {
        this.previousMunicipalityModel = model;
    }

    public InspectorModel getInspectorData() {
        return inspectorData;
    }

    public void setInspectorData(InspectorModel inspectorData) {
        this.inspectorData = inspectorData;
    }
    
}