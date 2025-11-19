package businesspermitsystem.db;

import businesspermitsystem.models.AuditLogModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AuditLog table.
 * Handles logging all changes to critical business data.
 */
public class AuditLogDAO {

    private Connection connection;

    /**
     * Constructor - initializes database connection
     */
    public AuditLogDAO() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    /**
     * Adds a new audit log entry to the database.
     * Called whenever a critical record is created, updated, or deleted.
     *
     * @param auditLog the AuditLogModel to insert
     * @return true if successful, false otherwise
     */
    public boolean addAuditLog(AuditLogModel auditLog) {
        String query = "INSERT INTO audit_log (entity, entity_id, action, changed_by_user, change_datetime, change_summary) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, auditLog.getEntity());
            pstmt.setInt(2, auditLog.getEntityId());
            pstmt.setString(3, auditLog.getAction());
            pstmt.setString(4, auditLog.getChangedByUser());
            pstmt.setTimestamp(5, auditLog.getChangedDateTime());
            pstmt.setString(6, auditLog.getChangeSummary());

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Error adding audit log: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all audit logs for a specific entity type and ID.
     * Example: Get all changes made to Business with ID 5
     *
     * @param entity the entity type (Business, Permit, Payment, etc.)
     * @param entityId the specific record ID
     * @return List of AuditLogModel objects
     */
    public List<AuditLogModel> getAuditLogsByEntity(String entity, int entityId) {
        List<AuditLogModel> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE entity = ? AND entity_id = ? ORDER BY change_datetime DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, entity);
            pstmt.setInt(2, entityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLogModel auditLog = new AuditLogModel(
                            rs.getInt("audit_id"),
                            rs.getString("entity"),
                            rs.getInt("entity_id"),
                            rs.getString("action"),
                            rs.getString("changed_by_user"),
                            rs.getTimestamp("change_datetime"),
                            rs.getString("change_summary")
                    );
                    auditLogs.add(auditLog);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by entity: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }

    /**
     * Retrieves all audit logs made by a specific user.
     * Useful for tracking who made what changes.
     *
     * @param username the username who made changes
     * @return List of AuditLogModel objects
     */
    public List<AuditLogModel> getAuditLogsByUser(String username) {
        List<AuditLogModel> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE changed_by_user = ? ORDER BY change_datetime DESC LIMIT 1000";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLogModel auditLog = new AuditLogModel(
                            rs.getInt("audit_id"),
                            rs.getString("entity"),
                            rs.getInt("entity_id"),
                            rs.getString("action"),
                            rs.getString("changed_by_user"),
                            rs.getTimestamp("change_datetime"),
                            rs.getString("change_summary")
                    );
                    auditLogs.add(auditLog);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by user: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }

    /**
     * Retrieves all audit logs within a date range.
     * Useful for generating compliance reports.
     *
     * @param startDate beginning of date range
     * @param endDate end of date range
     * @return List of AuditLogModel objects
     */
    public List<AuditLogModel> getAuditLogsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<AuditLogModel> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE change_datetime BETWEEN ? AND ? ORDER BY change_datetime DESC LIMIT 5000";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLogModel auditLog = new AuditLogModel(
                            rs.getInt("audit_id"),
                            rs.getString("entity"),
                            rs.getInt("entity_id"),
                            rs.getString("action"),
                            rs.getString("changed_by_user"),
                            rs.getTimestamp("change_datetime"),
                            rs.getString("change_summary")
                    );
                    auditLogs.add(auditLog);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }

    /**
     * Retrieves all audit logs (with reasonable limit for performance).
     * Used for comprehensive audit reports.
     *
     * @return List of all AuditLogModel objects (limited to most recent 1000)
     */
    public List<AuditLogModel> getAllAuditLogs() {
        List<AuditLogModel> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_log ORDER BY change_datetime DESC LIMIT 1000";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                AuditLogModel auditLog = new AuditLogModel(
                        rs.getInt("audit_id"),
                        rs.getString("entity"),
                        rs.getInt("entity_id"),
                        rs.getString("action"),
                        rs.getString("changed_by_user"),
                        rs.getTimestamp("change_datetime"),
                        rs.getString("change_summary")
                );
                auditLogs.add(auditLog);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all audit logs: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }

    /**
     * Retrieves audit logs filtered by action type.
     * Example: Get all CREATE, UPDATE, or DELETE actions
     *
     * @param action the action type (CREATE, UPDATE, DELETE)
     * @return List of AuditLogModel objects
     */
    public List<AuditLogModel> getAuditLogsByAction(String action) {
        List<AuditLogModel> auditLogs = new ArrayList<>();
        String query = "SELECT * FROM audit_log WHERE action = ? ORDER BY change_datetime DESC LIMIT 1000";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, action);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AuditLogModel auditLog = new AuditLogModel(
                            rs.getInt("audit_id"),
                            rs.getString("entity"),
                            rs.getInt("entity_id"),
                            rs.getString("action"),
                            rs.getString("changed_by_user"),
                            rs.getTimestamp("change_datetime"),
                            rs.getString("change_summary")
                    );
                    auditLogs.add(auditLog);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving audit logs by action: " + e.getMessage());
            e.printStackTrace();
        }

        return auditLogs;
    }
}
