package de.zeus.hermes;

import de.zeus.hermes.service.DatabaseService;
import de.zeus.hermes.util.Config;
import de.zeus.hermes.util.PropertiesLoader;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInitializer {

    private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.log(Level.SEVERE, "Please enter the path to the configuration file as an argument.");
            return;
        }
        new AppInitializer().init(args);
    }

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

        // Example: Access to the configuration values
        LOGGER.log(Level.INFO, "Database-URL: {0}", config.getDatabaseUrl());

        // Initialize DatabaseService and perform the SQL to XML process
        DatabaseService dbService = new DatabaseService();
        dbService.sqlToXml();
    }
}
