package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import businesspermitsystem.models.BusinessModel;

/**
 * Data Access Object (DAO) for the {@link BusinessModel} model.
 * This class handles all database interactions related to Business,
 * including adding, deleting, and updating records.
 * It uses {@link DatabaseConnector} to establish connections.
 */
public class BusinessDAO {

    /**
     * Adds a new business to the table.
     */
    public boolean addBusiness(BusinessModel business) throws SQLException {
        String sql = "INSERT INTO business (business_name, trade_name, street_address, barangay," +
                " business_type, tax_id, start_date, status, municipality_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);

        statement.setString(1, business.getBusinessName());
        statement.setString(2, business.getTradeName());
        statement.setString(3, business.getStreetAddress());
        statement.setString(4, business.getBarangay());
        statement.setString(5, business.getBusinessType());
        statement.setString(6, business.getTaxId());
        statement.setDate(7, java.sql.Date.valueOf(business.getStartDate()));
        statement.setString(8, business.getStatus());
        statement.setInt(9, business.getMunicipalityId());

        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0;
    }

    /**
     * Updates an existing business record.
     */
    public boolean updateBusiness(BusinessModel business) throws SQLException {
        String sql = "UPDATE business SET business_name = ?, trade_name = ?, street_address = ?, barangay = ?, " +
                "business_type = ?, tax_id = ?, start_date = ?, status = ?, municipality_id = ? " +
                "WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);

        statement.setString(1, business.getBusinessName());
        statement.setString(2, business.getTradeName());
        statement.setString(3, business.getStreetAddress());
        statement.setString(4, business.getBarangay());
        statement.setString(5, business.getBusinessType());
        statement.setString(6, business.getTaxId());
        statement.setDate(7, java.sql.Date.valueOf(business.getStartDate()));
        statement.setString(8, business.getStatus());
        statement.setInt(9, business.getMunicipalityId());
        statement.setInt(10, business.getBusinessId());

        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    }

    /**
     * Deletes a business by its ID.
     */
    public boolean deleteBusiness(int businessId) throws SQLException {
        String sql = "DELETE FROM business WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);
        statement.setInt(1, businessId);

        int rowsDeleted = statement.executeUpdate();
        return rowsDeleted > 0;
    }

    /**
     * This Method finds a business by its ID
     */
    public BusinessModel getBusinessByID(int businessId) throws SQLException{
        String sql= "SELECT * FROM business WHERE business_id = ?";
        PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);
        statement.setInt(1, businessId);


        ResultSet result = statement.executeQuery();

        if(result.next()){
            BusinessModel business = new BusinessModel();
            business.setBusinessId(result.getInt("business_id"));
            business.setBusinessName(result.getString("business_name"));
            business.setTradeName(result.getString("trade_name"));
            business.setStreetAddress(result.getString("street_address"));
            business.setBarangay(result.getString("barangay"));
            business.setBusinessType(result.getString("business_type"));
            business.setTaxId(result.getString("tax_id"));
            business.setStartDate(result.getDate("start_date").toLocalDate());
            business.setStatus(result.getString("status"));
            business.setMunicipalityId(result.getInt("municipality_id"));
            return business; //returns the new business
        }

        return null; //if theres no business found with smae business_Id

    }

    public BusinessModel getBusinessByName(String businessName) {
        String sql = "SELECT * FROM business WHERE business_name = ?";
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql);) {
            statement.setString(1, businessName);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    BusinessModel business = new BusinessModel();
                    business.setBusinessId(result.getInt("business_id"));
                    business.setBusinessName(result.getString("business_name"));
                    business.setTradeName(result.getString("trade_name"));
                    business.setStreetAddress(result.getString("street_address"));
                    business.setBarangay(result.getString("barangay"));
                    business.setBusinessType(result.getString("business_type"));
                    business.setTaxId(result.getString("tax_id"));
                    business.setStartDate(result.getDate("start_date").toLocalDate());
                    business.setStatus(result.getString("status"));
                    business.setMunicipalityId(result.getInt("municipality_id"));
                    return business;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not found
    }

    public List<BusinessModel> getAllBusinesses() {
        List<BusinessModel> list = new ArrayList<>();

        String sql = "SELECT business_id, business_name, trade_name, street_address, " +
                "barangay, business_type, tax_id, start_date, status, municipality_id " +
                "FROM business";

        try (PreparedStatement preparedStatement = DatabaseConnector.connection.prepareStatement(sql);
             ResultSet result = preparedStatement.executeQuery()) {

            while (result.next()) {
                BusinessModel business = new BusinessModel(
                        result.getInt("business_id"),
                        result.getString("business_name"),
                        result.getString("trade_name"),
                        result.getString("street_address"),
                        result.getString("barangay"),
                        result.getString("business_type"),
                        result.getString("tax_id"),
                        result.getDate("start_date").toLocalDate(),
                        result.getString("status"),
                        result.getInt("municipality_id")
                );
                list.add(business);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<BusinessModel> getBusinessesWithOwnerCount() {
        List<BusinessModel> list = new ArrayList<>();

        String sql =
                "SELECT \n" +
                        "    b.business_id, \n" +
                        "    b.business_name, \n" +
                        "    b.trade_name, \n" +
                        "    b.street_address, \n" +
                        "    b.barangay, \n" +
                        "    b.business_type, \n" +
                        "    b.tax_id, \n" +
                        "    b.start_date, \n" +
                        "    b.status, \n" +
                        "    b.municipality_id,\n" +
                        "    COUNT(bo.owner_id) AS owner_count\n" +
                        "FROM business b\n" +
                        "JOIN business_owner bo ON b.business_id = bo.business_id\n" +
                        "GROUP BY \n" +
                        "    b.business_id, \n" +
                        "    b.business_name, \n" +
                        "    b.trade_name, \n" +
                        "    b.street_address, \n" +
                        "    b.barangay, \n" +
                        "    b.business_type, \n" +
                        "    b.tax_id, \n" +
                        "    b.start_date, \n" +
                        "    b.status, \n" +
                        "    b.municipality_id\n" +
                        "ORDER BY b.business_name ASC;";

        try (PreparedStatement stmt = DatabaseConnector.connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BusinessModel businessModel = new BusinessModel();

                businessModel.setBusinessId(rs.getInt("business_id"));
                businessModel.setBusinessName(rs.getString("business_name"));
                businessModel.setTradeName(rs.getString("trade_name"));
                businessModel.setStreetAddress(rs.getString("street_address"));
                businessModel.setBarangay(rs.getString("barangay"));
                businessModel.setBusinessType(rs.getString("business_type"));
                businessModel.setTaxId(rs.getString("tax_id"));

                if (rs.getDate("start_date") != null) {
                    businessModel.setStartDate(rs.getDate("start_date").toLocalDate());
                }

                businessModel.setStatus(rs.getString("status"));
                businessModel.setMunicipalityId(rs.getInt("municipality_id"));

                // NEW FIELD (not within the business table calculated independently)
                businessModel.setOwnerCount(rs.getInt("owner_count"));

                list.add(businessModel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


}
