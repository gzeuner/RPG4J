package de.zeus.hermes;

import de.zeus.hermes.manager.System400Manager;
import de.zeus.hermes.service.DataQueueService;
import de.zeus.hermes.service.DatabaseService;
import de.zeus.hermes.util.Config;
import de.zeus.hermes.util.GenericRestClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ApplicationRunner class serves as the entry point for the application, orchestrating the execution based on the provided run mode.
 * It supports different operational modes such as executing a SQL export process or managing data queues for integration scenarios.
 */
public class ApplicationRunner {

    private static final Logger LOGGER = Logger.getLogger(ApplicationRunner.class.getName());
    private final Config config;

    /**
     * Constructs an ApplicationRunner with the specified configuration settings.
     * @param config the application configuration settings
     */
    public ApplicationRunner(Config config) {
        this.config = config;
    }

    /**
     * Executes the application based on the provided command-line arguments.
     * It supports run modes for SQL export and data queue operations, specified via "--runMode".
     * Additionally, an SQL query can be provided for the SQL export mode using "--sql".
     * @param args the command-line arguments specifying the run mode and optional SQL query
     */
    public void run(String[] args) {
        String runMode = null;
        String sqlQuery = null; // This is set only if --runMode is sqlExport

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if ("--runMode".equals(args[i]) && i + 1 < args.length) {
                runMode = args[++i];
                // Accept the next argument as an SQL query if the mode is sqlExport and --sql follows
                if ("sqlExport".equals(runMode) && i + 2 < args.length && "--sql".equals(args[i + 1])) {
                    i += 2; // Skip "--sql"
                    sqlQuery = args[i]; // Assign sqlQuery the value
                }
            }
        }

        // Check if runMode is set to prevent NullPointerException
        if (runMode == null) {
            LOGGER.log(Level.SEVERE, "No runMode specified.");
            return; // Exit the method if no runMode is specified
        }

        // Execute based on the mode
        switch (runMode) {
            case "sqlExport":
                executeSqlExport(sqlQuery != null ? sqlQuery : config.getQuery()); // Use the provided or default SQL query
                break;
            case "qLink":
                executeDtaQueue();
                break;
            case "restApi":
                executeRestApi();
                break;
            default:
                LOGGER.log(Level.SEVERE, "Invalid runMode specified.");
        }
    }

    /**
     * Fetches weather data from the MetaWeather API for Berlin.
     */
    private void executeRestApi() {
        // URL of the MetaWeather API endpoint for Berlin
        String restUrl = "https://wttr.in/Berlin?format=%C+%t+%w\n";
        ;

        // Create an instance of GenericRestClient
        GenericRestClient restClient = new GenericRestClient();

        // Fetch data from the MetaWeather API
        String responseData = restClient.fetchResponseAsString(restUrl);

        // Process the fetched data (e.g., parse JSON)
        if (responseData != null) {
            // Process the data (e.g., convert JSON to Java objects)
            LOGGER.log(Level.INFO, "Received weather data for Berlin from MetaWeather API: {0}", responseData);
        } else {
            LOGGER.log(Level.SEVERE, "Failed to fetch weather data for Berlin from MetaWeather API.");
        }
    }


    /**
     * Executes the SQL export process using the provided SQL query.
     * @param sqlQuery the SQL query to execute, or null to use the default query from the configuration
     */
    private void executeSqlExport(String sqlQuery) {
        DatabaseService databaseService = new DatabaseService();
        databaseService.perform(sqlQuery);
    }

    /**
     * Manages data queue operations for integration scenarios.
     */
    private void executeDtaQueue() {
        DataQueueService dataQueueService = new DataQueueService(new System400Manager());
        dataQueueService.manageDataQueue();
    }
}
