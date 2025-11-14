package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.InspectorModel;

/**
 * Data Access Object (DAO) for the {@link InspectorModel}.
 * * This class handles all database interactions related to inspectors —
 * including creating, reading, updating, and deleting records.
 *
 * It uses {@link DatabaseConnector} to establish connections.
 */
public class InspectorDAO {

    /**
     * Adds a new Inspector into the database
     * * @param inspector is the model being inserted into the database
     * @throws SQLException
     */
    public void addInspector(InspectorModel inspector) throws SQLException {
        String query = "INSERT INTO inspector (last_name, first_name, middle_name, designation, license_number, active, office_location_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            
            statement.setString(1, inspector.getLastName());
            statement.setString(2, inspector.getFirstName());
            statement.setString(3, inspector.getMiddleName()); 
            statement.setString(4, inspector.getDesignation());
            statement.setString(5, inspector.getLicenseNumber());
            statement.setInt(6, (inspector.isActive()) ? 1 : 0);
            statement.setInt(7, inspector.getMunicipalityID());

            statement.executeUpdate();
        }
    }

    /**
     * Returns the list of inspectors in the database
     * * @return ArrayList<InspectorModel> that represents all the inspectors
     * @throws SQLException
     */
    public ArrayList<InspectorModel> getInspectors() throws SQLException {
        ArrayList<InspectorModel> inspectors = new ArrayList<InspectorModel>();

        
        String query = "SELECT inspector_id, last_name, first_name, middle_name, designation, license_number, active, office_location_id FROM inspector";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                InspectorModel inspector = new InspectorModel(
                    result.getInt("inspector_id"),
                    result.getString("last_name"),
                    result.getString("first_name"),
                    result.getString("middle_name"),
                    result.getString("designation"),
                    result.getString("license_number"), 
                    result.getBoolean("active"),
                    result.getInt("office_location_id") 
                );
                inspectors.add(inspector);
            }
        }
        return inspectors;
    }

    /**
     * Deletes an Inspector record from the database using their ID.
     * * @param id The primary key (inspector_id) of the inspector to delete.
     * @throws SQLException
     */
    public void deleteInspector(int id) throws SQLException {
        String query = "DELETE FROM inspector WHERE inspector_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
    
    /**
     * Updates an existing Inspector record in the database.
     * * @param inspector The InspectorModel object containing the updated data.
     * @throws SQLException
     */
    public void updateInspector(InspectorModel inspector) throws SQLException {
        String query = "UPDATE inspector SET last_name=?, first_name=?, middle_name=?, designation=?, license_number=?, active=?, office_location_id=? WHERE inspector_id=?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            
            statement.setString(1, inspector.getLastName());
            statement.setString(2, inspector.getFirstName());
            statement.setString(3, inspector.getMiddleName());
            statement.setString(4, inspector.getDesignation());
            statement.setString(5, inspector.getLicenseNumber());
            statement.setInt(6, (inspector.isActive()) ? 1 : 0);
            statement.setInt(7, inspector.getMunicipalityID());
            statement.setInt(8, inspector.getInspectorID()); 
            
            statement.executeUpdate();
        }
    }
}