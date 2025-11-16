package businesspermitsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
/*
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new PermitRenewalApplicationModel(
                        result.getInt("renewal_id"),
                        result.getInt("business_id"),
                        result.getInt("permit_id"),
                        result.getString("middle_name"),
                        result.getString("contact_no"),
                        result.getString("email"),
                        result.getString("gov_id_type"),
                        result.getString("gov_id_no"),
                        result.getString("tin"),
                        result.getString("home_address")
                    );
                }
            }

*/
        } catch (SQLException e) {
            System.err.println("Error retrieving permit renewal application: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
/*
    public ArrayList<PermitRenewalApplicationModel> getAllPermitRenewalApplication() {
        ArrayList<PermitRenewalApplicationModel> renewalApplications = new ArrayList<>();
        String query = "SELECT * FROM permit_renewal_application";

        return 
    }
*/

    
}
