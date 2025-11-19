package businesspermitsystem.utils;

/**
 * Defines the available export formats for any generated report.
 */
public enum ReportFormat {
    PDF("PDF Document", ".pdf"),
    
    TXT("Plain Text File", ".txt");

    private final String description;
    private final String extension;

    ReportFormat(String description, String extension) {
        this.description = description;
        this.extension = extension;
    }

    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }
    
    @Override
    public String toString() {
        return description;
    }
}