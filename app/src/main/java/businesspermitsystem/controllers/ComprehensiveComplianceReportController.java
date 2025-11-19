package businesspermitsystem.controllers;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;
import businesspermitsystem.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Comprehensive Compliance Report.
 * Simplified version without payment data (to be added later).
 */
public class ComprehensiveComplianceReportController {

    @FXML private ComboBox<String> municipalityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button generateButton;
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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Report generated with " + reportData.size() + " records.");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to generate report: " + e.getMessage());
        }
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
     * Update summary statistics
     */
    private void updateSummary() {
        int total = reportData.size();
        int totalActive = 0;
        int totalExpired = 0;
        int compliant = 0;

        for (ComplianceReportModel data : reportData) {
            totalActive += data.getActivePermits();
            totalExpired += data.getExpiredPermits();
            if (data.getCompliance().equals("Compliant")) {
                compliant++;
            }
        }

        double rate = total > 0 ? (compliant * 100.0 / total) : 0;

        totalBusinessesLabel.setText(String.valueOf(total));
        activePermitsLabel.setText(String.valueOf(totalActive));
        expiredPermitsLabel.setText(String.valueOf(totalExpired));
        complianceRateLabel.setText(String.format("%.1f%%", rate));
    }

    /**
     * Export to CSV
     */
    @FXML
    private void exportToCSV(ActionEvent event) {
        if (reportData.isEmpty()) {
            showError("Please generate a report first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report");
        fileChooser.setInitialFileName("compliance_report.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(generateButton.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Business ID,Business Name,Owner,Status,Active Permits,Expired Permits,Compliance\n");

                for (ComplianceReportModel data : reportData) {
                    writer.append(String.valueOf(data.getBusinessId())).append(",")
                            .append(csv(data.getBusinessName())).append(",")
                            .append(csv(data.getOwner())).append(",")
                            .append(csv(data.getStatus())).append(",")
                            .append(String.valueOf(data.getActivePermits())).append(",")
                            .append(String.valueOf(data.getExpiredPermits())).append(",")
                            .append(csv(data.getCompliance())).append("\n");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Report exported successfully!");
                alert.showAndWait();

            } catch (Exception e) {
                showError("Failed to export: " + e.getMessage());
            }
        }
    }

    /**
     * CSV escape helper
     */
    private String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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

    /**
     * Show error dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
