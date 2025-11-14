package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.MunicipalityModel;

/**
 * Data Access Object (DAO) for the {@link MunicipalityModel}.
 * * This class handles all database interactions related to municipalities
 * including creating, reading, updating, and deleting records.
 * * It uses {@link DatabaseConnector} to establish connections.
 */
public class MunicipalityDAO {

    /**
     * Helper method to map a ResultSet row to a MunicipalityModel object.
     */
    private MunicipalityModel mapResultSetToModel(ResultSet result) throws SQLException {
        return new MunicipalityModel(
            result.getInt("municipality_id"),
            result.getString("municipality_name"),
            result.getString("province"),
            result.getString("region"),
            result.getString("classification"),
            result.getString("contact_number"),
            result.getString("office_street"),
            result.getString("office_barangay"),
            result.getString("office_zipcode")
        );
    }
    

    /**
     * Adds a new Municipality into the database.
     * @param municipality is the model being inserted into the database.
     * @throws SQLException
     */
    public void addMunicipality(MunicipalityModel municipality) throws SQLException {
        String query = "INSERT INTO municipality (municipality_name, province, region, classification, contact_number, office_street, office_barangay, office_zipcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            
            statement.setString(1, municipality.getMunicipalityName());
            statement.setString(2, municipality.getProvince());
            statement.setString(3, municipality.getRegion()); 
            statement.setString(4, municipality.getClassification());
            statement.setString(5, municipality.getContactNumber());
            statement.setString(6, municipality.getOfficeStreet());
            statement.setString(7, municipality.getOfficeBarangay());
            statement.setString(8, municipality.getOfficeZipCode());

            statement.executeUpdate();
        }
    }
    
    /**
     * Returns the list of all municipalities in the database.
     * @return ArrayList<MunicipalityModel> that represents all the municipalities.
     * @throws SQLException
     */
    public ArrayList<MunicipalityModel> getMunicipalities() throws SQLException {
        ArrayList<MunicipalityModel> municipalities = new ArrayList<>();

        String query = "SELECT * FROM municipality";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                municipalities.add(mapResultSetToModel(result));
            }
        }
        return municipalities;
    }

    /**
     * Retrieves a single Municipality record from the database using its ID.
     * * This is the method needed by the UpdateInspectorController.
     * * @param id The primary key (municipality_id) of the municipality to retrieve.
     * @return MunicipalityModel or null if not found.
     * @throws SQLException
     */
    public MunicipalityModel getMunicipalityById(int id) throws SQLException {
        String query = "SELECT * FROM municipality WHERE municipality_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapResultSetToModel(result);
                }
            }
        }
        return null;
    }
    
    /**
     * Updates an existing Municipality record in the database.
     * @param municipality The MunicipalityModel object containing the updated data.
     * @throws SQLException
     */
    public void updateMunicipality(MunicipalityModel municipality) throws SQLException {
        String query = "UPDATE municipality SET municipality_name=?, province=?, region=?, classification=?, contact_number=?, office_street=?, office_barangay=?, office_zipcode=? WHERE municipality_id=?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            
            statement.setString(1, municipality.getMunicipalityName());
            statement.setString(2, municipality.getProvince());
            statement.setString(3, municipality.getRegion());
            statement.setString(4, municipality.getClassification());
            statement.setString(5, municipality.getContactNumber());
            statement.setString(6, municipality.getOfficeStreet());
            statement.setString(7, municipality.getOfficeBarangay());
            statement.setString(8, municipality.getOfficeZipCode());
            statement.setInt(9, municipality.getMunicipalityID()); // WHERE clause

            statement.executeUpdate();
        }
    }

    
    /**
     * Deletes a Municipality record from the database using its ID.
     * @param id The primary key (municipality_id) of the municipality to delete.
     * @throws SQLException
     */
    public void deleteMunicipality(int id) throws SQLException {
        String query = "DELETE FROM municipality WHERE municipality_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}