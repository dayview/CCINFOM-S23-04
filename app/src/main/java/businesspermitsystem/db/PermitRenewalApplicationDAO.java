package businesspermitsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.PermitRenewalApplicationModel;


public class PermitRenewalApplicationDAO {
    
    private  Connection connection;

    public PermitRenewalApplicationDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public PermitRenewalApplicationModel getRenewalApplicationByID(int renewalID) {
        String query = "SELECT * FROM permit_renewal_application WHERE renewal_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, renewalID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new PermitRenewalApplicationModel(
                        result.getInt("renewal_id"),
                        result.getInt("business_id"),
                        result.getInt("previous_permit_id"),
                        result.getDate("application_date"),
                        result.getDouble("renewal_fee"),
                        result.getDouble("surcharge"),
                        result.getDouble("total_amount"),
                        result.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permit renewal application: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<PermitRenewalApplicationModel> getRenewalsByBusinessID(int businessID) {
    ArrayList<PermitRenewalApplicationModel> renewals = new ArrayList<>();
    String query = "SELECT * FROM permit_renewal_application WHERE business_id = ?";
    
    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, businessID);
        ResultSet result = statement.executeQuery();
        
        while (result.next()) {
            renewals.add(new PermitRenewalApplicationModel(
                result.getInt("renewal_id"),
                result.getInt("business_id"),
                result.getInt("previous_permit_id"),
                result.getDate("application_date"),
                result.getDouble("renewal_fee"),
                result.getDouble("surcharge"),
                result.getDouble("total_amount"),
                result.getString("status")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving renewals by business: " + e.getMessage());
        e.printStackTrace();
    }
    return renewals;
}

    public ArrayList<PermitRenewalApplicationModel> getAllPermitRenewalApplication() {
        ArrayList<PermitRenewalApplicationModel> renewalApplications = new ArrayList<>();
        String query = "SELECT * FROM permit_renewal_application";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                PermitRenewalApplicationModel renewalApplication = new PermitRenewalApplicationModel(
                        result.getInt("renewal_id"),
                        result.getInt("business_id"),
                        result.getInt("previous_permit_id"),
                        result.getDate("application_date"),
                        result.getDouble("renewal_fee"),
                        result.getDouble("surcharge"),
                        result.getDouble("total_amount"),
                        result.getString("status")
                );
                renewalApplications.add(renewalApplication);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving permit renewal applications: " + e.getMessage());
            e.printStackTrace();
        }
        return renewalApplications;
    }

    public boolean addRenewalApplication(PermitRenewalApplicationModel renewalApplication) {
        String query = "INSERT INTO permit_renewal_application (business_id, previous_permit_id, application_date, renewal_fee, surcharge, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, renewalApplication.getBusinessID());
            statement.setInt(2, renewalApplication.getPreviousPermitID());
            statement.setDate(3, new java.sql.Date(renewalApplication.getApplicationDate().getTime()));
            statement.setDouble(4, renewalApplication.getRenewalFee());
            statement.setDouble(5, renewalApplication.getSurcharge());
            statement.setDouble(6, renewalApplication.getTotalAmount());
            statement.setString(7, renewalApplication.getStatus());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding permit renewal application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int addRenewalGetID(PermitRenewalApplicationModel renewalApplication) {
        String query = "INSERT INTO permit_renewal_application (business_id, previous_permit_id, application_date, renewal_fee, surcharge, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, renewalApplication.getBusinessID());
            statement.setInt(2, renewalApplication.getPreviousPermitID());
            statement.setDate(3, new java.sql.Date(renewalApplication.getApplicationDate().getTime()));
            statement.setDouble(4, renewalApplication.getRenewalFee());
            statement.setDouble(5, renewalApplication.getSurcharge());
            statement.setDouble(6, renewalApplication.getTotalAmount());
            statement.setString(7, renewalApplication.getStatus());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Error adding permit renewal application: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateRenewalApplication(PermitRenewalApplicationModel renewalApplication) {
        String query = "UPDATE permit_renewal_application SET business_id = ?, previous_permit_id = ?, application_date = ?, renewal_fee = ?, surcharge = ?, total_amount = ?, status = ? WHERE renewal_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, renewalApplication.getBusinessID());
            statement.setInt(2, renewalApplication.getPreviousPermitID());
            statement.setDate(3, new java.sql.Date(renewalApplication.getApplicationDate().getTime()));
            statement.setDouble(4, renewalApplication.getRenewalFee());
            statement.setDouble(5, renewalApplication.getSurcharge());
            statement.setDouble(6, renewalApplication.getTotalAmount());
            statement.setString(7, renewalApplication.getStatus());
            statement.setInt(8, renewalApplication.getRenewalID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating permit renewal application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRenewalApplication(int renewalID) {
        String query = "DELETE FROM permit_renewal_application WHERE renewal_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, renewalID);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting permit renewal application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
