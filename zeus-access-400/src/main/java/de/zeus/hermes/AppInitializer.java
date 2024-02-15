package de.zeus.hermes;

import de.zeus.hermes.service.DatabaseService;
import de.zeus.hermes.util.Config;
import de.zeus.hermes.util.PropertiesLoader;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The AppInitializer class serves as the entry point for the application, responsible for initializing the application
 * environment, loading configuration settings, and starting the database service process. It demonstrates how to
 * utilize the Config and DatabaseService classes to facilitate database operations based on configurations specified
 * in an external properties file.
 *
 * This class emphasizes the importance of externalized configuration for flexibility and maintainability of
 * Java applications. Through the use of the PropertiesLoader, it dynamically loads database connection details
 * and other settings, ensuring that the application can adapt to different environments without code changes.
 */
public class AppInitializer {

    private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.log(Level.SEVERE, "Please enter the path to the configuration file as an argument.");
            return;
        }
        new AppInitializer().init(args);
    }

    /**
     * Initializes the application using configuration settings specified in the provided file path. It loads the
     * configuration, sets up the application context, and initiates the SQL to XML export process using the
     * DatabaseService. This method encapsulates the bootstrap logic required to prepare the application for
     * running database operations.
     *
     * @param args Command line arguments passed to the application, expected to include the path to the configuration file.
     */
    private void init(String[] args) {

        String configFilePath = args[0];
        Properties properties = PropertiesLoader.loadProperties(configFilePath);
        if (properties == null) {
            LOGGER.log(Level.SEVERE, "Configuration file could not be loaded.");
            return;
        }

        // Initialize the config with the read properties
        Config config = Config.getInstance();
        config.setDriver(properties.getProperty("driver"));
        config.setDatabaseUrl(properties.getProperty("databaseUrl"));
        config.setUsername(properties.getProperty("username"));
        config.setPassword(properties.getProperty("password"));
        config.setQuery(properties.getProperty("query"));
        config.loadExportFormatsFromString(properties.getProperty("exportFormats"));
        config.setXsltPath(properties.getProperty("xsltpath"));
        config.setExportPath(properties.getProperty("exportpath"));

        // Log access to the configuration values as an example of successful configuration loading
        LOGGER.log(Level.INFO, "Database-URL: {0}", config.getDatabaseUrl());

        // Initialize DatabaseService and perform the SQL to XML process
        DatabaseService dbService = new DatabaseService();
        LOGGER.log(Level.INFO, "Starting the SQL to XML export process.");
        dbService.perform();
    }
}
