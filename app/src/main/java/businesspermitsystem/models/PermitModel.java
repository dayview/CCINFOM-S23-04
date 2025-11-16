package businesspermitsystem.models;

import java.util.Date;

/**
 * Represents a Permit in the Business Permit System
 * Contains permit issuance, validity, and status information
 */
public class PermitModel {
    private int permitID;
    private int businessID;
    private int permitTypeID;
    private String status;
    private Date statusEffectiveDate;
    private String note;

    public PermitModel(int permitID, int businessID, int permitTypeID, String status,
                       Date statusEffectiveDate, String note) {
        this.permitID = permitID;
        this.businessID = businessID;
        this.permitTypeID = permitTypeID;
        this.status = status;
        this.statusEffectiveDate = statusEffectiveDate;
        this.note = note;
    }

    public int getPermitID() {
        return permitID;
    }

    public int getBusinessID() {
        return businessID;
    }

    public int getPermitTypeID() {
        return permitTypeID;
    }

    public String getStatus() {
        return status;
    }

    public Date getStatusEffectiveDate() {
        return statusEffectiveDate;
    }

    public String getNote() {
        return note;
    }

    public void setPermitID(int permitID) {
        this.permitID = permitID;
    }

    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    public void setPermitTypeID(int permitTypeID) {
        this.permitTypeID = permitTypeID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatusEffectiveDate(Date statusEffectiveDate) {
        this.statusEffectiveDate = statusEffectiveDate;
    }

    public void setNote(String note) {
        this.note = note;
    }
}