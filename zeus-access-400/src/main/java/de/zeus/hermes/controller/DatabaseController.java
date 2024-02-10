package de.zeus.hermes.controller;

import de.zeus.hermes.util.Config;
import de.zeus.hermes.manager.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;


import de.zeus.hermes.util.XmlExporter;

/**
 * The DatabaseController is responsible for managing the database connection lifecycle within the application.
 * It utilizes the {@link DatabaseManager} to establish and close connections to the database based on the
 * configuration parameters specified in the {@link Config} class. This controller ensures that a single
 * instance of the database connection is maintained and utilized throughout the application, supporting
 * operations such as connecting to and disconnecting from the database.
 *
 * This class provides methods to connect to the database using configuration parameters (driver, URL, username,
 * and password) and to safely close the connection when it is no longer needed. Logging is utilized to monitor the
 * success and failure of connection attempts, offering visibility into the database connection status.
 *
 * Additionally, it now delegates the task of exporting database query results to XML format to the {@link XmlExporter} class,
 * demonstrating a separation of concerns and enhancing the modularity of the application.
 */
public class DatabaseController {


    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());
    private final DatabaseManager dbManager = new DatabaseManager();
    private final Config config = Config.getInstance();
    private Connection connection = null;

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
    public void runSqlToXmlProcess() {
        try {
            connectToDatabase();
            String query = config.getQuery();
            PreparedStatement stmt = dbManager.getPreparedStmt(connection, query);
            ResultSet rs = stmt.executeQuery();

            String rootNode = "export";
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String fileName = timestamp + "_" + rootNode + ".xml";
            XmlExporter.exportResultSetToXML(rs, rootNode, fileName);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when executing the database operation", e);
        } finally {
            disconnectFromDatabase();
        }
    }
}
