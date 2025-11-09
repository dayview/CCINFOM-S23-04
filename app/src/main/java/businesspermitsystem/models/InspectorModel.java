package businesspermitsystem.models;

/**
 * The model for the inspector data from the database
 */
public class InspectorModel {
    /**
     * The main primary key and unique identifier of each inspector
     */
    private int inspectorID;
    /**
     * The last name of the inspector
     */
    private String lastName;
    /**
     * The first name of each inspector
     */
    private String firstName;
    /**
     * The middle name of each inspector
     */
    private String middleName;
    /**
     * Formal title or the inspector's offical authorized and specific inspection duty (e.g fire inspector, health and sanitation inspector).
     */
    private String designation;
    /**
     * Unique license number of the inspector
     */
    private String licenseNumber;
    /**
     * Boolean that states if the inspector is currently active in service
     */
    private Boolean active;
    /**
     * The Foreign key and ID of the municipality where the inspector is employed.
     */
    private String municipalityID;

    public int getInspectorID() {
        return inspectorID;
    }

    public void setInspectorID(int inspectorID) {
        this.inspectorID = inspectorID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public Boolean isActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getMunicipalityID() {
        return municipalityID;
    }
    
    public void setMunicipalityID(String municipalityID) {
        this.municipalityID = municipalityID;
    }
}
