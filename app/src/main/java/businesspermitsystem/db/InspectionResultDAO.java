package businesspermitsystem.db;

import businesspermitsystem.models.InspectionResultModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the InspectionResult entity.
 * Handles database interactions related to saving and retrieving inspection outcomes.
 */
public class InspectionResultDAO {

    /**
     * Helper method to map a ResultSet row to an InspectionResultModel object.
     * @param result The ResultSet positioned at the current row.
     * @return A fully populated InspectionResultModel.
     * @throws SQLException
     */
    private InspectionResultModel mapResultSetToResult(ResultSet result) throws SQLException {
        InspectionResultModel model = new InspectionResultModel();
        model.setInspectionId(result.getInt("inspection_id"));
        model.setScheduleId(result.getInt("schedule_id"));
        model.setResult(result.getString("result"));
        model.setRemarks(result.getString("remarks"));
        return model;
    }
    
    /**
     * Adds a new inspection result record (C in CRUD).
     * @param resultModel The findings to be inserted.
     * @return true if the result was successfully added, false otherwise.
     * @throws SQLException 
     */
    public boolean addResult(InspectionResultModel resultModel) throws SQLException {
        String sql = "INSERT INTO inspection_result (schedule_id, result, remarks) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            
            statement.setInt(1, resultModel.getScheduleId());
            statement.setString(2, resultModel.getResult());
            statement.setString(3, resultModel.getRemarks());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
    }
    
    /**
     * Retrieves an inspection result record by its primary key.
     * @param id The primary key (inspection_id).
     * @return The InspectionResultModel if found, null otherwise.
     * @throws SQLException
     */
    public InspectionResultModel getResultByID(int id) throws SQLException {
        String sql = "SELECT * FROM inspection_result WHERE inspection_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapResultSetToResult(result);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all inspection result records from the database.
     * @return A list of all InspectionResultModel objects.
     * @throws SQLException
     */
    public List<InspectionResultModel> getAllResults() throws SQLException {
        List<InspectionResultModel> results = new ArrayList<>();
        String sql = "SELECT * FROM inspection_result ORDER BY inspection_id DESC";
        
        try (Statement statement = DatabaseConnector.connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            
            while (result.next()) {
                results.add(mapResultSetToResult(result));
            }
        }
        return results;
    }
    
    /**
     * Checks if a result already exists for a given schedule (used in ReportFindingsController).
     * @param scheduleId The ID of the schedule to check.
     * @return true if a result record exists, false otherwise.
     * @throws SQLException
     */
    public boolean resultExistsForSchedule(int scheduleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM inspection_result WHERE schedule_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            statement.setInt(1, scheduleId);
            
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Updates an existing inspection result record.
     * @param resultModel The InspectionResultModel object containing the updated data.
     * @return true if the record was updated, false otherwise.
     * @throws SQLException
     */
    public boolean updateResult(InspectionResultModel resultModel) throws SQLException {
        String sql = "UPDATE inspection_result SET schedule_id = ?, result = ?, remarks = ? WHERE inspection_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            
            statement.setInt(1, resultModel.getScheduleId());
            statement.setString(2, resultModel.getResult());
            statement.setString(3, resultModel.getRemarks());
            statement.setInt(4, resultModel.getInspectionId());
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        }
    }
    
    /**
     * Deletes an inspection result record from the database using its ID.
     * @param id The primary key (inspection_id) of the result to delete.
     * @return true if the record was deleted, false otherwise.
     * @throws SQLException
     */
    public boolean deleteResult(int id) throws SQLException {
        String sql = "DELETE FROM inspection_result WHERE inspection_id = ?";
        
        try (PreparedStatement statement = DatabaseConnector.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }
}