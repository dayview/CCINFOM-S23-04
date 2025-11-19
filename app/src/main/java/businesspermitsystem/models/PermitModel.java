package businesspermitsystem.models;

import java.sql.Date;

/**
 * Represents a permit issued to a business within the Business Permit System.
 *
 * <p>This model corresponds directly to the {@code permit} table in the database
 * and stores information about permit issuance, status, and administrative notes.</p>
 *
 * <p>Fields include:
 * <ul>
 *     <li>permitID – Unique identifier for the permit</li>
 *     <li>businessID – The business to which the permit belongs</li>
 *     <li>permitTypeID – The type/category of permit</li>
 *     <li>status – Current state (e.g., "Issued", "Suspended", "Revoked")</li>
 *     <li>statusEffectiveDate – When the permit’s current status took effect</li>
 *     <li>note – Optional remarks or administrative notes</li>
 * </ul>
 * </p>
 */
public class PermitModel {

    /** Unique primary key of the permit. */
    private int permitID;

    /** Foreign key referencing the business that owns the permit. */
    private int businessID;

    /** Foreign key referencing the permit type. */
    private int permitTypeID;

    /** Current status of the permit (e.g., Issued, Suspended). */
    private String status;

    /** Date when the permit’s current status became effective. */
    private Date statusEffectiveDate;

    /** Additional notes or remarks related to the permit. */
    private String note;

    /**
     * Constructs a new PermitModel.
     *
     * @param permitID            unique identifier of the permit
     * @param businessID          ID of the business the permit belongs to
     * @param permitTypeID        ID of the permit type
     * @param status              permit status string
     * @param statusEffectiveDate date when the status took effect
     * @param note                optional remarks
     */
    public PermitModel(int permitID, int businessID, int permitTypeID, String status,
                       Date statusEffectiveDate, String note) {
        this.permitID = permitID;
        this.businessID = businessID;
        this.permitTypeID = permitTypeID;
        this.status = status;
        this.statusEffectiveDate = statusEffectiveDate;
        this.note = note;
    }

    /** @return the permit ID */
    public int getPermitID() {
        return permitID;
    }

    /** @return the business ID associated with this permit */
    public int getBusinessID() {
        return businessID;
    }

    /** @return the permit type ID */
    public int getPermitTypeID() {
        return permitTypeID;
    }

    /** @return the current status of the permit */
    public String getStatus() {
        return status;
    }

    /** @return the date when the status became effective */
    public Date getStatusEffectiveDate() {
        return statusEffectiveDate;
    }

    /** @return additional notes related to the permit */
    public String getNote() {
        return note;
    }

    /**
     * Sets the permit's unique identifier.
     *
     * @param permitID the new permit ID
     */
    public void setPermitID(int permitID) {
        this.permitID = permitID;
    }

    /**
     * Sets the business ID this permit belongs to.
     *
     * @param businessID the new business ID
     */
    public void setBusinessID(int businessID) {
        this.businessID = businessID;
    }

    /**
     * Sets the permit type ID.
     *
     * @param permitTypeID the new permit type ID
     */
    public void setPermitTypeID(int permitTypeID) {
        this.permitTypeID = permitTypeID;
    }

    /**
     * Updates the current status of the permit.
     *
     * @param status the new permit status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the effective date of the current permit status.
     *
     * @param statusEffectiveDate the date when the status became effective
     */
    public void setStatusEffectiveDate(Date statusEffectiveDate) {
        this.statusEffectiveDate = statusEffectiveDate;
    }

    /**
     * Sets additional comments or notes about the permit.
     *
     * @param note the new note text
     */
    public void setNote(String note) {
        this.note = note;
    }
}
