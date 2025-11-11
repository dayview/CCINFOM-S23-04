package businesspermitsystem.models;

/**
 * 
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
     * The region in which the municipality is location.
     */
    private String region;
    /**
     * The main contact number of the municipality
     */
    private String contactNumber;
    /**
     * The province in which this municipality is located
     */
    private String province;
    /**
     * @todo Confirm what this means
     */
    private String classification;
    public MunicipalityModel(int municipalityID, String municipalityName, String region, String contactNumber, String province, String classification) {
        this.municipalityID = municipalityID;
        this.municipalityName = municipalityName;
        this.region = region;
        this.contactNumber = contactNumber;
        this.province = province;
        this.classification = classification;
    }
    public int getMunicipalityID() {
        return municipalityID;
    }
    public void setMunicipalityID(int municipalityID) {
        this.municipalityID = municipalityID;
    }
    public String getMunicipalityName() {
        return municipalityName;
    }
    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getClassification() {
        return classification;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }
}
