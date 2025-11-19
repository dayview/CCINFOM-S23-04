package businesspermitsystem.db;

import businesspermitsystem.models.PermitTypeModel;
import businesspermitsystem.models.FeeScheduleModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermitTypeDAO {
    private Connection connection;
    private FeeScheduleDAO feeScheduleDAO;

    public PermitTypeDAO() {
        this.connection = DatabaseConnector.connection;
        this.feeScheduleDAO = new FeeScheduleDAO();

        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public List<PermitTypeModel> getAllPermitTypes() {
        List<PermitTypeModel> permitTypes = new ArrayList<>();
        String query = "SELECT * FROM permit_type";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int feeScheduleID = rs.getInt("fee_schedule_id");
                FeeScheduleModel feeSchedule = feeScheduleDAO.getFeeScheduleByID(feeScheduleID);

                PermitTypeModel permitType = new PermitTypeModel(
                        rs.getInt("permit_type_id"),
                        rs.getString("permit_name"),
                        feeSchedule,
                        rs.getString("document_requirements"),
                        rs.getInt("validity_months")
                );
                permitTypes.add(permitType);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permit type: " + e.getMessage());
            e.printStackTrace();
    }
    return permitTypes;
    }

    public PermitTypeModel getPermitTypeByID(int permitTypeID) {
        String query = "SELECT * FROM permit_type WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitTypeID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int feeScheduleID = rs.getInt("fee_schedule_id");
                    FeeScheduleModel feeSchedule = feeScheduleDAO.getFeeScheduleByID(feeScheduleID);

                    return new PermitTypeModel(
                            rs.getInt("permit_type_id"),
                            rs.getString("permit_name"),
                            feeSchedule,
                            rs.getString("document_requirements"),
                            rs.getInt("validity_months")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permit type: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPermitType(PermitTypeModel permitType) {
        String query = "INSERT INTO permit_type (permit_name, fee_schedule_id, document_requirements, validity_months) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, permitType.getName());
            pstmt.setInt(2, permitType.getFeeSchedule().getID());
            pstmt.setString(3, permitType.getDocumentRequirements());
            pstmt.setInt(4, permitType.getValidityMonths());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding permit type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePermitType(PermitTypeModel permitType) {
        String query = "UPDATE permit_type SET permit_name = ?, fee_schedule_id = ?, document_requirements = ?, validity_months = ? WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, permitType.getName());
            pstmt.setInt(2, permitType.getFeeSchedule().getID());
            pstmt.setString(3, permitType.getDocumentRequirements());
            pstmt.setInt(4, permitType.getValidityMonths());
            pstmt.setInt(5, permitType.getID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating permit type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePermitType(int permitTypeID) {
        String query = "DELETE FROM permit_type WHERE permit_type_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitTypeID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting permit type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}