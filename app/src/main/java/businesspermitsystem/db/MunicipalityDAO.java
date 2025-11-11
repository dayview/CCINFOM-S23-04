package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.MunicipalityModel;

/**
 * Data Access Object (DAO) for the {@link MunicipalityModel}.
 * 
 * This class handles all database interactions related to municipalities
 * including creating, reading, updating, and deleting records.
 * 
 * It uses {@link DatabaseConnector} to establish connections.
 */
public class MunicipalityDAO {
    public void addMunicipality() {
        
    }

    public ArrayList<MunicipalityModel> getMunicipalities() throws SQLException {
        ArrayList<MunicipalityModel> municipalities = new ArrayList<MunicipalityModel>();

        String query = "SELECT * FROM municipality";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(query);

        ResultSet result = statement.executeQuery();

        while (result.next()) {
            MunicipalityModel municipality = new MunicipalityModel(
                result.getInt(0),
                result.getString(1),
                result.getString(0),
                result.getString(0),
                result.getString(0),
                result.getString(0)
            );
        }

        return municipalities;
    }
}
