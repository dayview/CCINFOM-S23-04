package businesspermitsystem.controllers;

import businesspermitsystem.db.PaymentsCollectedReportDAO;
import businesspermitsystem.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

/**
 * Controller responsible for generating, displaying, and exporting the Payments Collected Report.
 */
public class PaymentsCollectedReportController {

    @FXML private TextField yearField;
    @FXML private TextArea reportArea;

    private final PaymentsCollectedReportDAO dao = new PaymentsCollectedReportDAO();

    /**
     * Generates the report based on a provided year
     * and displays it in the report text area.
     */
    @FXML
    public void generateReport() {
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            String report = dao.generatePaymentsCollectedReport(year);
            reportArea.setText(report);
        } catch (Exception e) {
            reportArea.setText("Invalid year! Please enter a valid numeric value.");
        }
    }

    @FXML
    public void exportToFile() {
        String content = reportArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("No Data", "Please generate a report before exporting.");
            return;
        }

        try {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("PaymentsCollectedReport.txt");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text File", "*.txt")
            );
            File file = chooser.showSaveDialog(reportArea.getScene().getWindow());

            if (file != null) {
                Files.writeString(file.toPath(), content);
                showAlert("Success", "Report exported successfully!");
            }

        } catch (Exception e) {
            showAlert("Export Failed", "Could not export file: " + e.getMessage());
        }
    }

    @FXML
    public void closeWindow() {
            Stage stage = (Stage) reportArea.getScene().getWindow();
            new SceneManager(stage).switchScene("/view/MainView.fxml", "Main Menu");
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
