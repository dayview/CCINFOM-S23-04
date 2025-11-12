package businesspermitsystem.models;

import java.time.LocalDate;

/**
 *
 */
public class BusinessModel {

    /**
     *
     */
    private int businessId;          // private key
    /**
     *
     */
    private String businessName;
    /**
     *
     */
    private String tradeName;
    /**
     *
     */
    private String barangay;
    /**
     *
     */
    private String streetAddress;
    /**
     *
     */
    private String city;
    /**
     *
     */
    private String province;

    /**
     *
     */
    private String businessType;
    /**
     *
     */
    private String taxId;
    /**
     *
     */
    private LocalDate startDate;
    /**
     *
     */
    private String status;            //can be active, suspended, closed, or pending
    /**
     *
     */
    private int municipalityId;       // foreign key from MunicipalityModel


    /**
     *
     */
    public BusinessModel() {
    }

    /**
     *
     */
    public BusinessModel(int businessId, String businessName, String tradeName,
                         String streetAddress, String barangay, String city, String province,
                         String businessType, String taxId, LocalDate startDate,
                         String status, int municipalityId) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.tradeName = tradeName;
        this.streetAddress = streetAddress;
        this.barangay = barangay;
        this.city = city;
        this.province = province;
        this.businessType = businessType;
        this.taxId = taxId;
        this.startDate = startDate;
        this.status = status;
        this.municipalityId = municipalityId;
    }


    // ============ Getters and Setters ============

    /**
     *
     */
    public int getBusinessId() {
        return businessId;
    }

    /**
     *
     */
    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    /**
     *
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     *
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     *
     */
    public String getTradeName() {
        return tradeName;
    }

    /**
     *
     */
    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    /**
     *
     */
    public String getBarangay() {
        return barangay;
    }

    /**
     *
     */
    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    /**
     *
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     *
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     *
     */

    public String getCity() {
        return city;
    }

    /**
     *
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     */
    public String getProvince() {
        return province;
    }

    /**
     *
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     *
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     *
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     *
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     *
     */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /**
     *
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     */
    public int getMunicipalityId() {
        return municipalityId;
    }

    /**
     *
     */
    public void setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
    }


}
