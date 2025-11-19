package businesspermitsystem.controllers;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;
import businesspermitsystem.utils.ReportExporter;
import businesspermitsystem.utils.ReportFormat;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller for Comprehensive Compliance Report.
 * Enhanced with PDF/TXT export using ReportExporter pattern.
 */
public class ComprehensiveComplianceReportController {

    @FXML private ComboBox<String> municipalityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateButton;

    // --- Export Controls ---
    @FXML private ChoiceBox<ReportFormat> exportFormatChoiceBox;
    @FXML private Button exportButton;
    @FXML private Button backButton;

    // Summary labels
    @FXML private Label totalBusinessesLabel;
    @FXML private Label activePermitsLabel;
    @FXML private Label expiredPermitsLabel;
    @FXML private Label complianceRateLabel;

    // Table
    @FXML private TableView<ComplianceReportModel> complianceTable;
    @FXML private TableColumn<ComplianceReportModel, Integer> businessIdColumn;
    @FXML private TableColumn<ComplianceReportModel, String> businessNameColumn;
    @FXML private TableColumn<ComplianceReportModel, String> ownerColumn;
    @FXML private TableColumn<ComplianceReportModel, String> statusColumn;
    @FXML private TableColumn<ComplianceReportModel, Integer> activePermitsColumn;
    @FXML private TableColumn<ComplianceReportModel, Integer> expiredPermitsColumn;
    @FXML private TableColumn<ComplianceReportModel, String> complianceColumn;

    private ObservableList<ComplianceReportModel> reportData;
    private MunicipalityDAO municipalityDAO;

