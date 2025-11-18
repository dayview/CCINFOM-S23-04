package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; // Need this for the isAvailable method
import java.util.ArrayList;
import java.util.List; // Using List interface for cleaner method signature

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
        String query = "INSERT INTO inspector (last_name, first_name, middle_name, designation, license_number, active, municipality_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
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
     * Helper method to map a ResultSet row to an InspectorModel object.
     * @param result The ResultSet positioned at the current row.
     * @return A fully populated InspectorModel.
     * @throws SQLException
     */
    private InspectorModel mapResultSetToInspector(ResultSet result) throws SQLException {
        return new InspectorModel(
            result.getInt("inspector_id"),
            result.getString("last_name"),
            result.getString("first_name"),
            result.getString("middle_name"),
            result.getString("designation"),
            result.getString("license_number"), 
            result.getBoolean("active"),
            result.getInt("municipality_id") 
        );
    }

    /**
     * Returns the list of all inspectors in the database.
     * * @return ArrayList<InspectorModel> that represents all the inspectors
     * @throws SQLException
     */
    public ArrayList<InspectorModel> getInspectors() throws SQLException {
        ArrayList<InspectorModel> inspectors = new ArrayList<>(); // Use diamond operator
        
        String query = "SELECT inspector_id, last_name, first_name, middle_name, designation, license_number, active, municipality_id FROM inspector";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                inspectors.add(mapResultSetToInspector(result));
            }
        }
        return inspectors;
    }
    
    /**
     * Retrieves an Inspector record by its ID.
     * @param id The primary key (inspector_id).
     * @return The InspectorModel if found, null otherwise.
     * @throws SQLException
     */
    public InspectorModel getInspectorByID(int id) throws SQLException {
        String query = "SELECT * FROM inspector WHERE inspector_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapResultSetToInspector(result);
                }
            }
        }
        return null;
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
        String query = "UPDATE inspector SET last_name=?, first_name=?, middle_name=?, designation=?, license_number=?, active=?, municipality_id=? WHERE inspector_id=?";
        
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

    /**
     * Retrieves all active inspectors assigned to a specific municipality.
     * This enforces the rule that inspectors only handle local businesses (JURISDICTION CHECK).
     * * @param municipalityID The ID of the municipality (office_location_id).
     * @return A list of eligible, active inspectors.
     * @throws SQLException
     */
    public List<InspectorModel> getInspectorsByMunicipality(int municipalityID) throws SQLException {
        List<InspectorModel> inspectors = new ArrayList<>();
        // Query filters by municipality_id (jurisdiction) and active status
        String query = "SELECT * FROM inspector WHERE municipality_id = ? AND active = 1";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, municipalityID);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    inspectors.add(mapResultSetToInspector(result));
                }
            }
        }
        return inspectors;
    }

    /**
     * Checks if a specific inspector is already scheduled for an inspection on a given date (AVAILABILITY CHECK).
     * * @param inspectorID The ID of the inspector to check.
     * @param date The date to check for conflicts.
     * @return true if the inspector is free, false if they are already scheduled.
     * @throws SQLException
     */
    public boolean isAvailable(int inspectorID, LocalDate date) throws SQLException {
        // Query counts existing schedules for the given inspector on the given date.
        String query = "SELECT COUNT(*) FROM inspection_schedule WHERE inspector_id = ? AND inspection_date = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, inspectorID);
            statement.setDate(2, java.sql.Date.valueOf(date));
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    // If count > 0, they are busy (NOT available).
                    return result.getInt(1) == 0; 
                }
            }
        }
        // Safest default is to assume they are available if a database error occurs.
        return true; 
    }
}