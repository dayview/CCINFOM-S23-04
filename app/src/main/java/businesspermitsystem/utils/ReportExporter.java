package businesspermitsystem.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;


import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

public class ReportExporter {

    
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL);

    public static boolean export(String reportContent, String defaultFileNameBase, ReportFormat format, Stage ownerStage) {
        
        if (reportContent == null || reportContent.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Export Failed", "Report content is empty. Cannot export file.");
            return false;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report as " + format.getDescription());
        
        String extension = format.getExtension();
        fileChooser.setInitialFileName(defaultFileNameBase + extension);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format.getDescription() + " (*" + extension + ")", "*" + extension));

        File file = fileChooser.showSaveDialog(ownerStage != null ? ownerStage : new Stage()); 

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(extension)) {
                file = new File(file.getAbsolutePath() + extension);
            }
            
            if (format == ReportFormat.PDF) {
                return exportToPdf(file, reportContent);
            } else {
                return exportToTxt(file, reportContent);
            }
        }
        return false;
    }

    /**
     * Exports content using OpenPDF to a PDF file.
     */
    private static boolean exportToPdf(File file, String reportContent) {
        
        Document document = new Document(PageSize.LETTER, 50, 50, 50, 50); // Margins: Left, Right, Top, Bottom

        try {
           
            PdfWriter.getInstance(document, new FileOutputStream(file));
            
            
            document.open();
            
            
            Paragraph paragraph = new Paragraph(reportContent, FONT_NORMAL);
            document.add(paragraph);
            
            
            document.close();
            
            showAlert(AlertType.INFORMATION, "Export Successful", "Report saved to:\n" + file.getAbsolutePath());
            return true;
        } catch (DocumentException | IOException e) {
            showAlert(AlertType.ERROR, "Export Error", "Failed to generate PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean exportToTxt(File file, String reportContent) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(reportContent);
            showAlert(AlertType.INFORMATION, "Export Successful", "Report saved to:\n" + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Export Error", "Failed to generate TXT: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}