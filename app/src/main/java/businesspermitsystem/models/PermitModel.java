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
    private Date validityStart;
    private Date validityEnd;

    public PermitModel(int permitID, int businessID, int permitTypeID, String status,
                       Date statusEffectiveDate, String note, Date validityStart, Date validityEnd) {
        this.permitID = permitID;
        this.businessID = businessID;
        this.permitTypeID = permitTypeID;
        this.status = status;
        this.statusEffectiveDate = statusEffectiveDate;
        this.note = note;
        this.validityStart = validityStart;
        this.validityEnd = validityEnd;
    }

    public int getPermitID() {
        return permitID;
    }

    public void setPermitID(int permitID) {
        this.permitID = permitID;
    }

    public int getBusinessID() {
        return businessID;
    }

    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    public int getPermitTypeID() {
        return permitTypeID;
    }

    public void setPermitTypeID(int permitTypeID) {
        this.permitTypeID = permitTypeID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusEffectiveDate() {
        return statusEffectiveDate;
    }

    public void setStatusEffectiveDate(Date statusEffectiveDate) {
        this.statusEffectiveDate = statusEffectiveDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getValidityStart() {
        return validityStart;
    }

    public void setValidityStart(Date validityStart) {
        this.validityStart = validityStart;
    }

    public Date getValidityEnd() {
        return validityEnd;
    }

    public void setValidityEnd(Date validityEnd) {
        this.validityEnd = validityEnd;
    }
}