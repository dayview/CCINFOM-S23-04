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
public class PermitType {
    private int permitTypeId;
    private String permitName;
    private double baseFee;
    private String surchargeRule;
    private int validityMonths;
    private String documentRequirements;

    public PermitType() {
    }

    /**
     * Constructor with all fields (for creating new records)
     * @param permitName
     * @param baseFee
     * @param surchargeRule
     * @param validityMonths
     * @param documentRequirements
     */
    public PermitType(String permitName, double baseFee, String surchargeRule, int validityMonths, String documentRequirements) {
        this.permitName = permitName;
        this.baseFee = baseFee;
        this.surchargeRule = surchargeRule;
        this.validityMonths = validityMonths;
        this.documentRequirements = documentRequirements;
    }

    /**
     * Constructor with ID (for existing records from database)
     */
    public PermitType(int permitTypeId, String permitName, double baseFee,
                      String surchargeRule, int validityMonths, String documentRequirements) {
        this.permitTypeId = permitTypeId;
        this.permitName = permitName;
        this.baseFee = baseFee;
        this.surchargeRule = surchargeRule;
        this.validityMonths = validityMonths;
        this.documentRequirements = documentRequirements;
    }

    public int getPermitTypeId() {
        return permitTypeId;
    }

    public void setPermitTypeId(int permitTypeId) {
        this.permitTypeId = permitTypeId;
    }

    public String getPermitName() {
        return permitName;
    }

    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(double baseFee) {
        this.baseFee = baseFee;
    }

    public String getSurchargeRule() {
        return surchargeRule;
    }

    public void setSurchargeRule(String surchargeRule) {
        this.surchargeRule = surchargeRule;
    }

    public int getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(int validityMonths) {
        this.validityMonths = validityMonths;
    }

    public String getDocumentRequirements() {
        return documentRequirements;
    }

    public void setDocumentRequirements(String documentRequirements) {
        this.documentRequirements = documentRequirements;
    }

    /**
     * CREATE - Insert new permit type record into database
     * @return true if successfully created, false otherwise
     */
    public boolean create() {
        String sql = "INSERT INTO permit_type (permit_name, base_fee, surcharge_rule," +
                     "validity_months, document_requirements) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, this.permitName);
            pstmt.setDouble(2, this.baseFee);
            pstmt.setString(3, this.surchargeRule);
            pstmt.setInt(4, this.validityMonths);
            pstmt.setString(5, this.documentRequirements);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    this.permitTypeId = rs.getInt(1);
                }
                System.out.println("Permit type created successfully with ID: " + this.permitTypeId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error creating permit type: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * READ - Retrieve a permit type record by ID
     * @param permitTypeId the ID of the permit type to retrieve
     * @return permitType object if found, null otherwise
     */
    public static PermitType read(int permitTypeId) {
        String sql = "SELECT * FROM permit_type WHERE permit_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, permitTypeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PermitType(
                        rs.getInt("permit_type_id"),
                        rs.getString("permit_name"),
                        rs.getDouble("base_fee"),
                        rs.getString("surcharge_rule"),
                        rs.getInt("validity_months"),
                        rs.getString("document_requirements")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error reading permit type: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UPDATE - Update existing permit type record in database
     * @return true if successfully updated, false otherwise
     */
    public boolean update() {
        String sql = "UPDATE permit_type SET permit_name = ?, base_fee = ?, " +
                     "surcharge_rule = ?, validity_months = ?, document_requirements = ? " +
                     "WHERE permit_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.permitName);
            pstmt.setDouble(2, this.baseFee);
            pstmt.setString(3, this.surchargeRule);
            pstmt.setInt(4, this.validityMonths);
            pstmt.setString(5, this.documentRequirements);
            pstmt.setInt(6, this.permitTypeId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Permit type updated successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error updating permit type: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * DELETE - Remove permit type record from database
     * @return true if successfully deleted, false otherwise
     */
    public boolean delete() {
        String sql = "DELETE FROM permit_type WHERE permit_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, this.permitTypeId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Permit type deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting permit type: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to display permit type information
     */
    @Override
    public String toString() {
        return "PermitType{" +
               "permitTypeId=" + permitTypeId +
               ", permitName='" + permitName + '\'' +
               ", baseFee=" + baseFee +
               ", surchargeRule'" + surchargeRule + '\'' +
               ", validityMonths=" + validityMonths +
               ", documentRequirements='" + documentRequirements + '\'' +
               '}';
    }
}