package businesspermitsystem.db;

import businesspermitsystem.models.PaymentModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payment entity.
 * Handles database operations for payment records.
 */
public class PaymentDAO {
    private Connection connection;

    public PaymentDAO() {
        this.connection = DatabaseConnector.connection;
        if (this.connection == null) {
            System.err.println("Warning: Database connection not established.");
        }
    }

    /**
     * Adds a new payment record and returns the generated payment ID.
     * 
     * @param payment the payment model to insert
     * @return the generated payment ID, or -1 if failed
     */
    public int addPaymentGetID(PaymentModel payment) {
        String query = "INSERT INTO payment (renewal_id, amount, payment_method, payment_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, payment.getRenewalId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setTimestamp(4, new Timestamp(payment.getPaymentDate().getTime()));
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves a payment by its ID.
     * 
     * @param paymentId the payment ID
     * @return the payment model or null if not found
     */
    public PaymentModel getPaymentByID(int paymentId) {
        String query = "SELECT * FROM payment WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PaymentModel(
                        rs.getInt("payment_id"),
                        rs.getInt("renewal_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all payments for a specific renewal.
     * 
     * @param renewalId the renewal ID
     * @return list of payments for the renewal
     */
    public List<PaymentModel> getPaymentsByRenewalID(int renewalId) {
        List<PaymentModel> payments = new ArrayList<>();
        String query = "SELECT * FROM payment WHERE renewal_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, renewalId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaymentModel payment = new PaymentModel(
                        rs.getInt("payment_id"),
                        rs.getInt("renewal_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_date")
                    );
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    /**
     * Retrieves all payment records.
     * 
     * @return list of all payments
     */
    public List<PaymentModel> getAllPayments() {
        List<PaymentModel> payments = new ArrayList<>();
        String query = "SELECT * FROM payment ORDER BY payment_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PaymentModel payment = new PaymentModel(
                    rs.getInt("payment_id"),
                    rs.getInt("renewal_id"),
                    rs.getDouble("amount"),
                    rs.getString("payment_method"),
                    rs.getTimestamp("payment_date")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    /**
     * Updates an existing payment record.
     * 
     * @param payment the payment model with updated data
     * @return true if successful, false otherwise
     */
    public boolean updatePayment(PaymentModel payment) {
        String query = "UPDATE payment SET renewal_id = ?, amount = ?, payment_method = ?, payment_date = ? WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, payment.getRenewalId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setTimestamp(4, new Timestamp(payment.getPaymentDate().getTime()));
            pstmt.setInt(5, payment.getPaymentId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a payment record.
     * 
     * @param paymentId the payment ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deletePayment(int paymentId) {
        String query = "DELETE FROM payment WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, paymentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}