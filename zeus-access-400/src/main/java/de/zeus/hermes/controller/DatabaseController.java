package de.zeus.hermes.controller;

import de.zeus.hermes.util.Config;
import de.zeus.hermes.manager.DatabaseManager;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DatabaseController is responsible for managing the database connection lifecycle within the application.
 * It utilizes the {@link DatabaseManager} to establish and close connections to the database based on the
 * configuration parameters defined in the {@link Config} class. This controller ensures that a single
 * instance of the database connection is maintained and used throughout the application, supporting
 * operations such as connecting to the database and disconnecting from it.
 *
 * The class provides methods to connect to the database using configuration parameters (driver, URL, username,
 * and password) and to safely close the connection when it is no longer needed. Logging is used to track the
 * success and failure of connection attempts, providing visibility into the database connection status.
 */
public class DatabaseController {

    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());
    private final DatabaseManager dbManager = new DatabaseManager();
    private Connection connection = null;
    private Config config = Config.getInstance();

    // Establishes a database connection using the configurations specified in Config class.
    public void connectToDatabase() {
        if (connection == null) {
            connection = dbManager.getDatabaseConnection(config.getDriver(),
                    config.getDatabaseUrl(), config.getUsername(), config.getPassword());
            if (connection != null) {
                LOGGER.log(Level.INFO,"Successfully connected to the database.");
            }
        }
    }

    // Closes the database connection if it is currently established.
    public void disconnectFromDatabase() {
        if (connection != null) {
            dbManager.closeDatabaseConnection(connection);
            LOGGER.log(Level.INFO, "Database connection has been disconnected.");
            connection = null;
        }
    }

}
