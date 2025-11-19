package businesspermitsystem.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import businesspermitsystem.models.InspectionModel;


public class InspectionDAO {
    
    private  Connection connection;

    public InspectionDAO() {
        this.connection = DatabaseConnector.connection;

        if (this.connection == null) {
            System.err.println("Warning: Database connection not established. Call DatabaseConnector.getConnection() first.");
        }
    }

    public InspectionModel getInspectionByID(int inspectionID) {
        String query = "SELECT * FROM inspection WHERE inspection_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, inspectionID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new InspectionModel(
                        result.getInt("inspection_id"),
                        result.getInt("renewal_id"),
                        result.getInt("inspector_id"),
                        result.getDate("inspection_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving inspection: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public InspectionModel getInspectionByRenewal(int renewalID) {
        String query = "SELECT * FROM inspection WHERE renewal_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, renewalID);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new InspectionModel(
                        result.getInt("inspection_id"),
                        result.getInt("renewal_id"),
                        result.getInt("inspector_id"),
                        result.getDate("inspection_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving inspection: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<InspectionModel> getAllInspection() {
        ArrayList<InspectionModel> inspections = new ArrayList<>();
        String query = "SELECT * FROM inspection";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                InspectionModel inspection = new InspectionModel(
                        result.getInt("inspection_id"),
                        result.getInt("renewal_id"),
                        result.getInt("inspector_id"),
                        result.getDate("inspection_date")
                );
                inspections.add(inspection);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving inspections: " + e.getMessage());
            e.printStackTrace();
        }
        return inspections;
    }

    public boolean addInspection(InspectionModel inspection) {
        String query = "INSERT INTO inspection (renewal_id, inspector_id, inspection_date) VALUES (?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, inspection.getRenewalID());
            statement.setInt(2, inspection.getInspectorID());
            statement.setDate(3, new java.sql.Date(inspection.getInspectionDate().getTime()));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding inspection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int addInspectionGetID(InspectionModel inspection) {
    String query = "INSERT INTO inspection (renewal_id, inspector_id, inspection_date) VALUES (?, ?, ?)";
    
    try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        statement.setInt(1, inspection.getRenewalID());
        statement.setInt(2, inspection.getInspectorID());
        statement.setDate(3, new java.sql.Date(inspection.getInspectionDate().getTime()));

        int rowsAffected = statement.executeUpdate();
        
        if (rowsAffected > 0) {
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;

    } catch (SQLException e) {
        System.err.println("Error adding inspection: " + e.getMessage());
        e.printStackTrace();
        return -1;
    }
}

    public boolean updateInspection(InspectionModel inspection) {
        String query = "UPDATE inspection SET renewal_id = ?, inspector_id = ?, inspection_date = ? WHERE inspection_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, inspection.getRenewalID());
            statement.setInt(2, inspection.getInspectorID());
            statement.setDate(3, new java.sql.Date(inspection.getInspectionDate().getTime()));
            statement.setInt(4, inspection.getInspectionID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating inspection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInspection(int inspectionID) {
        String query = "DELETE FROM inspection WHERE inspection_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, inspectionID);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting inspection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
