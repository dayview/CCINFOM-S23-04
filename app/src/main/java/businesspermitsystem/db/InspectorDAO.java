package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    /**
     * Adds a new Inspector into the database
     * 
     * @param inspector is the model being inserted into the database
     * @throws SQLException
     */
    public void addInspector(InspectorModel inspector) throws SQLException {
        String query = "INSERT INTO inspector (last_name, first_name, designation, license_no, active, office_location_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);
        
        statement.setString(1, inspector.getLastName());
        statement.setString(2, inspector.getFirstName());
        statement.setString(3, inspector.getDesignation());
        statement.setString(4, inspector.getLicenseNumber());
        statement.setInt(5, (inspector.isActive()) ? 1 : 0);
        statement.setInt(6, inspector.getMunicipalityID());

        statement.executeUpdate();
    }

    /**
     * Returns the list of inspectors in the database
     * 
     * @return ArrayList<InspectorModel> that represents all the inspectors
     * @throws SQLException
     */
    public ArrayList<InspectorModel> getInspectors() throws SQLException {
        ArrayList<InspectorModel> inspectors = new ArrayList<InspectorModel>();

        String query = "SELECT * FROM inspector";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            InspectorModel inspector = new InspectorModel(
                result.getInt("inspector_id"),
                result.getString("last_name"),
                result.getString("first_name"),
                result.getString("middle_name"),
                result.getString("designation"),
                result.getString("licenseNumber"),
                result.getBoolean("active"),
                result.getInt("municipality_id")
            );
            inspectors.add(inspector);
        }
        return inspectors;
    }
}
