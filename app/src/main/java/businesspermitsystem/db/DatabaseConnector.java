package businesspermitsystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing MySQL database connections.
 * 
 * Provides methods for connecting to and testing a database
 * using dynamic credentials. This version is designed for
 * use with a JavaFX login screen where users can input
 * their own connection details.
 */
public class DatabaseConnector {

    /**
     * Establishes and returns a MySQL database connection.
     * 
     * @param url      the JDBC connection URL
     * @param username the database username
     * @param password the database password
     * @return a valid {@link Connection} object if successful
     * @throws SQLException if the connection fails or driver is missing
     */
    public static Connection getConnection(String url, String username, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }

    /**
     * Tests the provided database credentials without storing them.
     * 
     * This method is used primarily to verify that the user’s
     * connection settings are valid before proceeding to the
     * main application interface.
     * 
     * @param url      the JDBC connection URL
     * @param username the database username
     * @param password the database password
     * @return a {@link Connection} if the connection succeeds
     * @throws SQLException if unable to connect to the database
     */
    public static Connection testConnection(String url, String username, String password) throws SQLException {
        return getConnection(url, username, password);
    }

}
