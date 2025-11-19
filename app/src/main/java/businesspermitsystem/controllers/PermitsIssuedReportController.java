package businesspermitsystem.controllers;

import businesspermitsystem.db.PermitDAO;
import businesspermitsystem.db.BusinessDAO;
import businesspermitsystem.db.PermitTypeDAO;
import businesspermitsystem.db.MunicipalityDAO;
import businesspermitsystem.models.PermitModel;
import businesspermitsystem.models.BusinessModel;
import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.models.MunicipalityModel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.Year;

public class PermitsIssuedReportController {

    @FXML private TextField yearTextField;
    @FXML private TextArea reportTextArea;
    @FXML private Button generateReportButton;

    // --- Data Access Objects (DAOs) ---
    private final PermitDAO permitDAO = new PermitDAO();
    private final BusinessDAO businessDAO = new BusinessDAO(); 
    private final PermitTypeDAO permitTypeDAO = new PermitTypeDAO(); 
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO(); 
    

    private Map<Integer, PermitTypeModel> permitTypeCache;
    private Map<Integer, MunicipalityModel> municipalityCache;

    @FXML
    private void initialize() {
  
        yearTextField.setText(String.valueOf(Year.now().getValue()));
        reportTextArea.setText("Enter a year and click 'Generate Report' to view the analysis.");
        
        // Disable button if year field is empty
        yearTextField.textProperty().addListener((obs, oldVal, newVal) -> 
            generateReportButton.setDisable(newVal.trim().isEmpty() || !newVal.matches("\\d{4}"))
        );
        
        generateReportButton.setDisable(yearTextField.getText().trim().isEmpty());
    }
    
    /**
     * Loads necessary  data (Permit Types and Municipalities) into the variables.
     */
    private void loadCaches() {
        try {
            // Load Permit Types
            permitTypeCache = permitTypeDAO.getAllPermitTypes().stream()
                .collect(Collectors.toMap(PermitTypeModel::getID, type -> type));
                
            // Load Municipalities
            municipalityCache = municipalityDAO.getMunicipalities().stream()
                .collect(Collectors.toMap(MunicipalityModel::getMunicipalityID, muni -> muni));
                
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Data Cache Error", "Failed to load master data caches: " + e.getMessage());
        }
    }

    @FXML
    private void generateReport() {
        String yearText = yearTextField.getText().trim();
        if (yearText.isEmpty() || !yearText.matches("\\d{4}")) {
            showAlert(AlertType.ERROR, "Invalid Input", "Please enter a valid four-digit year.");
            return;
        }

        int year = Integer.parseInt(yearText);
        
        // Ensure caches are loaded before processing
        loadCaches();
        
        reportTextArea.setText("Generating report for year " + year + "...\n");
        generateReportButton.setDisable(true);

        try {
          
            List<PermitModel> permitsInYear = permitDAO.getPermitDataForYear(year);
            
         
            String reportContent = buildReportContent(year, permitsInYear);
            
            reportTextArea.setText(reportContent);

        } catch (SQLException e) {
            reportTextArea.setText("ERROR: Failed to retrieve data for the report due to a database error: " + e.getMessage());
            showAlert(AlertType.ERROR, "Database Error", "Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            generateReportButton.setDisable(false);
        }
    }

    /**
     * Processes the list of permits and generates the final formatted report string.
     */
    private String buildReportContent(int year, List<PermitModel> permits) throws SQLException {
        StringBuilder report = new StringBuilder();
        
        //  Header 
        report.append("==================================================================\n");
        report.append(String.format("PERMITS ISSUED REPORT - FY %d\n", year));
        report.append("==================================================================\n\n");
        
        int totalPermits = permits.size();
        report.append(String.format("TOTAL PERMITS PROCESSED: %d\n\n", totalPermits));

        if (totalPermits == 0) {
            report.append("No processed permits found for the year ").append(year).append(".\n");
            return report.toString();
        }

        // Permit Distribution 
        report.append("--- I. PERMIT DISTRIBUTION ANALYSIS ---\n\n");

        //  By Status
        Map<String, Long> statusDistribution = permits.stream()
            .collect(Collectors.groupingBy(PermitModel::getStatus, Collectors.counting()));
            
        report.append("1.1 Distribution by Permit Status:\n");
        statusDistribution.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> report.append(String.format("  - %-15s: %d\n", entry.getKey(), entry.getValue())));
        report.append("\n");


        //  By Permit Type
        Map<String, Long> typeDistribution = permits.stream()
            .collect(Collectors.groupingBy(
                permit -> {
                    PermitTypeModel type = permitTypeCache.get(permit.getPermitTypeID());
                    return type != null ? type.getName() : "Unknown Type ID " + permit.getPermitTypeID();
                },
                Collectors.counting()
            ));
            
        report.append("1.2 Distribution by Permit Type:\n");
        typeDistribution.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> report.append(String.format("  - %-25s: %d\n", entry.getKey(), entry.getValue())));
        report.append("\n");
        
        // Aggregation 1.3: By Business (Top 5 businesses with the most permits)
        Map<String, Long> businessDistribution = permits.stream()
            .collect(Collectors.groupingBy(
                permit -> {
                    try {
                        BusinessModel business = businessDAO.getBusinessByID(permit.getBusinessID());
                        return business != null ? business.getBusinessName() : "Unknown Business ID " + permit.getBusinessID();
                    } catch (SQLException e) {
                        return "DB Error Business ID " + permit.getBusinessID();
                    }
                },
                Collectors.counting()
            ));
            
        report.append("1.3 Top 5 Businesses by Permit Volume:\n");
        businessDistribution.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> report.append(String.format("  - %-30s: %d\n", entry.getKey(), entry.getValue())));
        report.append("\n");

        
        report.append("--- II. GEOGRAPHIC DISTRIBUTION ---\n\n");
        

        Map<String, List<PermitModel>> permitsByMunicipality = new HashMap<>();

        for (PermitModel permit : permits) {
            BusinessModel business = businessDAO.getBusinessByID(permit.getBusinessID());
            if (business != null) {
                MunicipalityModel municipality = municipalityCache.get(business.getMunicipalityId());
                String muniName = municipality != null ? municipality.getMunicipalityName() : "Unknown Municipality ID " + business.getMunicipalityId();
                
                permitsByMunicipality.computeIfAbsent(muniName, k -> new ArrayList<>()).add(permit);
            }
        }
        
        report.append("2.1 Permit Distribution per Municipality:\n");
        permitsByMunicipality.entrySet().stream()
            .sorted(Map.Entry.<String, List<PermitModel>>comparingByValue( (l1, l2) -> Integer.compare(l2.size(), l1.size()) ))
            .forEach(entry -> {
                String muniName = entry.getKey();
                int count = entry.getValue().size();
                
               
                Map<String, Long> muniStatusBreakdown = entry.getValue().stream()
                    .collect(Collectors.groupingBy(PermitModel::getStatus, Collectors.counting()));
                
                report.append(String.format("\n>> %s (TOTAL: %d Permits)\n", muniName, count));
                
                muniStatusBreakdown.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByKey())
                    .forEach(statusEntry -> 
                        report.append(String.format("    - %s: %d\n", statusEntry.getKey(), statusEntry.getValue())));
            });

        report.append("\n==================================================================\n");
        report.append("REPORT END\n");
        
        return report.toString();
    }
    
    // --- Helper Methods ---

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeReport() {
        Stage currentStage = (Stage) yearTextField.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(currentStage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit Dashboard");
    }
}