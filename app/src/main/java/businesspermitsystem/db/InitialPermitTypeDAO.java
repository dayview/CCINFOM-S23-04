package businesspermitsystem.db;

import businesspermitsystem.models.InitialPermitTypeModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InitialPermitTypeDAO {

    private Connection connection;

    public InitialPermitTypeDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    // Retrieve all permit types
    public List<InitialPermitTypeModel> getAllPermitTypes() {
        List<InitialPermitTypeModel> permitTypes = new ArrayList<>();
        String query = "SELECT * FROM permit_type";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                InitialPermitTypeModel permitType = new InitialPermitTypeModel(
                        rs.getInt("permit_type_id"),
                        rs.getString("permit_name"),
                        rs.getBigDecimal("base_fee"),
                        rs.getString("surcharge_rule"),
                        rs.getInt("validity_months"),
                        rs.getString("document_requirements")
                );
                permitTypes.add(permitType);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving permit types: " + e.getMessage());
            e.printStackTrace();
        }

        return permitTypes;
    }

    // Retrieve a single permit type by ID
    public InitialPermitTypeModel getPermitTypeByID(int permitTypeID) {
        String query = "SELECT * FROM permit_type WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitTypeID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new InitialPermitTypeModel(
                            rs.getInt("permit_type_id"),
                            rs.getString("permit_name"),
                            rs.getBigDecimal("base_fee"),
                            rs.getString("surcharge_rule"),
                            rs.getInt("validity_months"),
                            rs.getString("document_requirements")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving permit type: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Add a new permit type
    public boolean addPermitType(InitialPermitTypeModel permitType) {
        String query = "INSERT INTO permit_type " +
                "(permit_name, base_fee, surcharge_rule, validity_months, document_requirements) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, permitType.getPermitName());
            pstmt.setBigDecimal(2, permitType.getBaseFee());
            pstmt.setString(3, permitType.getSurchargeRule());
            pstmt.setInt(4, permitType.getValidityMonths());
            pstmt.setString(5, permitType.getDocumentRequirements());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding permit type: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Update a permit type
    public boolean updatePermitType(InitialPermitTypeModel permitType) {
        String query = "UPDATE permit_type SET permit_name = ?, base_fee = ?, surcharge_rule = ?, " +
                "validity_months = ?, document_requirements = ? WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, permitType.getPermitName());
            pstmt.setBigDecimal(2, permitType.getBaseFee());
            pstmt.setString(3, permitType.getSurchargeRule());
            pstmt.setInt(4, permitType.getValidityMonths());
            pstmt.setString(5, permitType.getDocumentRequirements());
            pstmt.setInt(6, permitType.getPermitTypeId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating permit type: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Delete a permit type
    public boolean deletePermitType(int permitTypeID) {
        String query = "DELETE FROM permit_type WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitTypeID);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting permit type: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
