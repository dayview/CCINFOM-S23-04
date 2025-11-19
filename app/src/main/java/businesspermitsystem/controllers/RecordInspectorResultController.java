package businesspermitsystem.controllers;

import businesspermitsystem.db.DatabaseConnector;
import businesspermitsystem.db.InspectorResultDAO;
import businesspermitsystem.db.InspectorScheduleDAO;
import businesspermitsystem.utils.SceneManager;
import businesspermitsystem.models.InspectorResultModel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecordInspectorResultController {

    @FXML private ComboBox<ScheduledEntry> scheduleComboBox;
    @FXML private ComboBox<String> resultComboBox;
    @FXML private TextArea remarksArea;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final InspectorResultDAO resultDAO = new InspectorResultDAO();
    private final InspectorScheduleDAO scheduleDAO = new InspectorScheduleDAO();

    /** Helper inner class for displaying schedules */
    public static class ScheduledEntry {
        public int scheduleId;
        public String businessName;
        public String inspectorName;
        public Date date;

        public ScheduledEntry(int scheduleId, String businessName, String inspectorName, Date date) {
            this.scheduleId = scheduleId;
            this.businessName = businessName;
            this.inspectorName = inspectorName;
            this.date = date;
        }

        @Override
        public String toString() {
            return businessName + " — " + inspectorName + " (" + date + ")";
        }
    }

    @FXML
    public void initialize() {
        loadScheduledInspections();

        // Set result options
        resultComboBox.getItems().addAll("Pass", "Fail");
    }

    /** Loads all schedules that are still "Scheduled" */
    private void loadScheduledInspections() {
        List<ScheduledEntry> list = new ArrayList<>();

        String sql = """
            SELECT s.schedule_id,
                   b.business_name,
                   CONCAT(i.first_name, ' ', i.last_name) AS inspector_name,
                   s.inspection_date
            FROM inspection_schedule s
            JOIN business b ON s.business_id = b.business_id
            JOIN inspector i ON s.inspector_id = i.inspector_id
            WHERE s.status = 'Scheduled'
            ORDER BY s.inspection_date ASC
        """;

        try (PreparedStatement stmt = DatabaseConnector.connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ScheduledEntry(
                        rs.getInt("schedule_id"),
                        rs.getString("business_name"),
                        rs.getString("inspector_name"),
                        rs.getDate("inspection_date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load scheduled inspections.", Alert.AlertType.ERROR);
        }

        scheduleComboBox.getItems().addAll(list);
    }

    @FXML
    private void handleSave() {

        ScheduledEntry entry = scheduleComboBox.getValue();
        String result = resultComboBox.getValue();
        String remarks = remarksArea.getText().trim();

        if (entry == null || result == null) {
            showAlert("Missing Data", "Please select a schedule and result.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Insert result record
            InspectorResultModel resultModel = new InspectorResultModel(
                    0,
                    entry.scheduleId,
                    result,
                    remarks
            );

            boolean saved = resultDAO.addInspectorResult(resultModel);

            if (!saved) {
                showAlert("Error", "Failed to save inspector result.", Alert.AlertType.ERROR);
                return;
            }

            // Update schedule status to Completed
            scheduleDAO.updateScheduleStatus(entry.scheduleId, "Completed");

            showAlert("Success", "Inspection result recorded successfully!", Alert.AlertType.INFORMATION);

            Stage stage = (Stage) saveButton.getScene().getWindow();
            new SceneManager(stage).switchScene("/view/MainView.fxml", "Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to record inspector result.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        new SceneManager(stage).switchScene("/view/InitialPermitMenuView.fxml", "Main Menu");
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
