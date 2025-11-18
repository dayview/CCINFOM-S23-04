package businesspermitsystem.db;

import businesspermitsystem.controllers.InspectorScheduleController;
import businesspermitsystem.models.InspectorScheduleModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InspectorScheduleDAO {

    private final Connection connection;

    public InspectorScheduleDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("ERROR: Database connection not established.");
        }
    }

    /**
     * Inserts a new inspector schedule (assignment) into the inspection_schedule table.
     */
    public boolean addSchedule(InspectorScheduleModel schedule) {

        String sql = """
            INSERT INTO inspection_schedule (inspector_id, business_id, inspection_date, status)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, schedule.getInspectorId());
            stmt.setInt(2, schedule.getBusinessId());
            stmt.setDate(3, java.sql.Date.valueOf(schedule.getInspectionDate()));
            stmt.setString(4, schedule.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting inspector schedule: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean createSchedule(int inspectorId, int businessId, LocalDate date) {

        String sql = """
        INSERT INTO inspection_schedule (inspector_id, business_id, inspection_date, status)
        VALUES (?, ?, ?, 'Scheduled')
    """;

        try (PreparedStatement stmt = DatabaseConnector.connection.prepareStatement(sql)) {

            stmt.setInt(1, inspectorId);
            stmt.setInt(2, businessId);
            stmt.setDate(3, java.sql.Date.valueOf(date));

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<InspectorScheduleController.ApplicationEntry> getPaidApplicationsForScheduling() {

        List<InspectorScheduleController.ApplicationEntry> list = new ArrayList<>();

        String sql = """
        SELECT pa.application_id,
               pa.business_id,
               b.municipality_id,
               b.business_name
        FROM permit_application pa
        JOIN business b ON pa.business_id = b.business_id
        WHERE pa.status = 'Paid'
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int appId = rs.getInt("application_id");
                int bizId = rs.getInt("business_id");
                int muniId = rs.getInt("municipality_id");
                String businessName = rs.getString("business_name");

                list.add(new InspectorScheduleController.ApplicationEntry(
                        appId, bizId, muniId, businessName
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateScheduleStatus(int scheduleId, String status) {

        String sql = "UPDATE inspection_schedule SET status = ? WHERE schedule_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, scheduleId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
