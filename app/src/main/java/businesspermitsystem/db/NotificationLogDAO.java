package businesspermitsystem.db;

import businesspermitsystem.models.NotificationLogModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationLogDAO {
    private Connection connection;

    /**
     * Constructor - initializes database connection
     */
    public NotificationLogDAO() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    /**
     * Adds a new notification log entry to the database.
     * Used to record all notifications sent to owners.
     *
     * @param notification the NotificationLogModel to insert
     * @return true if successful, false otherwise
     */
    public boolean addNotificationLog(NotificationLogModel notification) {
        String query = "INSERT INTO notification_log (business_id, owner_id, channel, sent_date_time, subject, message_preview) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, notification.getBusinessId());
            pstmt.setInt(2, notification.getOwnerId());
            pstmt.setString(3, notification.getChannel());
            pstmt.setTimestamp(4, notification.getSentDateTime());
            pstmt.setString(5, notification.getSubject());
            pstmt.setString(6, notification.getMessagePreview());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding notification log: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all notification sent to a specific owner.
     *
     * @param ownerId the owner's unique identifier
     * @return List of NotificationLogModel objects
     */
    public List<NotificationLogModel> getNotificationLogsByOwnerId(int ownerId) {
        List<NotificationLogModel> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_log WHERE owner_id = ? ORDER BY sent_date_time DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, ownerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NotificationLogModel notification = new NotificationLogModel(
                            rs.getInt("notice_id"),
                            rs.getInt("business_id"),
                            rs.getInt("owner_id"),
                            rs.getString("channel"),
                            rs.getTimestamp("sent_date_time"),
                            rs.getString("subject"),
                            rs.getString("message_preview")
                    );
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications by owner: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Retrieves all notifications for a specific business.
     *
     * @param businessId the business's unique identifier
     * @return List of NotificationLogModel objects
     */
    public List<NotificationLogModel> getNotificationLogsByBusinessId(int businessId) {
        List<NotificationLogModel> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_log WHERE business_id = ? ORDER BY sent_date_time DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, businessId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NotificationLogModel notification = new NotificationLogModel(
                            rs.getInt("notice_id"),
                            rs.getInt("business_id"),
                            rs.getInt("owner_id"),
                            rs.getString("channel"),
                            rs.getTimestamp("sent_date_time"),
                            rs.getString("subject"),
                            rs.getString("message_preview")
                    );
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications by business: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Retrieves all notifications sent via a specific channel (SMS or email).
     *
     * @param channel the communication channel ("SMS" or "email")
     * @return List of NotificationLogModel objects
     */
    public List<NotificationLogModel> getNotificationLogsByChannel(String channel) {
        List<NotificationLogModel> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_log WHERE channel = ? ORDER BY sent_date_time DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, channel);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NotificationLogModel notification = new NotificationLogModel(
                            rs.getInt("notice_id"),
                            rs.getInt("business_id"),
                            rs.getInt("owner_id"),
                            rs.getString("channel"),
                            rs.getTimestamp("sent_date_time"),
                            rs.getString("subject"),
                            rs.getString("message_preview")
                    );
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications by channel: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Retrieves all notifications within a date range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return List of NotificationLogModel objects
     */
    public List<NotificationLogModel> getNotificationLogsByDateRange(Timestamp startDate, Timestamp endDate) {
        List<NotificationLogModel> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_log WHERE sent_date_time BETWEEN ? AND ? ORDER BY sent_date_time DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, startDate);
            pstmt.setTimestamp(2, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NotificationLogModel notification = new NotificationLogModel(
                            rs.getInt("notice_id"),
                            rs.getInt("business_id"),
                            rs.getInt("owner_id"),
                            rs.getString("channel"),
                            rs.getTimestamp("sent_date_time"),
                            rs.getString("subject"),
                            rs.getString("message_preview")
                    );
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications by date range: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Retrieves all notifications logs
     *
     * @return List of all NotificationLogModel objects
     */
    public List<NotificationLogModel> getAllNotificationLogs() {
        List<NotificationLogModel> notifications = new ArrayList<>();
        String query = "SELECT * FROM notification_log ORDER BY sent_date_time DESC LIMIT 1000";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                NotificationLogModel notification = new NotificationLogModel(
                        rs.getInt("notice_id"),
                        rs.getInt("business_id"),
                        rs.getInt("owner_id"),
                        rs.getString("channel"),
                        rs.getTimestamp("sent_date_time"),
                        rs.getString("subject"),
                        rs.getString("message_preview")
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Retrieves a single notification log by its ID
     *
     * @param noticeId the unique notification ID
     * @return NotificationLogModel or null if not found
     */
    public NotificationLogModel getNotificationLogById(int noticeId) {
        String query = "SELECT * FROM notification_log WHERE notice_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, noticeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new NotificationLogModel(
                            rs.getInt("notice_id"),
                            rs.getInt("business_id"),
                            rs.getInt("owner_id"),
                            rs.getString("channel"),
                            rs.getTimestamp("sent_date_time"),
                            rs.getString("subject"),
                            rs.getString("message_preview")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notification: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
