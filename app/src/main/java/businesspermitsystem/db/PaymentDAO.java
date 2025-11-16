package businesspermitsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.PaymentModel;

public class PaymentDAO {
    
    private  Connection connection;

    public PaymentDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public PaymentModel getPaymentByID(int paymentID) {
        String query = "SELECT * FROM payment WHERE payment_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, paymentID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new PaymentModel(
                        result.getInt("payment_id"),
                        result.getInt("renewal_id"),
                        result.getDouble("amount"),
                        result.getString("method"),
                        result.getDate("payment_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public PaymentModel getPaymentByRenewal(int renewalID) {
        String query = "SELECT * FROM payment WHERE renewal_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, renewalID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new PaymentModel(
                        result.getInt("payment_id"),
                        result.getInt("renewal_id"),
                        result.getDouble("amount"),
                        result.getString("method"),
                        result.getDate("payment_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<PaymentModel> getAllPayment() {
        ArrayList<PaymentModel> payments = new ArrayList<>();
        String query = "SELECT * FROM payment";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                PaymentModel payment = new PaymentModel(
                        result.getInt("payment_id"),
                        result.getInt("renewal_id"),
                        result.getDouble("amount"),
                        result.getString("method"),
                        result.getDate("payment_date")
                );
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public boolean addPayment(PaymentModel payment) {
        String query = "INSERT INTO payment (renewal_id, amount, method, payment_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, payment.getRenewalID());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getMethod());
            statement.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime()));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePayment(PaymentModel payment) {
        String query = "UPDATE payment SET renewal_id = ?, amount = ?, method = ?, payment_date = ? WHERE payment_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, payment.getRenewalID());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getMethod());
            statement.setDate(4, new java.sql.Date(payment.getPaymentDate().getTime()));
            statement.setInt(5, payment.getPaymentID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePayment(int paymentID) {
        String query = "DELETE FROM payment WHERE payment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, paymentID);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
}
