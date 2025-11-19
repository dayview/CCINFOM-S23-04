package businesspermitsystem.controllers;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;
import businesspermitsystem.utils.ReportExporter;
import businesspermitsystem.utils.ReportFormat;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Year;

public class InspectionResultsReportController {

    @FXML private TextField yearTextField;
    @FXML private TextArea reportTextArea;
    @FXML private Button generateReportButton;
    
    // --- Export Controls ---
    @FXML private ChoiceBox<ReportFormat> exportFormatChoiceBox;
    @FXML private Button exportButton;

    // --- Data Access Objects (DAOs) ---
    private final InspectionScheduleDAO scheduleDAO = new InspectionScheduleDAO();
    private final InspectionResultDAO resultDAO = new InspectionResultDAO();
    private final BusinessDAO businessDAO = new BusinessDAO();
    private final InspectorDAO inspectorDAO = new InspectorDAO();
    private final PermitDAO permitDAO = new PermitDAO();
    private final PermitTypeDAO permitTypeDAO = new PermitTypeDAO();
    private final MunicipalityDAO municipalityDAO = new MunicipalityDAO();
    
    // --- Internal State ---
    private Map<Integer, InspectorModel> inspectorCache;
    private Map<Integer, BusinessModel> businessCache;
    private Map<Integer, PermitTypeModel> permitTypeCache;
    private Map<Integer, MunicipalityModel> municipalityCache;
    private String currentReportContent = "";

    @FXML
    private void initialize() {
        // Pre-fill the current year
        yearTextField.setText(String.valueOf(Year.now().getValue()));
        reportTextArea.setText("Enter a year and click 'Generate' to view the inspection analysis.");
        
        // Setup Export Options
        exportFormatChoiceBox.setItems(FXCollections.observableArrayList(ReportFormat.values()));
        exportFormatChoiceBox.getSelectionModel().select(ReportFormat.PDF);
        
        // Disable generate button if year field is invalid
        yearTextField.textProperty().addListener((obs, oldVal, newVal) -> 
            generateReportButton.setDisable(newVal.trim().isEmpty() || !newVal.matches("\\d{4}"))
        );
        
        generateReportButton.setDisable(yearTextField.getText().trim().isEmpty());
    }
    
