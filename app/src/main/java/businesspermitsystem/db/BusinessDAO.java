package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import businesspermitsystem.models.BusinessModel;
/**
 * Data Access Object (DAO) for the {@link BusinessModel} model.
 *
 * This class handles all database interactions related to Business,
 * Includes adding, deleting, or editing
 *
 * It uses {@link DatabaseConnector} to establish connections.
 */
public class BusinessDAO {

    /**
     *  Retrieves a business by their business ID
     */
    public BusinessModel getBusinessById(int businessId) throws SQLException {
        String sql = "SELECT * FROM business WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);
        statement.setInt(1, businessId);

        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new BusinessModel(
                    rs.getInt("business_id"),
                    rs.getString("business_name"),
                    rs.getString("trade_name"),
                    rs.getString("street_address"),   // atomic address field
                    rs.getString("barangay"),
                    rs.getString("city"),
                    rs.getString("province"),
                    rs.getString("business_type"),
                    rs.getString("tax_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getInt("municipality_id")
            );
        }
        return null;
    }

    /**
     *  Adds a new business to the table
     */
    public boolean addBusiness(BusinessModel business) throws SQLException {
        String sql = "INSERT INTO business (business_name, trade_name, street_address, barangay, city, province," +
                " business_type, tax_id, start_date, status, municipality_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);

        statement.setString(1, business.getBusinessName());
        statement.setString(2, business.getTradeName());
        statement.setString(3, business.getStreetAddress());
        statement.setString(4, business.getBarangay());
        statement.setString(5, business.getCity());
        statement.setString(6, business.getProvince());
        statement.setString(7, business.getBusinessType());
        statement.setString(8, business.getTaxId());
        statement.setDate(9, java.sql.Date.valueOf(business.getStartDate()));
        statement.setString(10, business.getStatus());
        statement.setInt(11, business.getMunicipalityId());

        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0; // returns 1 if something actually changed
    }

    /**
     *  Allows for updates of a table
     */
    public boolean updateBusiness(BusinessModel business) throws SQLException{
        String sql = "UPDATE business SET business_name = ?, trade_name = ?, street_address = ?, barangay = ?, city = ?, " +
                "province = ?, business_type = ?, tax_id = ?, start_date = ?, status = ?, municipality_id = ? WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);

        statement.setString(1, business.getBusinessName());
        statement.setString(2, business.getTradeName());
        statement.setString(3, business.getStreetAddress());
        statement.setString(4, business.getBarangay());
        statement.setString(5, business.getCity());
        statement.setString(6, business.getProvince());
        statement.setString(7, business.getBusinessType());
        statement.setString(8, business.getTaxId());
        statement.setDate(9, java.sql.Date.valueOf(business.getStartDate()));
        statement.setString(10, business.getStatus());
        statement.setInt(11, business.getMunicipalityId());
        statement.setInt(12, business.getBusinessId());

        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0; // returns 1 if something actually changed

    }

    /**
     * Deletes a Business by its ID.
     */
    public boolean deleteBusiness(int businessId) throws SQLException {
        String sql = "DELETE FROM business WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);
        statement.setInt(1, businessId);

        int rowsDeleted = statement.executeUpdate();
        return rowsDeleted > 0;
    }

}
