package businesspermitsystem.models;

/**
 * Lightweight data model used for issuing or updating
 * a permit status after a passed inspection.
 *
 * Not a database table — just a data bundle.
 */
public class PermitStatusUpdateModel {

    private int inspectionId;
    private int businessId;
    private String businessName;

    private int permitTypeId;
    private String permitTypeName;
    private int validityMonths;

    public int getInspectionId() { return inspectionId; }
    public void setInspectionId(int inspectionId) { this.inspectionId = inspectionId; }

    public int getBusinessId() { return businessId; }
    public void setBusinessId(int businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public int getPermitTypeId() { return permitTypeId; }
    public void setPermitTypeId(int permitTypeId) { this.permitTypeId = permitTypeId; }

    public String getPermitTypeName() { return permitTypeName; }
    public void setPermitTypeName(String permitTypeName) { this.permitTypeName = permitTypeName; }

    public int getValidityMonths() { return validityMonths; }
    public void setValidityMonths(int validityMonths) { this.validityMonths = validityMonths; }

    @Override
    public String toString() {
        return businessName + " (" + permitTypeName + ")";
    }
}
