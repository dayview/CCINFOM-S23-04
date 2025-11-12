package businesspermitsystem.services;

import businesspermitsystem.models.PermitModel;
import businesspermitsystem.db.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermitService {
    private Connection connection;

    public PermitService() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public List<PermitModel> getAllPermits() {
        List<PermitModel> permits = new ArrayList<>();
        String query = "SELECT * FROM Permit";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                PermitModel permit = new PermitModel(
                        rs.getInt("permit_id"),
                        rs.getInt("business_id"),
                        rs.getInt("permit_type_id"),
                        rs.getString("status"),
                        rs.getDate("status_effective_date"),
                        rs.getString("note")
                );
                permits.add(permit);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permits: " + e.getMessage());
            e.printStackTrace();
        }
        return permits;
    }

    public PermitModel getPermitByID(int permitID) {
        String query = "SELECT * FROM Permit WHERE permit_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PermitModel(
                            rs.getInt("permit_id"),
                            rs.getInt("business_id"),
                            rs.getInt("permit_type_id"),
                            rs.getString("status"),
                            rs.getDate("status_effective_date"),
                            rs.getString("note")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permit: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<PermitModel> getPermitsByBusinessID(int businessID) {
        List<PermitModel> permits = new ArrayList<>();
        String query = "SELECT * FROM Permit WHERE business_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, businessID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PermitModel permit = new PermitModel(
                        rs.getInt("permit_id"),
                        rs.getInt("business_id"),
                        rs.getInt("permit_type_id"),
                        rs.getString("status"),
                        rs.getDate("status_effective_date"),
                        rs.getString("note")
                    );
                    permits.add(permit);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permits for business: " + e.getMessage());
            e.printStackTrace();
        }
        return permits;
    }

    public boolean addPermit(PermitModel permit) {
        String query = "INSERT INTO Permit (business_id, permit_type_id, status, status_effective_date, note) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permit.getBusinessID());
            pstmt.setInt(2, permit.getPermitTypeID());
            pstmt.setString(3, permit.getStatus());
            pstmt.setDate(4, new java.sql.Date(permit.getStatusEffectiveDate().getTime()));
            pstmt.setString(5, permit.getNote());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding permit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePermit(PermitModel permit) {
        String query = "UPDATE Permit SET business_id = ?, permit_type_id = ?, status = ?, status_effective_date = ?, note = ? WHERE permit_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permit.getBusinessID());
            pstmt.setInt(2, permit.getPermitTypeID());
            pstmt.setString(3, permit.getStatus());
            pstmt.setDate(4, new java.sql.Date(permit.getStatusEffectiveDate.getTime()));
            pstmt.setString(5, permit.getNote());
            pstmt.setInt(6, permit.getPermitID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating permit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePermit(int permitID) {
        String query = "DELETE FROM Permit WHERE permit_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permitID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting permit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePermitStatus(int permitID, String newStatus, java.util.Date effectiveDate, String note) {
        String query = "UPDATE Permit SET status = ?, status_effective_date = ?, note = ?, WHERE permit_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setDate(2, new java.sql.Date(effectiveDate.getTime()));
            pstmt.setString(3, note);
            pstmt.setInt(4, permitID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating permit status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}