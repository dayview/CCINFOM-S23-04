package businesspermitsystem.db;

import businesspermitsystem.models.InitialPaymentModel;
import businesspermitsystem.models.PermitApplicationModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InitialPaymentDAO {

    private final Connection connection;

    public InitialPaymentDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established.");
        }
    }

    // INSERT payment
    public boolean addPayment(InitialPaymentModel payment) {

        String sql = """
            INSERT INTO payment (application_id, business_id, permit_type_id, municipality_id, payment_date, amount_paid, mode_of_payment, or_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, payment.getApplicationId());
            stmt.setInt(2, payment.getBusinessId());
            stmt.setInt(3, payment.getPermitTypeId());
            stmt.setInt(4, payment.getMunicipalityId());
            stmt.setDate(5, Date.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(6, payment.getAmountPaid());
            stmt.setString(7, payment.getModeOfPayment());
            stmt.setString(8, payment.getOrNumber());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // GET payment by ID
    public InitialPaymentModel getPaymentById(int paymentId) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPayment(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // GET all payments for an application
    public List<InitialPaymentModel> getPaymentsByApplicationId(int applicationId) {

        List<InitialPaymentModel> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE application_id = ? ORDER BY payment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPayment(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // DELETE payment
    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Helper method to build model from ResultSet
    private InitialPaymentModel extractPayment(ResultSet rs) throws SQLException {
        InitialPaymentModel payment = new InitialPaymentModel();

        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setApplicationId(rs.getInt("application_id"));
        payment.setBusinessId(rs.getInt("business_id"));
        payment.setPermitTypeId(rs.getInt("permit_type_id"));
        payment.setMunicipalityId(rs.getInt("municipality_id"));
        payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        payment.setAmountPaid(rs.getBigDecimal("amount_paid"));
        payment.setModeOfPayment(rs.getString("mode_of_payment"));
        payment.setOrNumber(rs.getString("or_number"));

        return payment;
    }

    public List<PermitApplicationModel> getApplicationsForPayment() {
        List<PermitApplicationModel> list = new ArrayList<>();

        String sql = """
        SELECT pa.application_id,
               pa.business_id,
               pa.permit_type_id,
               pa.application_date,
               pa.approval_date,
               pa.expiration_date,
               pa.status,
               pa.base_fee,
               pa.surcharge,
               pa.total_fee,
               pa.remarks,
               pt.permit_name,
               fs.base_fee AS fs_base_fee,
               fs.surcharge_rule
        FROM permit_application pa
        JOIN permit_type pt ON pa.permit_type_id = pt.permit_type_id
        JOIN fee_schedule fs ON pt.fee_schedule_id = fs.fee_schedule_id
        WHERE pa.status = 'For Payment'
    """;

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

                // Extra fields for UI display
                app.setPermitName(rs.getString("permit_name"));

                list.add(app);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


}
