package de.zeus.hermes.service;

import de.zeus.hermes.controller.DatabaseController;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DatabaseService class acts as a higher-level service layer that facilitates interactions with the database through
 * the DatabaseController. It abstracts the database operation processes, offering a simplified interface for performing
 * specific tasks such as exporting SQL query results to XML format. This design allows for better separation of concerns
 * by decoupling the application logic from direct database operations.
 *
 * By utilizing the DatabaseController, this service encapsulates the logic required to initiate and manage database
 * interactions, including connecting to the database, executing queries, and handling the conversion of results to XML.
 * This approach enhances the modularity and maintainability of the application, making it easier to adapt to changes
 * in business requirements or underlying database implementations.
 *
 * Additionally, the service includes logging capabilities to monitor its operations, providing visibility into the
 * execution flow and aiding in troubleshooting and debugging processes.
 */
public class DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());
    private final DatabaseController databaseController = new DatabaseController();

    /**
     * Initiates the process of exporting SQL query results to an XML file. This method leverages the DatabaseController
     * to connect to the database, execute a predefined SQL query, and export the results as XML. It encapsulates all
     * necessary steps to accomplish this task, offering a simplified and direct way to generate XML files from database
     * query results.
     */
    public void perform() {
        LOGGER.log(Level.INFO, "Initiating SQL to XML export process.");
        databaseController.runSqlToExportProcess();
    }
}
