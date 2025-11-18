package businesspermitsystem.models;

import java.time.LocalDate;

/**
 * The {@code BusinessModel} class represents a registered business within the Business Permit System.
 * <p>
 * It serves as a data model that maps to the {@code business} table in the database, holding
 * all relevant details such as business identity, location, type, registration information
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * BusinessModel business = new BusinessModel(
 *     1, "Microsoft Corporation", "Microsoft",
 *     "123 Main St", "Barangay Uno", "Quezon City", "Metro Manila",
 *     "Technology", "TAX-2024-001", LocalDate.of(2024, 3, 1),
 *     "Active"
 * );
 * }</pre>
 *
 * @author antqnluis
 * @version 1.0
 */
public class BusinessModel {

    /**
     * Unique identifier for a business
     */
    private int businessId;          // private key
    /**
     * The Businesses legal name
     */
    private String businessName;
    /**
     * trade name/operating name of a business
     */
    private String tradeName;
    /**
     * What Barangay is this business located on
     */
    private String barangay;
    /**
     * The Street location of a business
     */
    private String streetAddress;
    /**
     * Indicates what type of business is it
     */
    private String businessType;
    /**
     * Business Tax Identification number
     */
    private String taxId;
    /**
     * The official date of when the business started
     */
    private LocalDate startDate;
    /**
     * The current status of their business permit application
     * Valid values: Active, Suspended, Closed, Pending, Revoked, Merged, Retired
     */
    private String status;

    /**
     * The date when the current status became effective
     * Used for tracking status change history
     */
    private LocalDate statusEffectiveDate;

    /**
     * The reason or justification for the current status
     * Example: "Failed health inspection", "Business closure", "Merger with Company X"
     */
    private String statusReason;

    /**
     * Reference to supporting documents for status change
     * Could be a file path, document ID, or URL
     */
    private String supportDocRef;

    /**
     * Foreign key
     * ID of the Municipality where Business is registered
     */
    private int municipalityId;
    
    
    /**
     * Default constructor.
     * Initializes a new instance of {@code BusinessModel} with no predefined values.
     */
    public BusinessModel() {
    }
    
    /**
     * Full-argument constructor.
     * <p>
     * Initializes a new {@code BusinessModel} with all specified field values.
     * </p>
     *
     * @param businessId      unique business ID
     * @param businessName    legal business name
     * @param tradeName       trade or operating name
     * @param streetAddress   street address of the business
     * @param barangay        barangay where the business is located
     * @param businessType    type or classification of the business
     * @param taxId           tax identification number
     * @param startDate       date when the business started operations
     * @param status          operational status (Active, Suspended, Closed, Pending)
     */
    public BusinessModel(int businessId, String businessName, String tradeName,
                         String streetAddress, String barangay,
                         String businessType, String taxId, LocalDate startDate,
                         String status, int municipalityId) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.tradeName = tradeName;
        this.streetAddress = streetAddress;
        this.barangay = barangay;
        this.businessType = businessType;
        this.taxId = taxId;
        this.startDate = startDate;
        this.status = status;
        this.municipalityId = municipalityId;
    }

    /** @return the unique business ID */
    public int getBusinessId() {
        return businessId;
    }

    /** @param businessId sets the unique business ID */
    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    /** @return the registered business name */
    public String getBusinessName() {
        return businessName;
    }

    /** @param businessName sets the registered business name */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /** @return the trade or operating name */
    public String getTradeName() {
        return tradeName;
    }

    /** @param tradeName sets the trade or operating name */
    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    /** @return the barangay of the business location */
    public String getBarangay() {
        return barangay;
    }

    /** @param barangay sets the barangay of the business location */
    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    /** @return the street address */
    public String getStreetAddress() {
        return streetAddress;
    }

    /** @param streetAddress sets the street address */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    
    /** @return the business type or classification */
    public String getBusinessType() {
        return businessType;
    }

    /** @param businessType sets the business type or classification */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /** @return the tax identification number */
    public String getTaxId() {
        return taxId;
    }

    /** @param taxId sets the tax identification number */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /** @return the business start date */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @param startDate sets the business start date */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** @return the business status (Active, Suspended, Closed, Pending) */
    public String getStatus() {
        return status;
    }

    /** @param status sets the business status */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return the date when the current status became effective */
    public LocalDate getStatusEffectiveDate() {
        return statusEffectiveDate;
    }

    /** @param statusEffectiveDate sets the effective date of current status */
    public void setStatusEffectiveDate(LocalDate statusEffectiveDate) {
        this.statusEffectiveDate = statusEffectiveDate;
    }
    /** @return the reason for the current status */
    public String getStatusReason() {
        return statusReason;
    }

    /** @param statusReason sets the reason for status change */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /** @return reference to supporting documents */
    public String getSupportDocRef() {
        return supportDocRef;
    }

    /** @param supportDocRef sets the reference to supporting documents */
    public void setSupportDocRef(String supportDocRef) {
        this.supportDocRef = supportDocRef;
    }

    /** @return Municipality */
    public int getMunicipalityId() {
        return municipalityId;
    }

    /** @param municipalityId sets the municipality*/
    public void setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
    }

    @Override
    public String toString() {
        if (ownerCount > 0) {
            return businessName + " (" + ownerCount + " owners linked)";
        }
        return businessName;
    }

    private int ownerCount;

    public int getOwnerCount() { return ownerCount; }
    public void setOwnerCount(int ownerCount) { this.ownerCount = ownerCount; }


}