    /**
     * Loads necessary master data into memory caches.
     */
    private void loadCaches() {
        try {
            inspectorCache = inspectorDAO.getInspectors().stream()
                .collect(Collectors.toMap(InspectorModel::getInspectorID, inspector -> inspector));
                
            permitTypeCache = permitTypeDAO.getAllPermitTypes().stream()
                .collect(Collectors.toMap(PermitTypeModel::getID, type -> type));
                
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
        loadCaches();
        
        reportTextArea.setText("Generating inspection results report for year " + year + "...\n");
        generateReportButton.setDisable(true);
        exportButton.setDisable(true);

        try {
            // Get all schedules and results
            List<InspectionScheduleModel> allSchedules = scheduleDAO.getAllSchedules();
            List<InspectionResultModel> allResults = resultDAO.getAllResults();
            
            // Filter schedules by year
            List<InspectionScheduleModel> schedulesInYear = allSchedules.stream()
                .filter(s -> s.getInspectionDate() != null && 
                            s.getInspectionDate().getYear() == year)
                .collect(Collectors.toList());
            
            // Build lookup map: scheduleId -> result
            Map<Integer, InspectionResultModel> resultsByScheduleId = allResults.stream()
                .collect(Collectors.toMap(
                    InspectionResultModel::getScheduleId,
                    result -> result,
                    (existing, replacement) -> existing // Keep first if duplicates
                ));
            
            // Build the report
            String reportContent = buildReportContent(year, schedulesInYear, resultsByScheduleId);
            
            reportTextArea.setText(reportContent);
            currentReportContent = reportContent;
            
            exportButton.setDisable(false);

        } catch (SQLException e) {
            reportTextArea.setText("ERROR: Failed to retrieve data: " + e.getMessage());
            showAlert(AlertType.ERROR, "Database Error", "Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            generateReportButton.setDisable(false);
        }
    }

    /**
     * Handles the export action using the selected format.
     */
    @FXML
    private void exportReport() {
        if (currentReportContent.isEmpty()) {
            showAlert(AlertType.WARNING, "No Data", "Please generate a report first.");
            return;
        }

        ReportFormat selectedFormat = exportFormatChoiceBox.getValue();
        if (selectedFormat == null) {
            showAlert(AlertType.WARNING, "Selection Error", "Please select an export format.");
            return;
        }

        String year = yearTextField.getText();
        String defaultFileName = String.format("Inspection_Results_Report_FY%s", year);
        Stage currentStage = (Stage) reportTextArea.getScene().getWindow();

        ReportExporter.export(currentReportContent, defaultFileName, selectedFormat, currentStage);
    }

    /**
     * Aggregates data and builds the formatted report string.
     */
    private String buildReportContent(int year, List<InspectionScheduleModel> schedules,
                                     Map<Integer, InspectionResultModel> resultsByScheduleId) throws SQLException {
        StringBuilder report = new StringBuilder();
        
        // --- Header ---
        report.append("==================================================================\n");
        report.append(String.format("INSPECTION RESULTS REPORT - FY %d\n", year));
        report.append("==================================================================\n\n");
        
        int totalInspections = schedules.size();
        int completedInspections = (int) schedules.stream()
            .filter(s -> "Complete".equals(s.getStatus()))
            .count();
        int pendingInspections = totalInspections - completedInspections;
        
        report.append(String.format("TOTAL INSPECTIONS SCHEDULED: %d\n", totalInspections));
        report.append(String.format("COMPLETED INSPECTIONS: %d\n", completedInspections));
        report.append(String.format("PENDING/SCHEDULED: %d\n\n", pendingInspections));

        if (totalInspections == 0) {
            report.append("No inspection records found for the year ").append(year).append(".\n");
            return report.toString();
        }

        // --- Section 1: Inspection Status Overview ---
        report.append("--- I. INSPECTION STATUS OVERVIEW ---\n\n");

        Map<String, Long> statusDistribution = schedules.stream()
            .collect(Collectors.groupingBy(InspectionScheduleModel::getStatus, Collectors.counting()));
            
        report.append("1.1 Distribution by Schedule Status:\n");
        statusDistribution.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> report.append(String.format("  - %-20s: %d\n", entry.getKey(), entry.getValue())));
        report.append("\n");

        // --- Section 2: Inspection Results Analysis ---
        report.append("--- II. INSPECTION RESULTS ANALYSIS ---\n\n");
        
        // Get completed schedules with results
        List<InspectionScheduleModel> completedSchedules = schedules.stream()
            .filter(s -> resultsByScheduleId.containsKey(s.getScheduleID()))
            .collect(Collectors.toList());
        
        if (completedSchedules.isEmpty()) {
            report.append("No completed inspection results recorded for this period.\n\n");
        } else {
            Map<String, Long> resultDistribution = completedSchedules.stream()
                .map(s -> resultsByScheduleId.get(s.getScheduleID()).getResult())
                .collect(Collectors.groupingBy(result -> result, Collectors.counting()));
                
            report.append("2.1 Distribution by Inspection Result:\n");
            resultDistribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    long count = entry.getValue();
                    double percentage = (count * 100.0) / completedSchedules.size();
                    report.append(String.format("  - %-20s: %d (%.1f%%)\n", 
                        entry.getKey(), count, percentage));
                });
            report.append("\n");
        }

        // --- Section 3: Inspector Performance ---
        report.append("--- III. INSPECTOR PERFORMANCE ---\n\n");
        
        Map<Integer, List<InspectionScheduleModel>> schedulesByInspector = schedules.stream()
            .collect(Collectors.groupingBy(InspectionScheduleModel::getInspectorID));
        
        report.append("3.1 Inspection Workload by Inspector:\n");
        schedulesByInspector.entrySet().stream()
            .sorted(Map.Entry.<Integer, List<InspectionScheduleModel>>comparingByValue(
                (l1, l2) -> Integer.compare(l2.size(), l1.size())))
            .forEach(entry -> {
                int inspectorId = entry.getKey();
                List<InspectionScheduleModel> inspectorSchedules = entry.getValue();
                InspectorModel inspector = inspectorCache.get(inspectorId);
                String inspectorName = inspector != null ? 
                    inspector.getFirstName() + " " + inspector.getLastName() : 
                    "Unknown Inspector ID " + inspectorId;
                
                long completed = inspectorSchedules.stream()
                    .filter(s -> "Complete".equals(s.getStatus()))
                    .count();
                
                report.append(String.format("\n>> %s\n", inspectorName));
                report.append(String.format("    - Total Assigned: %d\n", inspectorSchedules.size()));
                report.append(String.format("    - Completed: %d\n", completed));
                report.append(String.format("    - Pending: %d\n", inspectorSchedules.size() - completed));
            });
        report.append("\n");

