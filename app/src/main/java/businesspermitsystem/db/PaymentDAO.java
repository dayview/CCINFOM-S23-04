package businesspermitsystem.db;

import businesspermitsystem.models.PaymentModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    private final Connection connection;

    public PaymentDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established.");
        }
    }

    // INSERT payment
    public boolean addPayment(PaymentModel payment) {

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
    public PaymentModel getPaymentById(int paymentId) {
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
    public List<PaymentModel> getPaymentsByApplicationId(int applicationId) {

        List<PaymentModel> list = new ArrayList<>();
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
    private PaymentModel extractPayment(ResultSet rs) throws SQLException {
        PaymentModel payment = new PaymentModel();

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
}
