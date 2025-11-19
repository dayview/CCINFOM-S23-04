package businesspermitsystem.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a permit application filed by a business.
 * Stores information about selected permit type, fee computation,
 * dates, inspection results, payment status, and issuance details.
 */
public class PermitApplicationModel {

    private int applicationId;
    private int businessId;
    private int permitTypeId;

    private LocalDate applicationDate;
    private LocalDate approvalDate;     // When inspection passes
    private LocalDate issueDate;        // When permit is officially issued
    private LocalDate expirationDate;

    private String permitNo;
    private String status;
    private String finalStatus;         // Approved / Denied

    private BigDecimal baseFee;
    private BigDecimal surcharge;
    private BigDecimal totalFee;

    private String remarks;

    // Default constructor
    public PermitApplicationModel() {}

    // Full constructor (updated)
    public PermitApplicationModel(int applicationId, int businessId, int permitTypeId, LocalDate applicationDate, LocalDate approvalDate,
                                  LocalDate issueDate, LocalDate expirationDate, String permitNo, String status, String finalStatus,
                                  BigDecimal baseFee, BigDecimal surcharge,
                                  BigDecimal totalFee, String remarks) {

        this.applicationId = applicationId;
        this.businessId = businessId;
        this.permitTypeId = permitTypeId;
        this.applicationDate = applicationDate;
        this.approvalDate = approvalDate;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.permitNo = permitNo;
        this.status = status;
        this.finalStatus = finalStatus;
        this.baseFee = baseFee;
        this.surcharge = surcharge;
        this.totalFee = totalFee;
        this.remarks = remarks;
    }


    // GETTERS

    public int getApplicationId() { return applicationId; }

    public int getBusinessId() { return businessId; }

    public int getPermitTypeId() { return permitTypeId; }

    public LocalDate getApplicationDate() { return applicationDate; }

    public LocalDate getApprovalDate() { return approvalDate; }

    public LocalDate getIssueDate() { return issueDate; }

    public LocalDate getExpirationDate() { return expirationDate; }

    public String getPermitNo() { return permitNo; }

    public String getStatus() { return status; }

    public String getFinalStatus() { return finalStatus; }

    public BigDecimal getBaseFee() { return baseFee; }

    public BigDecimal getSurcharge() { return surcharge; }

    public BigDecimal getTotalFee() { return totalFee; }

    public String getRemarks() { return remarks; }

    // SETTERS

    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public void setBusinessId(int businessId) { this.businessId = businessId; }

    public void setPermitTypeId(int permitTypeId) { this.permitTypeId = permitTypeId; }

    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }

    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }

    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public void setPermitNo(String permitNo) { this.permitNo = permitNo; }

    public void setStatus(String status) { this.status = status; }

    public void setFinalStatus(String finalStatus) { this.finalStatus = finalStatus; }

    public void setBaseFee(BigDecimal baseFee) { this.baseFee = baseFee; }

    public void setSurcharge(BigDecimal surcharge) { this.surcharge = surcharge; }

    public void setTotalFee(BigDecimal totalFee) { this.totalFee = totalFee; }

    public void setRemarks(String remarks) { this.remarks = remarks; }

    private String permitName;

    public String getPermitName() { return permitName; }
    public void setPermitName(String permitName) { this.permitName = permitName; }

    @Override
    public String toString() {
        return permitName != null ? permitName : "Unknown Permit";
    }

}
