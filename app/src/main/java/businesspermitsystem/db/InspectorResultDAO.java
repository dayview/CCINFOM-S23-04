package businesspermitsystem.db;

import businesspermitsystem.models.InspectorResultModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InspectorResultDAO {

    private final Connection connection;

    public InspectorResultDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established.");
        }
    }

    /** Save an inspector result */
    public boolean addInspectorResult(InspectorResultModel result) {

        String sql = """
            INSERT INTO inspection_result (schedule_id, result, remarks)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, result.getScheduleId());
            stmt.setString(2, result.getResult());
            stmt.setString(3, result.getRemarks());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting inspection result: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /** Retrieve all results for one schedule */
    public List<InspectorResultModel> getResultsByScheduleId(int scheduleId) {

        List<InspectorResultModel> list = new ArrayList<>();

        String sql = """
            SELECT * FROM inspection_result
            WHERE schedule_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, scheduleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InspectorResultModel result = new InspectorResultModel(
                        rs.getInt("inspection_id"),
                        rs.getInt("schedule_id"),
                        rs.getString("result"),
                        rs.getString("remarks")
                );
                list.add(result);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving inspector result: " + e.getMessage());
        }

        return list;
    }
}