        // --- Section 4: Business Inspection Analysis ---
        report.append("--- IV. BUSINESS INSPECTION ANALYSIS ---\n\n");
        
        Map<Integer, List<InspectionScheduleModel>> schedulesByBusiness = schedules.stream()
            .collect(Collectors.groupingBy(InspectionScheduleModel::getBusinessID));
        
        report.append("4.1 Top 10 Businesses by Inspection Volume:\n");
        schedulesByBusiness.entrySet().stream()
            .sorted(Map.Entry.<Integer, List<InspectionScheduleModel>>comparingByValue(
                (l1, l2) -> Integer.compare(l2.size(), l1.size())))
            .limit(10)
            .forEach(entry -> {
                int businessId = entry.getKey();
                List<InspectionScheduleModel> businessSchedules = entry.getValue();
                
                try {
                    BusinessModel business = businessDAO.getBusinessByID(businessId);
                    String businessName = business != null ? 
                        business.getBusinessName() : 
                        "Unknown Business ID " + businessId;
                    
                    long completed = businessSchedules.stream()
                        .filter(s -> resultsByScheduleId.containsKey(s.getScheduleID()))
                        .count();
                    
                    report.append(String.format("  - %-35s: %d inspections (%d completed)\n", 
                        businessName, businessSchedules.size(), completed));
                } catch (SQLException e) {
                    report.append(String.format("  - Business ID %d: %d inspections (error loading details)\n", 
                        businessId, businessSchedules.size()));
                }
            });
        report.append("\n");

        // --- Section 5: Geographic Distribution ---
        report.append("--- V. GEOGRAPHIC DISTRIBUTION ---\n\n");
        
        Map<String, List<InspectionScheduleModel>> schedulesByMunicipality = new HashMap<>();
        
        for (InspectionScheduleModel schedule : schedules) {
            try {
                BusinessModel business = businessDAO.getBusinessByID(schedule.getBusinessID());
                if (business != null) {
                    MunicipalityModel municipality = municipalityCache.get(business.getMunicipalityId());
                    String muniName = municipality != null ? 
                        municipality.getMunicipalityName() : 
                        "Unknown Municipality ID " + business.getMunicipalityId();
                    
                    schedulesByMunicipality.computeIfAbsent(muniName, k -> new ArrayList<>()).add(schedule);
                }
            } catch (SQLException e) {
                // Skip this record
            }
        }
        
        report.append("5.1 Inspection Distribution per Municipality:\n");
        schedulesByMunicipality.entrySet().stream()
            .sorted(Map.Entry.<String, List<InspectionScheduleModel>>comparingByValue(
                (l1, l2) -> Integer.compare(l2.size(), l1.size())))
            .forEach(entry -> {
                String muniName = entry.getKey();
                List<InspectionScheduleModel> muniSchedules = entry.getValue();
                
                long completed = muniSchedules.stream()
                    .filter(s -> "Complete".equals(s.getStatus()))
                    .count();
                
                report.append(String.format("\n>> %s (TOTAL: %d Inspections)\n", 
                    muniName, muniSchedules.size()));
                report.append(String.format("    - Completed: %d\n", completed));
                report.append(String.format("    - Pending: %d\n", muniSchedules.size() - completed));
                
                // Results breakdown for this municipality
                Map<String, Long> muniResults = muniSchedules.stream()
                    .filter(s -> resultsByScheduleId.containsKey(s.getScheduleID()))
                    .map(s -> resultsByScheduleId.get(s.getScheduleID()).getResult())
                    .collect(Collectors.groupingBy(result -> result, Collectors.counting()));
                
                if (!muniResults.isEmpty()) {
                    report.append("    Results Breakdown:\n");
                    muniResults.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByKey())
                        .forEach(resultEntry -> 
                            report.append(String.format("      • %-15s: %d\n", 
                                resultEntry.getKey(), resultEntry.getValue())));
                }
            });

        report.append("\n==================================================================\n");
        report.append("REPORT END\n");
        
        return report.toString();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeReport() {
        Stage stage = (Stage) exportButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/mainView.fxml", "Business Permit System");
    }
}