package businesspermitsystem.models;

/**
 * Data model for Comprehensive Compliance Report.
 * Simplified version without payment data.
 */
public class ComplianceReportModel {

    private int businessId;
    private String businessName;
    private String owner;
    private String status;
    private int activePermits;
    private int expiredPermits;
    private String compliance;

    public ComplianceReportModel(int businessId, String businessName, String owner,
                                 String status, int activePermits, int expiredPermits,
                                 String compliance) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.owner = owner;
        this.status = status;
        this.activePermits = activePermits;
        this.expiredPermits = expiredPermits;
        this.compliance = compliance;
    }

    // Getters and Setters
    public int getBusinessId() { return businessId; }
    public void setBusinessId(int businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getActivePermits() { return activePermits; }
    public void setActivePermits(int activePermits) { this.activePermits = activePermits; }

    public int getExpiredPermits() { return expiredPermits; }
    public void setExpiredPermits(int expiredPermits) { this.expiredPermits = expiredPermits; }

    public String getCompliance() { return compliance; }
    public void setCompliance(String compliance) { this.compliance = compliance; }
}
