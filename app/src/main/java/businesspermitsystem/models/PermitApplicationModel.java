package businesspermitsystem.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a permit application filed by a business.
 * Stores information about selected permit type, fee computation,
 * dates, and overall application status.
 */
public class PermitApplicationModel {

    private int applicationId;
    private int businessId;
    private int permitTypeId;

    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate expirationDate;

    private String status; // Pending, Approved, Rejected

    private BigDecimal baseFee;
    private BigDecimal surcharge;
    private BigDecimal totalFee;

    private String remarks;

    // Default constructor
    public PermitApplicationModel() {}

    // Full-argument constructor
    public PermitApplicationModel(int applicationId, int businessId, int permitTypeId,
                                  LocalDate applicationDate, LocalDate approvalDate, LocalDate expirationDate,
                                  String status, BigDecimal baseFee, BigDecimal surcharge,
                                  BigDecimal totalFee, String remarks) {

        this.applicationId = applicationId;
        this.businessId = businessId;
        this.permitTypeId = permitTypeId;
        this.applicationDate = applicationDate;
        this.approvalDate = approvalDate;
        this.expirationDate = expirationDate;
        this.status = status;
        this.baseFee = baseFee;
        this.surcharge = surcharge;
        this.totalFee = totalFee;
        this.remarks = remarks;
    }

    // Getters
    public int getApplicationId() {
        return applicationId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public int getPermitTypeId() {
        return permitTypeId;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public String getRemarks() {
        return remarks;
    }

    // Setters
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public void setPermitTypeId(int permitTypeId) {
        this.permitTypeId = permitTypeId;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
