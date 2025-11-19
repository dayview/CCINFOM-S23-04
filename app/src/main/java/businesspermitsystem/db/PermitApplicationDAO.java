package businesspermitsystem.db;

import businesspermitsystem.controllers.InspectorScheduleController;
import businesspermitsystem.models.PermitApplicationModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Permit Application records
 * in the Business Permit System. Provides CRUD operations as well as
 * specialized queries for payment processing and inspector scheduling.
 */
public class PermitApplicationDAO {

    /** Active database connection retrieved from DatabaseConnector. */
    private final Connection connection;

    /**
     * Initializes the DAO and validates that a DB connection exists.
     */
    public PermitApplicationDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established. Run DatabaseConnector.getConnection().");
        }
    }

    /**
     * Inserts a new permit application record into the database.
     *
     * @param app the permit application model containing the record details
     * @return generated application ID if inserted successfully, otherwise -1
     */
    public int addPermitApplication(PermitApplicationModel app) {
        String sql = """
            INSERT INTO permit_application 
            (business_id, permit_type_id, application_date, approval_date, expiration_date, status, 
             base_fee, surcharge, total_fee, remarks)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, app.getBusinessId());
            preparedStatement.setInt(2, app.getPermitTypeId());
            preparedStatement.setDate(3, Date.valueOf(app.getApplicationDate()));
            preparedStatement.setDate(4, app.getApprovalDate() != null ? Date.valueOf(app.getApprovalDate()) : null);
            preparedStatement.setDate(5, app.getExpirationDate() != null ? Date.valueOf(app.getExpirationDate()) : null);
            preparedStatement.setString(6, app.getStatus());
            preparedStatement.setBigDecimal(7, app.getBaseFee());
            preparedStatement.setBigDecimal(8, app.getSurcharge());
            preparedStatement.setBigDecimal(9, app.getTotalFee());
            preparedStatement.setString(10, app.getRemarks());

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting permit application: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Retrieves a specific permit application by ID.
     *
     * @param id application ID
     * @return matching PermitApplicationModel, or null if not found
     */
    public PermitApplicationModel getApplicationById(int id) {
        String sql = "SELECT * FROM permit_application WHERE application_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) return extractApplication(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving permit application: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all permit applications associated with a specific business.
     *
     * @param businessId the business ID
     * @return list of permit applications belonging to the business
     */
    public List<PermitApplicationModel> getApplicationsByBusinessId(int businessId) {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE business_id = ? ORDER BY application_date DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, businessId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) list.add(extractApplication(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving business applications: " + e.getMessage());
        }

        return list;
    }

    /**
     * Retrieves permit applications by a specific status value.
     *
     * @param status application status (e.g., "Paid", "Pending")
     * @return list of applications matching the status
     */
    public List<PermitApplicationModel> getApplicationsByStatus(String status) {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE status = ? ORDER BY application_date DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) list.add(extractApplication(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving by status: " + e.getMessage());
        }

        return list;
    }

    /**
     * Updates an existing permit application to the new schema format.
     *
     * @param app updated permit application model
     * @return true if updated successfully, false otherwise
     */
    public boolean updatePermitApplication(PermitApplicationModel app) {
        String sql = """
            UPDATE permit_application
            SET business_id = ?, 
                permit_type_id = ?, 
                application_date = ?, 
                approval_date = ?, 
                expiration_date = ?, 
                status = ?, 
                base_fee = ?, 
                surcharge = ?, 
                total_fee = ?, 
                remarks = ?
            WHERE application_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, app.getBusinessId());
            preparedStatement.setInt(2, app.getPermitTypeId());
            preparedStatement.setDate(3, Date.valueOf(app.getApplicationDate()));
            preparedStatement.setDate(4, app.getApprovalDate() != null ? Date.valueOf(app.getApprovalDate()) : null);
            preparedStatement.setDate(5, app.getExpirationDate() != null ? Date.valueOf(app.getExpirationDate()) : null);
            preparedStatement.setString(6, app.getStatus());
            preparedStatement.setBigDecimal(7, app.getBaseFee());
            preparedStatement.setBigDecimal(8, app.getSurcharge());
            preparedStatement.setBigDecimal(9, app.getTotalFee());
            preparedStatement.setString(10, app.getRemarks());
            preparedStatement.setInt(11, app.getApplicationId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating permit application: " + e.getMessage());
        }

        return false;
    }

    /**
     * Deletes a permit application from the database.
     *
     * @param id application ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deletePermitApplication(int id) {
        String sql = "DELETE FROM permit_application WHERE application_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting application: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all applications with status 'For Payment' for the payment screen.
     *
     * @return list of applications waiting for payment
     */
    public List<PermitApplicationModel> getApplicationsForPayment() {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE status = 'For Payment'";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(extractApplication(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Retrieves all paid applications that have NOT been scheduled yet for inspection.
     * Excludes businesses that already have an inspection schedule entry.
     *
     * @return a list of ApplicationEntry objects containing application + business info
     */
    public List<InspectorScheduleController.ApplicationEntry> getPaidApplicationsForScheduling() {

        List<InspectorScheduleController.ApplicationEntry> list = new ArrayList<>();

        String sql = """
        SELECT pa.application_id,
               pa.business_id,
               b.municipality_id,
               b.business_name
        FROM permit_application pa
        JOIN business b ON pa.business_id = b.business_id
        WHERE pa.status = 'Paid'
          AND pa.business_id NOT IN (
                SELECT business_id
                FROM inspection_schedule
          )
        ORDER BY pa.application_id ASC
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new InspectorScheduleController.ApplicationEntry(
                        rs.getInt("application_id"),
                        rs.getInt("business_id"),
                        rs.getInt("municipality_id"),
                        rs.getString("business_name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching paid applications for scheduling: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Extracts a PermitApplicationModel from a result set.
     *
     * @param rs ResultSet from SQL query
     * @return constructed PermitApplicationModel
     * @throws SQLException if column access is invalid
     */
    private PermitApplicationModel extractApplication(ResultSet rs) throws SQLException {
        PermitApplicationModel app = new PermitApplicationModel();

        app.setApplicationId(rs.getInt("application_id"));
        app.setBusinessId(rs.getInt("business_id"));
        app.setPermitTypeId(rs.getInt("permit_type_id"));
        app.setApplicationDate(rs.getDate("application_date").toLocalDate());

        Date approval = rs.getDate("approval_date");
        if (approval != null) app.setApprovalDate(approval.toLocalDate());

        Date exp = rs.getDate("expiration_date");
        if (exp != null) app.setExpirationDate(exp.toLocalDate());

        app.setStatus(rs.getString("status"));
        app.setBaseFee(rs.getBigDecimal("base_fee"));
        app.setSurcharge(rs.getBigDecimal("surcharge"));
        app.setTotalFee(rs.getBigDecimal("total_fee"));
        app.setRemarks(rs.getString("remarks"));

        return app;
    }
}
