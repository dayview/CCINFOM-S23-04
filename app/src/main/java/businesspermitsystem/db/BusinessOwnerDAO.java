package businesspermitsystem.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents the business_owner table and allows for the link of a business to owner
 *
 */
public class BusinessOwnerDAO {

    public static boolean linkBusinessAndOwner(int businessId, int ownerId) {
        String sql = "INSERT INTO business_owner (business_id, owner_id) VALUES (?, ?)";

        try (PreparedStatement ps = DatabaseConnector.connection.prepareStatement(sql)) {
            ps.setInt(1, businessId);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            //checks if there is a duplicate owner for a given business
            if (e.getMessage().contains("Duplicate entry")) {
                return false;
            }

            e.printStackTrace();
            return false;
        }
    }

}
