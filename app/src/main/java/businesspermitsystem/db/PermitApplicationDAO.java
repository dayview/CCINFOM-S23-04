package businesspermitsystem.db;

import businesspermitsystem.controllers.InspectorScheduleController;
import businesspermitsystem.models.PermitApplicationModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PermitApplicationDAO {

    private final Connection connection;

    public PermitApplicationDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established. Run DatabaseConnector.getConnection().");
        }
    }

    // INSERT NEW APPLICATION
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

    // GET SINGLE APPLICATION
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

    // GET BUSINESS APPLICATIONS
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

    // GET BY STATUS
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

    // **FIXED UPDATE — MATCHES NEW SCHEMA**
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

    // DELETE
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

    // MAP RESULTSET → MODEL
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

    public List<PermitApplicationModel> getApplicationsForPayment() {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE status = 'For Payment'";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PermitApplicationModel app = new PermitApplicationModel();

                app.setApplicationId(rs.getInt("application_id"));
                app.setBusinessId(rs.getInt("business_id"));
                app.setPermitTypeId(rs.getInt("permit_type_id"));
                app.setApplicationDate(rs.getDate("application_date").toLocalDate());

                if (rs.getDate("approval_date") != null)
                    app.setApprovalDate(rs.getDate("approval_date").toLocalDate());

                if (rs.getDate("expiration_date") != null)
                    app.setExpirationDate(rs.getDate("expiration_date").toLocalDate());

                app.setStatus(rs.getString("status"));
                app.setBaseFee(rs.getBigDecimal("base_fee"));
                app.setSurcharge(rs.getBigDecimal("surcharge"));
                app.setTotalFee(rs.getBigDecimal("total_fee"));
                app.setRemarks(rs.getString("remarks"));

                list.add(app);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Returns a map of application_id -> business_name
     * for all applications that are already paid and ready for inspection.
     *
     * This avoids modifying the PermitApplicationModel.
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
                int appId = rs.getInt("application_id");
                int bizId = rs.getInt("business_id");
                int muniId = rs.getInt("municipality_id");
                String businessName = rs.getString("business_name");

                list.add(new InspectorScheduleController.ApplicationEntry(
                        appId, bizId, muniId, businessName
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching paid applications for scheduling: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }


}
