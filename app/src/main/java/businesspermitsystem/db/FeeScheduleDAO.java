package businesspermitsystem.db;

import businesspermitsystem.models.FeeScheduleModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeScheduleDAO {
    private Connection connection;

    public FeeScheduleDAO() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public List<FeeScheduleModel> getAllFeeSchedules() {
        List<FeeScheduleModel> feeSchedules = new ArrayList<>();
        String query = "SELECT * FROM FeeSchedule";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                FeeScheduleModel feeSchedule = new FeeScheduleModel(
                        rs.getInt("fee_schedule_id"),
                        rs.getDouble("base_fee"),
                        rs.getString("surcharge_rule"),
                        rs.getInt("validity_months"),
                        rs.getString("document_requirements")
                );
                feeSchedules.add(feeSchedule);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fee schedules: " + e.getMessage());
            e.printStackTrace();
        }
        return feeSchedules;
    }

    public FeeScheduleModel getFeeScheduleByID(int feeScheduleID) {
        String query = "SELECT * FROM FeeSchedule WHERE fee_schedule_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, feeScheduleID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new FeeScheduleModel(
                            rs.getInt("fee_schedule_id"),
                            rs.getDouble("base_fee"),
                            rs.getString("surcharge_rule"),
                            rs.getInt("validity_months"),
                            rs.getString("document_requirements")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fee schedule: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addFeeSchedule(FeeScheduleModel feeSchedule) {
        String query = "INSERT INTO FeeSchedule (base_fee, surcharge_rule, validity_months, document_requirements) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, feeSchedule.getBaseFee());
            pstmt.setString(2, feeSchedule.getSurchargeRule());
            pstmt.setInt(3, feeSchedule.getValidityMonths());
            pstmt.setString(4, feeSchedule.getDocumentRequirements());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error addiing fee schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFeeSchedule(FeeScheduleModel feeSchedule) {
        String query = "UPDATE FeeSchedule SET base_fee = ?, surcharge_rule = ?, validity_months = ?, document_requirements = ? WHERE fee_schedule_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, feeSchedule.getBaseFee());
            pstmt.setString(2, feeSchedule.getSurchargeRule());
            pstmt.setInt(3, feeSchedule.getValidityMonths());
            pstmt.setString(4, feeSchedule.getDocumentRequirements());
            pstmt.setInt(5, feeSchedule.getID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fee schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFeeSchedule(int feeScheduleID) {
        String query = "DELETE FROM FeeSchedule WHERE fee_schedule_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, feeScheduleID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting fee schedule: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}