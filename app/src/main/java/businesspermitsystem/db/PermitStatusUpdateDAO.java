package businesspermitsystem.db;

import businesspermitsystem.models.PermitStatusUpdateModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermitStatusUpdateDAO {

    private final Connection connection;

    public PermitStatusUpdateDAO() {
        this.connection = DatabaseConnector.connection;
    }

    /**
     * Returns businesses with passed inspections,
     * no permit yet issued.
     */
    public List<PermitStatusUpdateModel> getEligibleForIssuance() {

        List<PermitStatusUpdateModel> list = new ArrayList<>();

        String sql = """
            SELECT 
                ir.inspection_id,
                s.business_id,
                b.business_name,
                pa.permit_type_id,
                pt.permit_name,
                pt.validity_months
            FROM inspection_result ir
            JOIN inspection_schedule s ON ir.schedule_id = s.schedule_id
            JOIN business b ON s.business_id = b.business_id
            JOIN permit_application pa ON pa.business_id = b.business_id
            JOIN permit_type pt ON pa.permit_type_id = pt.permit_type_id
            WHERE ir.result = 'Pass'
              AND NOT EXISTS (
                    SELECT 1 FROM permit p
                    WHERE p.business_id = b.business_id
                      AND p.permit_type_id = pa.permit_type_id
              )
            ORDER BY ir.inspection_id ASC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PermitStatusUpdateModel model = new PermitStatusUpdateModel();

                model.setInspectionId(rs.getInt("inspection_id"));
                model.setBusinessId(rs.getInt("business_id"));
                model.setBusinessName(rs.getString("business_name"));
                model.setPermitTypeId(rs.getInt("permit_type_id"));
                model.setPermitTypeName(rs.getString("permit_name"));
                model.setValidityMonths(rs.getInt("validity_months"));

                list.add(model);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Inserts new permit entry into permit table.
     */
    public boolean issuePermit(int businessId, int permitTypeId,
                               Date startDate, Date endDate, String note) {

        String sql = """
            INSERT INTO permit (business_id, permit_type_id, status, status_effective_date,
                                note, validity_start, validity_end)
            VALUES (?, ?, 'Issued', CURDATE(), ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, businessId);
            stmt.setInt(2, permitTypeId);
            stmt.setString(3, note);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
