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
}
