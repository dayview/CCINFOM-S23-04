package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.OwnerModel;

public class OwnerDAO {
    public OwnerModel getOwnerByID(int ownerID) {
        String query = "SELECT * FROM owner WHERE owner_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, ownerID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new OwnerModel(
                        result.getInt("ownerID"),
                        result.getString("lastName"),
                        result.getString("firstName"),
                        result.getString("middleName"),
                        result.getString("contactNo"),
                        result.getString("email"),
                        result.getString("govID_type"),
                        result.getString("govID_no"),
                        result.getString("tin"),
                        result.getString("homeAddress")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving owner: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<OwnerModel> getAllOwners() {
        ArrayList<OwnerModel> owners = new ArrayList<>();
        String query = "SELECT * FROM owner";

        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                OwnerModel owner = new OwnerModel(
                    result.getInt("ownerID"),
                    result.getString("lastName"),
                    result.getString("firstName"),
                    result.getString("middleName"),
                    result.getString("contactNo"),
                    result.getString("email"),
                    result.getString("govID_type"),
                    result.getString("govID_no"),                        result.getString("tin"),
                    result.getString("homeAddress")
                );
                owners.add(owner);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving owners: " + e.getMessage());
            e.printStackTrace();
        }
        return owners;
    }

    public boolean addOwner(OwnerModel owner) {
        String query = "INSERT INTO owner (last_name, first_name, middle_name, contact_no, email, gov_id_type, gov_id_no, tin, home_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setString(1, owner.getLastName());
            statement.setString(2, owner.getFirstName());
            statement.setString(3, owner.getMiddleName());
            statement.setString(4, owner.getContactNo());
            statement.setString(5, owner.getEmail());
            statement.setString(6, owner.getGovID_type());
            statement.setString(7, owner.getGovID_no());
            statement.setString(8, owner.getTin());
            statement.setString(9, owner.getHomeAddress());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOwner(OwnerModel owner) {
        String query = "UPDATE owner SET last_name = ?, first_name = ?, middle_name = ?, contact_no = ?, email = ?, gov_id_type = ?, gov_id_no = ?, tin = ?, home_address = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            
            statement.setString(1, owner.getLastName());
            statement.setString(2, owner.getFirstName());
            statement.setString(3, owner.getMiddleName());
            statement.setString(4, owner.getContactNo());
            statement.setString(5, owner.getEmail());
            statement.setString(6, owner.getGovID_type());
            statement.setString(7, owner.getGovID_no());
            statement.setString(8, owner.getTin());
            statement.setString(9, owner.getHomeAddress());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOwner(int ownerID) {
        String query = "DELETE FROM owner WHERE owner_id = ?";

        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, ownerID);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting owner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}