    // --- Report Content Storage ---
    private String currentReportContent = "";

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        municipalityDAO = new MunicipalityDAO();
        setupTableColumns();
        loadFilterOptions();
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusMonths(12));
        reportData = FXCollections.observableArrayList();
        complianceTable.setItems(reportData);

        // --- Setup Export Controls ---
        setupExportControls();
    }

    /**
     * Setup export format options
     */
    private void setupExportControls() {
        exportFormatChoiceBox.setItems(FXCollections.observableArrayList(ReportFormat.values()));
        exportFormatChoiceBox.getSelectionModel().select(ReportFormat.PDF);
        exportButton.setDisable(true); // Disabled until report is generated
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        businessIdColumn.setCellValueFactory(new PropertyValueFactory<>("businessId"));
        businessNameColumn.setCellValueFactory(new PropertyValueFactory<>("businessName"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        activePermitsColumn.setCellValueFactory(new PropertyValueFactory<>("activePermits"));
        expiredPermitsColumn.setCellValueFactory(new PropertyValueFactory<>("expiredPermits"));
        complianceColumn.setCellValueFactory(new PropertyValueFactory<>("compliance"));

        // Color-code compliance column
        complianceColumn.setCellFactory(column -> new TableCell<ComplianceReportModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Compliant")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (item.equals("Non-Compliant")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Load filter options
     */
    private void loadFilterOptions() {
        municipalityComboBox.getItems().add("All");
        try {
            ArrayList<MunicipalityModel> municipalities = municipalityDAO.getMunicipalities();
            for (MunicipalityModel m : municipalities) {
                municipalityComboBox.getItems().add(m.getMunicipalityName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to load municipalities: " + e.getMessage());
        }

        municipalityComboBox.setValue("All");
        statusComboBox.getItems().addAll("All", "Active", "Suspended", "Closed", "Pending", "Revoked");
        statusComboBox.setValue("All");
    }

    /**
     * Generate the report
     */
    @FXML
    private void generateReport(ActionEvent event) {
        try {
            reportData.clear();
            String municipality = municipalityComboBox.getValue();
            String status = statusComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            String query = buildQuery(municipality, status, startDate, endDate);
            Connection conn = DatabaseConnector.connection;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int activePermits = rs.getInt("active_permits");
                    int expiredPermits = rs.getInt("expired_permits");
                    String businessStatus = rs.getString("status");
                    String compliance = determineCompliance(businessStatus, activePermits, expiredPermits);

                    ComplianceReportModel data = new ComplianceReportModel(
                            rs.getInt("business_id"),
                            rs.getString("business_name"),
                            rs.getString("owner_name"),
                            businessStatus,
                            activePermits,
                            expiredPermits,
                            compliance
                    );
                    reportData.add(data);
                }
            }

            updateSummary();

            // --- Build and Store Report Content ---
            currentReportContent = buildReportContent(municipality, status, startDate, endDate);
            exportButton.setDisable(false); // Enable export after generation

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Report generated with " + reportData.size() + " records.");
            alert.showAndWait();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Export the generated report using ReportExporter
     */
    @FXML
    private void exportReport(ActionEvent event) {
        if (currentReportContent.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", "Please generate a report first.");
            return;
        }

        ReportFormat selectedFormat = exportFormatChoiceBox.getValue();
        if (selectedFormat == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select an export format.");
            return;
        }

        String municipality = municipalityComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Format: ComplianceReport_[Municipality]_[StartDate]_[EndDate]
        String defaultFileName = String.format(
                "ComplianceReport_%s_%s_to_%s",
                municipality.replace(" ", "_"),
                startDate,
                endDate
        );

        Stage currentStage = (Stage) exportButton.getScene().getWindow();
        ReportExporter.export(currentReportContent, defaultFileName, selectedFormat, currentStage);
    }

    /**
     * Build comprehensive report content as String
     */
    private String buildReportContent(String municipality, String status, LocalDate startDate, LocalDate endDate) {
        StringBuilder report = new StringBuilder();

        // --- Header ---
        report.append("==================================================================\n");
        report.append("COMPREHENSIVE COMPLIANCE REPORT\n");
        report.append("==================================================================\n\n");

        report.append(String.format("Municipality: %s\n", municipality));
        report.append(String.format("Status Filter: %s\n", status));
        report.append(String.format("Date Range: %s to %s\n", startDate, endDate));
        report.append(String.format("Report Generated: %s\n\n", LocalDate.now()));

        // --- Summary Section ---
        report.append("--- SUMMARY STATISTICS ---\n");
        report.append(String.format("Total Businesses Analyzed: %s\n", totalBusinessesLabel.getText()));
        report.append(String.format("Total Active Permits: %s\n", activePermitsLabel.getText()));
        report.append(String.format("Total Expired Permits: %s\n", expiredPermitsLabel.getText()));
        report.append(String.format("Overall Compliance Rate: %s\n\n", complianceRateLabel.getText()));

        // --- Detailed Records ---
        report.append("--- DETAILED COMPLIANCE RECORDS ---\n");
        report.append(String.format("%-10s | %-30s | %-25s | %-12s | %-8s | %-8s | %-15s\n",
                "Bus. ID", "Business Name", "Owner", "Status", "Active", "Expired", "Compliance"));
        report.append("-".repeat(130)).append("\n");

        for (ComplianceReportModel record : reportData) {
            report.append(String.format("%-10d | %-30s | %-25s | %-12s | %-8d | %-8d | %-15s\n",
                    record.getBusinessId(),
                    truncate(record.getBusinessName(), 30),
                    truncate(record.getOwner(), 25),
                    record.getStatus(),
                    record.getActivePermits(),
                    record.getExpiredPermits(),
                    record.getCompliance()
            ));
        }

        report.append("\n==================================================================\n");
        report.append("END OF REPORT\n");
        report.append("==================================================================\n");

        return report.toString();
    }

    /**
     * Truncate string to specified length for formatting
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    /**
     * Update summary labels
     */
    private void updateSummary() {
        int totalBusinesses = reportData.size();
        int activeCount = 0;
        int expiredCount = 0;
        int compliantCount = 0;

        for (ComplianceReportModel record : reportData) {
            activeCount += record.getActivePermits();
            expiredCount += record.getExpiredPermits();
            if (record.getCompliance().equals("Compliant")) {
                compliantCount++;
            }
        }

        totalBusinessesLabel.setText(String.valueOf(totalBusinesses));
        activePermitsLabel.setText(String.valueOf(activeCount));
        expiredPermitsLabel.setText(String.valueOf(expiredCount));

        double complianceRate = totalBusinesses > 0 ? (compliantCount * 100.0 / totalBusinesses) : 0;
        complianceRateLabel.setText(String.format("%.2f%%", complianceRate));
    }

    /**
     * Determine compliance status
     */
    private String determineCompliance(String status, int activePermits, int expiredPermits) {
        if (status.equals("Suspended") || status.equals("Revoked") || status.equals("Closed")) {
            return "Non-Compliant";
        }
        if (expiredPermits > 0 || activePermits == 0) {
            return "Warning";
        }
        return "Compliant";
    }

    /**
     * Build SQL query
     */
    private String buildQuery(String municipality, String status, LocalDate startDate, LocalDate endDate) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ")
                .append("b.business_id, ")
                .append("b.business_name, ")
                .append("b.status, ")
                .append("CONCAT(o.first_name, ' ', o.last_name) as owner_name, ")
                .append("(SELECT COUNT(*) FROM permit p WHERE p.business_id = b.business_id ")
                .append(" AND p.status = 'Active' AND (p.validity_end IS NULL OR p.validity_end >= CURDATE())) as active_permits, ")
                .append("(SELECT COUNT(*) FROM permit p WHERE p.business_id = b.business_id ")
                .append(" AND p.validity_end < CURDATE()) as expired_permits ")
                .append("FROM business b ")
                .append("LEFT JOIN business_owner bo ON b.business_id = bo.business_id ")
                .append("LEFT JOIN owner o ON bo.owner_id = o.owner_id ")
                .append("LEFT JOIN municipality m ON b.municipality_id = m.municipality_id ")
                .append("WHERE 1=1 ");

        if (!municipality.equals("All")) {
            query.append("AND m.municipality_name = '").append(municipality).append("' ");
        }

        if (!status.equals("All")) {
            query.append("AND b.status = '").append(status).append("' ");
        }

        if (startDate != null) {
            query.append("AND b.start_date >= '").append(startDate).append("' ");
        }

        if (endDate != null) {
            query.append("AND b.start_date <= '").append(endDate).append("' ");
        }

        query.append("GROUP BY b.business_id ")
                .append("ORDER BY b.business_name");

        return query.toString();
    }

    /**
     * Show alert utility
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Back to main menu
     */
    @FXML
    private void backToMain(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.switchScene("/view/MainView.fxml", "Business Permit System");
    }
}
