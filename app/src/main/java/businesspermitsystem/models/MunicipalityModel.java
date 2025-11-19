package businesspermitsystem.models;

/**
 * Represents a Municipality (city or town) record in the Business Permit System.
 */
public class MunicipalityModel {
    /**
     * The main primary key and unique identifier of each municipality
     */
    private int municipalityID;
    /**
     * The name of each municipality
     */
    private String municipalityName;
    /**
     * The province in which this municipality is located
     */
    private String province;
    /**
     * The region in which the municipality is location.
     */
    private String region;
    /**
     * The classification of the municipality based on their average annual income
     */
    private String classification;
    /**
     * The main contact number of the municipality
     */
    private String contactNumber;
    /**
     * The street where the office of the municipality is located
     */
    private String officeStreet;

    /**
     * The barangay where the office of the municipality is located
     */
    private String officeBarangay;

    /**
     * The zipcode of the location where the office of the municipality is located
     */
    private String officeZipCode;


    /**
     * Constructor for the MunicipalityModel.
     */
    public MunicipalityModel(int municipalityID, String municipalityName, String province, String region,String classification , String contactNumber, String officeStreet, String officeBarangay, String officeZipCode) {
        this.municipalityID = municipalityID;
        this.municipalityName = municipalityName;
        this.region = region;
        this.contactNumber = contactNumber;
        this.province = province;
        this.classification = classification;
        this.officeBarangay = officeBarangay;
        this.officeStreet = officeStreet;
        this.officeZipCode = officeZipCode;
    }
    
    // --- Getters ---

    public int getMunicipalityID() {
        return municipalityID;
    }
    public String getMunicipalityName() {
        return municipalityName;
    }
    public String getRegion() {
        return region;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public String getProvince() {
        return province;
    }
    public String getClassification() {
        return classification;
    }
    public String getOfficeStreet() {
        return officeStreet;
    }
    public String getOfficeBarangay() {
        return officeBarangay;
    }
    public String getOfficeZipCode() {
        return officeZipCode;
    }
    
    // --- Setters ---

    public void setMunicipalityID(int municipalityID) {
        this.municipalityID = municipalityID;
    }
    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }
    public void setOfficeStreet(String officeStreet) {
        this.officeStreet = officeStreet;
    }
    public void setOfficeBarangay(String officeBarangay) {
        this.officeBarangay = officeBarangay;
    }
    public void setOfficeZipCode(String officeZipCode) {
        this.officeZipCode = officeZipCode;
    }

    /**
     * Provides a concatenated string representation of the location.
     */
    @Override
    public String toString() {
        return municipalityName + ", " + region + ", " + province;
    }
}