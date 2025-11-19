package businesspermitsystem.db;

import businesspermitsystem.models.PermitModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermitDAO {
    private Connection connection;

    public PermitDAO() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    /**
     * Retrieves all permits from the database
     * @return List of all PermitModel objects
     */
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

    /**
     * Retrieves a single permit by ID
     * @param permitID the permit's unique identifier
     * @return PermitModel or null if not found
     */
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

    /**
     * Retrieves all permits for a specific business
     * @param businessID the business's unique identifier
     * @return List of PermitModel objects for that business
     */
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

    /**
     * Adds a new permit to the database
     * @param permit the PermitModel to insert
     * @return true if successful, false otherwise
     */
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

    /**
     * Updates an existing permit's full record
     * @param permit the PermitModel with updated values
     * @return true if successful, false otherwise
     */
    public boolean updatePermit(PermitModel permit) {
        String query = "UPDATE Permit SET business_id = ?, permit_type_id = ?, status = ?, status_effective_date = ?, note = ? WHERE permit_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, permit.getBusinessID());
            pstmt.setInt(2, permit.getPermitTypeID());
            pstmt.setString(3, permit.getStatus());
            pstmt.setDate(4, new java.sql.Date(permit.getStatusEffectiveDate().getTime()));
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

    /**
     * Deletes a permit from the database
     * @param permitID the permit's unique identifier
     * @return true if successful, false otherwise
     */
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
        String query = "UPDATE Permit SET status = ?, status_effective_date = ?, note = ? WHERE permit_id = ?";

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

    /**
     * Updates status for ALL permits belonging to a business (bulk update).
     * Used when a business status changes - all its permits must follow.
     *
     * Example: Business gets suspended and all its permits get suspended (or archived) too.
     *
     * @param businessID the business whose permits should be updated
     * @param newStatus the new status for all permits
     * @param effectiveDate when the status change takes effect
     * @param note reason or note about the bulk status change
     * @return number of permits updated
     */
    public int updatePermitsByBusinessID(int businessID, String newStatus, java.util.Date effectiveDate, String note) {
        String query = "UPDATE Permit SET status = ?, status_effective_date = ?, note = ? WHERE business_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatus);
            pstmt.setDate(2, new java.sql.Date(effectiveDate.getTime()));
            pstmt.setString(3, note);
            pstmt.setInt(4, businessID);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " permit(s) for business ID " + businessID);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error bulk updating permits for business: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}