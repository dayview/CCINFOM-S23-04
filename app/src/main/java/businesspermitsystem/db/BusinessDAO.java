package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import businesspermitsystem.models.BusinessModel;

public class BusinessDAO {

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
}
