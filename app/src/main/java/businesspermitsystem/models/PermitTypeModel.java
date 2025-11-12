package businesspermitsystem.models;

import java.sql.*;
import util.DatabaseConnection;

/* This class is exclusively handling the permit data (types & data) of a business */

/* Load PermitType (master record), pick the active FeeSchedule for that type and date
   Pick the active FeeSchedule for that type and date
   subtotal = feeSchedule.computeSubtotal(permitType, feeContext)
   taxes = taxCalculator.computeTaxes(subtotal, taxContent)
   total = subtotal + taxes.totalTax
 */

/* Edge cases to handle
 * Late renewal penalties (surcharge vs separate fee line)
 * Tax-exempt applicants
 * Tax-inclusive jurisdictions (prices include VAT)
 * Caps/maximum surcharge
 * Mid-year rule changes (effective dates)
 * Backdated filings (use filing/approval date consistently)
 */

/**
 * Represents a Permit Type and Fee Schedule in the Business Permit System
 * Contains information about permit categories, fees, validity, and requirements
 */
public class PermitTypeModel {
    private int permitTypeID;
    private String permitName;
    private FeeSchedule feeSchedule;
    private String documentRequirements;
    private int validityMonths;

    /**
     * Constructor with all fields (for creating new records)
     * @param permitTypeID
     * @param permitName
     * @param feeSchedule
     */
    public PermitTypeModel(int permitTypeID, String permitName, FeeSchedule feeSchedule, String documentRequirements, int validityMonths) {
        this.permitTypeID = permitTypeID;
        this.permitName = permitName;
        this.feeSchedule = feeSchedule;
        this.documentRequirements = documentRequirements;
        this.validityMonths = validityMonths;
    }

    public int getID() {
        return permitTypeID;
    }

    public String getName() {
        return permitName;
    }

    public FeeSchedule getFeeSchedule() {
        return feeSchedule;
    }

    public String getDocumentRequirements() {
        return documentRequirements;
    }

    public int getValidityMonths() {
        return validityMonths;
    }

    public void setID(int permitTypeID) {
        this.permitTypeID = permitTypeID;
    }

    public void setName(String permitName) {
        this.permitName = permitName;
    }

    public void setFeeSchedule(FeeSchedule feeSchedule) {
        this.feeSchedule = feeSchedule;
    }

    public void setDocumentRequirements(String documentRequirements) {
        this.documentRequirements = documentRequirements;
    }

    public void setValidityMonths(int validityMonths) {
        this.validityMonths = validityMonths;
    }
}