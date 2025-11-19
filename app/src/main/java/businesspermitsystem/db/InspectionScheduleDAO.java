package businesspermitsystem.db;

import businesspermitsystem.models.InspectionScheduleModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the InspectionSchedule entity.
 * Handles CRUD operations for scheduling and managing inspections.
 */
public class InspectionScheduleDAO {

    /**
     * Helper method to map a ResultSet row to an InspectionScheduleModel object.
     * @param result The ResultSet positioned at the current row.
     * @return A fully populated InspectionScheduleModel.
     * @throws SQLException
     */
    private InspectionScheduleModel mapResultSetToSchedule(ResultSet result) throws SQLException {
        InspectionScheduleModel schedule = new InspectionScheduleModel();
        schedule.setScheduleID(result.getInt("schedule_id"));
        schedule.setBusinessID(result.getInt("business_id"));
        schedule.setInspectorID(result.getInt("inspector_id"));
        // SQL Date to LocalDate conversion
        if (result.getDate("inspection_date") != null) {
            schedule.setInspectionDate(result.getDate("inspection_date").toLocalDate());
        }
        schedule.setStatus(result.getString("status"));
        return schedule;
    }
    
    /**
     * Adds a new inspection schedule record to the database with a default status of "Scheduled".
     * @param schedule The InspectionScheduleModel to be inserted.
     * @return true if the schedule was successfully added, false otherwise.
     * @throws SQLException 
     */
    public boolean addSchedule(InspectionScheduleModel schedule) throws SQLException {
        // SQL only includes fields from the finalized model: business_id, inspector_id, inspection_date, status
        String sql = "INSERT INTO inspection_schedule (business_id, inspector_id, inspection_date, status) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            
            statement.setInt(1, schedule.getBusinessID());
            statement.setInt(2, schedule.getInspectorID());
            statement.setDate(3, java.sql.Date.valueOf(schedule.getInspectionDate()));
            statement.setString(4, "Scheduled"); // Default status upon creation

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
    }
    
    /**
     * Retrieves an inspection schedule record by its primary key.
     * @param id The primary key (schedule_id) of the schedule.
     * @return The InspectionScheduleModel if found, null otherwise.
     * @throws SQLException
     */
    public InspectionScheduleModel getScheduleByID(int id) throws SQLException {
        String sql = "SELECT * FROM inspection_schedule WHERE schedule_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapResultSetToSchedule(result);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all inspection schedule records from the database.
     * @return A list of all InspectionScheduleModel objects.
     * @throws SQLException
     */
    public List<InspectionScheduleModel> getAllSchedules() throws SQLException {
        List<InspectionScheduleModel> schedules = new ArrayList<>();
        String sql = "SELECT * FROM inspection_schedule ORDER BY inspection_date ASC";
        
        try (Statement statement = DatabaseConnector.connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            
            while (result.next()) {
                schedules.add(mapResultSetToSchedule(result));
            }
        }
        return schedules;
    }
    
/**
 * Retrieves schedules where the status is NOT 'Complete' (i.e., not yet reported),
 * and optionally filters by an end date.
 * * @param endDate The date to check against (schedule date <= endDate). 
 * If null, no date filter is applied.
 * @return A list of eligible InspectionScheduleModel objects.
 * @throws SQLException
 */
public List<InspectionScheduleModel> getFilteredSchedules(LocalDate endDate) throws SQLException {
    List<InspectionScheduleModel> schedules = new ArrayList<>();
    
    String sql = "SELECT * FROM inspection_schedule WHERE status != 'Complete'";
    
    if (endDate != null) {
        
        sql += " AND inspection_date <= ?";
    }
    sql += " ORDER BY inspection_date ASC";
    
    try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
        if (endDate != null) {
            statement.setDate(1, java.sql.Date.valueOf(endDate));
        }
        
        try (ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                schedules.add(mapResultSetToSchedule(result));
            }
        }
    }
    return schedules;
}
    
    /**
     * Updates an existing inspection schedule record.
     * @param schedule The InspectionScheduleModel object containing the updated data.
     * @return true if the record was updated, false otherwise.
     * @throws SQLException
     */
    public boolean updateSchedule(InspectionScheduleModel schedule) throws SQLException {
        String sql = "UPDATE inspection_schedule SET business_id = ?, inspector_id = ?, inspection_date = ?, status = ? WHERE schedule_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            
            statement.setInt(1, schedule.getBusinessID());
            statement.setInt(2, schedule.getInspectorID());
            statement.setDate(3, java.sql.Date.valueOf(schedule.getInspectionDate()));
            statement.setString(4, schedule.getStatus());
            statement.setInt(5, schedule.getScheduleID());
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        }
    }
    
    /**
     * Deletes an inspection schedule record from the database using its ID.
     * @param id The primary key (schedule_id) of the schedule to delete.
     * @return true if the record was deleted, false otherwise.
     * @throws SQLException
     */
    public boolean deleteSchedule(int id) throws SQLException {
        String sql = "DELETE FROM inspection_schedule WHERE schedule_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }
}