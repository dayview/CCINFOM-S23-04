package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import businesspermitsystem.models.InspectorModel;

/**
 * Data Access Object (DAO) for the {@link InspectorModel}.
 * 
 * This class handles all database interactions related to inspectors —
 * including creating, reading, updating, and deleting records.
 *
 * It uses {@link DatabaseConnector} to establish connections.
 */
public class InspectorDAO {

    public void addInspector(InspectorModel inspector) throws SQLException {
        String query = "INSERT INTO inspector (last_name, first_name, designation, license_no, active, office_location_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);
        
        statement.setString(1, inspector.getLastName());
        statement.setString(2, inspector.getFirstName());
        statement.setString(3, inspector.getDesignation());
        statement.setString(4, inspector.getLicenseNumber());
        statement.setInt(5, (inspector.isActive()) ? 1 : 0);
        statement.setString(6, inspector.getMunicipalityID());

        statement.executeUpdate();
    }
}
