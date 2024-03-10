package de.zeus.hermes.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DatabaseManager class is responsible for managing database connections and executing SQL statements.
 * It provides methods to establish connections to the database using provided credentials, close connections,
 * and prepare and execute SQL statements. This class acts as a utility to abstract and simplify database
 * operations for the application.
 *
 * Methods in this class include:
 * - {@link #getDatabaseConnection(String, String, String, String)} to connect to the database using specified driver, URL, username, and password.
 * - {@link #closeDatabaseConnection(Connection)} to safely close an established database connection.
 * - {@link #getPreparedStmt(Connection, String)} to prepare an SQL statement for execution.
 * - {@link #getStmt(Connection)} to create a Statement object for sending SQL statements to the database.
 *
 * The class uses {@link java.util.logging.Logger} to log information and errors related to database operations,
 * aiding in debugging and monitoring the application's interaction with the database.
 */
public class DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;

    private DatabaseManager() {
        // No objects should be created from this class.
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getDatabaseConnection(String driver, String url, String user, String pwd) {
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, pwd);
            LOGGER.log(Level.INFO, "Connected to [" + url + "]");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't get a connection to DB-Server [" + url + "]", e);
            System.exit(1);
        }
        return con;
    }

    public void closeDatabaseConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                LOGGER.log(Level.INFO, "DB-Connection closed.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error while closing connection", e);
            }
        }
    }

    public PreparedStatement getPreparedStmt(Connection con, String query) {
        try {
            return con.prepareStatement(query);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error while preparing statement", e);
            return null;
        }
    }

    public Statement getStmt(Connection con) throws SQLException {
        return con.createStatement();
    }

}
