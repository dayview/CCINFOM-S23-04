package businesspermitsystem.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection utility class
 * Manages database connection for MySQL
 * @author Leon Pavino
 */
public class DatabaseConnection {
    // To modify -- currently a placeholder
    private static final String URL = "jdbc:mysql//localhost:3306/business_permit_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // change to MySQL password

    /**
     * Establishes and returns a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("Driver not found.");
        }
    }

    /**
     * Test method to check if database connection works
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}