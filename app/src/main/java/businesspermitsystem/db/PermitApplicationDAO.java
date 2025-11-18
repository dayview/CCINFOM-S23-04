package businesspermitsystem.db;

import businesspermitsystem.models.PermitApplicationModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DAO for the permit_application table.
 * Handles creation, retrieval, updating, and deletion of permit applications.
 */
public class PermitApplicationDAO {

    private final Connection connection;

    public PermitApplicationDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established. Run DatabaseConnector.getConnection().");
        }
    }

    public boolean addPermitApplication(PermitApplicationModel app) {
        String sql = """
            INSERT INTO permit_application
            (business_id, permit_type_id, application_date, approval_date, expiration_date,
             status, base_fee, surcharge, total_fee, remarks)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, app.getBusinessId());
            stmt.setInt(2, app.getPermitTypeId());

            stmt.setDate(3, Date.valueOf(app.getApplicationDate()));
            stmt.setDate(4, app.getApprovalDate() != null ? Date.valueOf(app.getApprovalDate()) : null);
            stmt.setDate(5, app.getExpirationDate() != null ? Date.valueOf(app.getExpirationDate()) : null);

            stmt.setString(6, app.getStatus());
            stmt.setBigDecimal(7, app.getBaseFee());
            stmt.setBigDecimal(8, app.getSurcharge());
            stmt.setBigDecimal(9, app.getTotalFee());
            stmt.setString(10, app.getRemarks());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting permit application: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public PermitApplicationModel getApplicationById(int id) {
        String sql = "SELECT * FROM permit_application WHERE application_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractApplication(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving permit application: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<PermitApplicationModel> getApplicationsByBusinessId(int businessId) {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE business_id = ? ORDER BY application_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, businessId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractApplication(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving business applications: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public List<PermitApplicationModel> getApplicationsByStatus(String status) {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = "SELECT * FROM permit_application WHERE status = ? ORDER BY application_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractApplication(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving applications by status: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    public boolean updatePermitApplication(PermitApplicationModel app) {
        String sql = """
            UPDATE permit_application
            SET business_id = ?, permit_type_id = ?, application_date = ?, approval_date = ?,
                expiration_date = ?, status = ?, base_fee = ?, surcharge = ?, total_fee = ?, remarks = ?
            WHERE application_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, app.getBusinessId());
            stmt.setInt(2, app.getPermitTypeId());

            stmt.setDate(3, Date.valueOf(app.getApplicationDate()));
            stmt.setDate(4, app.getApprovalDate() != null ? Date.valueOf(app.getApprovalDate()) : null);
            stmt.setDate(5, app.getExpirationDate() != null ? Date.valueOf(app.getExpirationDate()) : null);

            stmt.setString(6, app.getStatus());
            stmt.setBigDecimal(7, app.getBaseFee());
            stmt.setBigDecimal(8, app.getSurcharge());
            stmt.setBigDecimal(9, app.getTotalFee());
            stmt.setString(10, app.getRemarks());
            stmt.setInt(11, app.getApplicationId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating permit application: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deletePermitApplication(int id) {
        String sql = "DELETE FROM permit_application WHERE application_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting permit application: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private PermitApplicationModel extractApplication(ResultSet rs) throws SQLException {
        PermitApplicationModel app = new PermitApplicationModel();

        app.setApplicationId(rs.getInt("application_id"));
        app.setBusinessId(rs.getInt("business_id"));
        app.setPermitTypeId(rs.getInt("permit_type_id"));

        app.setApplicationDate(rs.getDate("application_date").toLocalDate());

        Date approval = rs.getDate("approval_date");
        if (approval != null) app.setApprovalDate(approval.toLocalDate());

        Date expiration = rs.getDate("expiration_date");
        if (expiration != null) app.setExpirationDate(expiration.toLocalDate());

        app.setStatus(rs.getString("status"));
        app.setBaseFee(rs.getBigDecimal("base_fee"));
        app.setSurcharge(rs.getBigDecimal("surcharge"));
        app.setTotalFee(rs.getBigDecimal("total_fee"));
        app.setRemarks(rs.getString("remarks"));

        return app;
    }
}
