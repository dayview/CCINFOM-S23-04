package businesspermitsystem.services;

import businesspermitsystem.db.*;
import businesspermitsystem.models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BusinessStatusUpdateService {

    private final BusinessDAO businessDAO;
    private final OwnerDAO ownerDAO;
    private final PermitDAO permitDAO;
    private final NotificationLogDAO notificationLogDAO;
    private final AuditLogDAO auditLogDAO;

    /**
     * Constructor - initializes all required DAOs
     */
    public BusinessStatusUpdateService() {
        this.businessDAO = new BusinessDAO();
        this.ownerDAO = new OwnerDAO();
        this.permitDAO = new PermitDAO();
        this.notificationLogDAO = new NotificationLogDAO();
        this.auditLogDAO = new AuditLogDAO();
    }

    /**
     * Main method to execute Business Status Update Transaction.
     * Validates inputs, updates database, sends notifications, and logs audit trail.
     *
     * @param businessId the business to update
     * @param newStatus the new status (Active, Suspended, Closed, Revoked, etc.)
     * @param effectiveDate when the status change takes effect
     * @param reason justification for the status change
     * @param supportDocRef reference to supporting documents
     * @param changedByUser the username of the staff making the change
     * @param notificationChannel "SMS" or "email"
     * @return StatusUpdateResult containing success flag and message
     */
    public StatusUpdateResult updateBusinessStatus(
            int businessId,
            String newStatus,
            LocalDate effectiveDate,
            String reason,
            String supportDocRef,
            String changedByUser,
            String notificationChannel) {

        Connection conn = DatabaseConnector.connection;

        try {
            BusinessModel business = businessDAO.getBusinessByID(businessId);
            if (business == null) {
                return new StatusUpdateResult(false, "Business not found with ID: " + businessId);
            }

            String oldStatus = business.getStatus();
            if (oldStatus.equals(newStatus)) {
                return new StatusUpdateResult(false, "New status is the same as current status");
            }

            if (!isValidStatus(newStatus)) {
                return new StatusUpdateResult(false, "Invalid status: " + newStatus);
            }


            conn.setAutoCommit(false);

            boolean businessUpdated = businessDAO.updateBusinessStatus(businessId, newStatus, effectiveDate, reason, supportDocRef);
            if (!businessUpdated) {
                conn.rollback();
                return new StatusUpdateResult(false, "Failed to update business status");
            }

            if (shouldCascadeToPermits(newStatus)) {
                String permitNote = "Permit status updated due to business status change to: " + newStatus;
                int permitsUpdated = permitDAO.updatePermitsByBusinessID(businessId, newStatus, new java.util.Date(), permitNote);
                if (permitsUpdated == 0) {
                    System.out.println("Warning: No permits were updated for business " + businessId);
                }
            }

            List<OwnerModel> owners = getOwnersForBusiness(businessId);
            if (owners.isEmpty()) {
                System.out.println("Warning: No owners found for business " + businessId);
            }

            for (OwnerModel owner : owners) {
                String subject = "Business Status Update: " + business.getBusinessName();
                String message = buildNotificationMessage(business.getBusinessName(), oldStatus, newStatus, effectiveDate, reason);

                NotificationLogModel notification = new NotificationLogModel(
                        businessId,
                        owner.getOwnerID(),
                        notificationChannel,
                        Timestamp.valueOf(LocalDateTime.now()),
                        subject,
                        message
                );

                boolean notificationLogged = notificationLogDAO.addNotificationLog(notification);
                if (!notificationLogged) {
                    System.err.println("Warning: Failed to log notification for owner " + owner.getOwnerID());
                }
            }

            String changeSummary = buildAuditChangeSummary(oldStatus, newStatus, effectiveDate, reason, supportDocRef);

            AuditLogModel auditLog = new AuditLogModel(
                    "Business",
                    businessId,
                    "UPDATE",
                    changedByUser,
                    Timestamp.valueOf(LocalDateTime.now()),
                    changeSummary
            );

            boolean auditLogged = auditLogDAO.addAuditLog(auditLog);
            if (!auditLogged) {
                conn.rollback();
                return new StatusUpdateResult(false, "Failed to create audit log");
            }

            conn.commit();
            return new StatusUpdateResult(true,
                    "Business status successfully updated from '" + oldStatus + "' to '" + newStatus + "'. " +
                            "Notifications sent to " + owners.size() + " owner(s).");

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return new StatusUpdateResult(false, "Database error: " + e.getMessage());

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Validates if the status is one of the allowed values
     */
    private boolean isValidStatus(String status) {
        return status.equals("Active") || status.equals("Suspended") ||
                status.equals("Closed") || status.equals("Pending") ||
                status.equals("Revoked") || status.equals("Merged") ||
                status.equals("Retired");
    }

    /**
     * Determines if permit status should cascade based on business status change
     */
    private boolean shouldCascadeToPermits(String newStatus) {
        return newStatus.equals("Suspended") || newStatus.equals("Revoked") ||
                newStatus.equals("Closed") || newStatus.equals("Retired");
    }

    /**
     * Gets the owners associated with a business.
     * Queries business_owner junction table.
     */
    private List<OwnerModel> getOwnersForBusiness(int businessId) {
        List<OwnerModel> owners = new ArrayList<>();
        String query = "SELECT o.* FROM owner o " +
                "INNER JOIN business_owner bo ON o.owner_id = bo.owner_id " +
                "WHERE bo.business_id = ?";

        try (PreparedStatement pstmt = DatabaseConnector.connection.prepareStatement(query)) {
            pstmt.setInt(1, businessId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OwnerModel owner = new OwnerModel(
                            rs.getInt("owner_id"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getString("middle_name"),
                            rs.getString("contact_no"),
                            rs.getString("email"),
                            rs.getString("gov_id_type"),
                            rs.getString("gov_id_no"),
                            rs.getString("tin"),
                            rs.getString("home_address")
                    );
                    owners.add(owner);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving owners for business: " + e.getMessage());
            e.printStackTrace();
        }

        return owners;
    }

    /**
     * Builds the notification message content
     */
    private String buildNotificationMessage(String businessName, String oldStatus,
                                            String newStatus, LocalDate effectiveDate, String reason) {
        return String.format(
                "Business '%s' status has been updated from '%s' to '%s' effective %s. Reason: %s",
                businessName, oldStatus, newStatus, effectiveDate.toString(), reason
        );
    }

    /**
     * Builds JSON summary of changes for audit log
     */
    private String buildAuditChangeSummary(String oldStatus, String newStatus,
                                           LocalDate effectiveDate, String reason, String supportDocRef) {
        return String.format(
                "{\"status\":\"%s -> %s\",\"effectiveDate\":\"%s\",\"reason\":\"%s\",\"supportDocRef\":\"%s\"}",
                oldStatus, newStatus, effectiveDate.toString(), reason, supportDocRef
        );
    }

    /**
     * Inner class to hold the result of the status update operation
     */
    public static class StatusUpdateResult {
        private final boolean success;
        private final String message;

        public StatusUpdateResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
