package de.zeus.hermes.util;

import java.util.HashSet;
import java.util.Set;

/**
 * The Config class serves as a centralized repository for managing application configuration settings. It is designed
 * as a singleton to ensure a single, globally accessible instance that holds configuration parameters such as database
 * connection details (driver, URL, username, password) and query specifications. This approach facilitates easy access
 * and modification of critical settings throughout the application, promoting consistency and reducing the likelihood
 * of configuration errors.
 *
 * This class provides methods to set and retrieve configuration parameters, enabling dynamic adjustments to the application's
 * behavior based on the provided settings. It is typically populated at application startup from external sources like
 * configuration files, environment variables, or command-line arguments, ensuring flexibility and adaptability to different
 * runtime environments.
 *
 * By abstracting configuration management into a dedicated class, the Config class simplifies the process of managing
 * application settings, making it easier to maintain and update configurations as the application evolves or as it is
 * deployed across different environments.
 */
public class Config {

    private static Config instance;

    // Config variables
    private String driver;
    private String databaseUrl;
    private String username;
    private String password;
    private String query;
    private String xsltPath;
    private String exportPath;
    private String as400System;
    private String as400Username;
    private String as400Password;
    private String dataQueueLibrary;
    private String rpgToJavaQueueName;
    private String javaToRpgQueueName;
    private int maxEntryLength;
    private String dataQueueDescription;

    private final Set<String> exportFormats = new HashSet<>();

    // Private constructor to prevent instantiation outside
    private Config() {
        // No objects should be created from this class.
    }

    // Public method to obtain the singleton instance
    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    // Getter and setter methods for your configuration variables

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Set<String> getExportFormats() {
        return exportFormats;
    }

    public String getXsltPath() {
        return xsltPath;
    }

    public void setXsltPath(String xsltPath) {
        this.xsltPath = xsltPath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public String getAs400System() {
        return as400System;
    }

    public void setAs400System(String as400System) {
        this.as400System = as400System;
    }

    public String getAs400Username() {
        return as400Username;
    }

    public void setAs400Username(String as400Username) {
        this.as400Username = as400Username;
    }

    public String getAs400Password() {
        return as400Password;
    }

    public void setAs400Password(String as400Password) {
        this.as400Password = as400Password;
    }

    public String getDataQueueLibrary() {
        return dataQueueLibrary;
    }

    public void setDataQueueLibrary(String dataQueueLibrary) {
        this.dataQueueLibrary = dataQueueLibrary;
    }

    public String getRpgToJavaQueueName() {
        return rpgToJavaQueueName;
    }

    public void setRpgToJavaQueueName(String rpgToJavaQueueName) {
        this.rpgToJavaQueueName = rpgToJavaQueueName;
    }

    public String getJavaToRpgQueueName() {
        return javaToRpgQueueName;
    }

    public void setJavaToRpgQueueName(String javaToRpgQueueName) {
        this.javaToRpgQueueName = javaToRpgQueueName;
    }

    public int getMaxEntryLength() {
        return maxEntryLength;
    }

    public void setMaxEntryLength(int maxEntryLength) {
        this.maxEntryLength = maxEntryLength;
    }

    public String getDataQueueDescription() {
        return dataQueueDescription;
    }

    public void setDataQueueDescription(String dataQueueDescription) {
        this.dataQueueDescription = dataQueueDescription;
    }

    // Method to add formats from a comma-separated string (for easy loading from properties file)
    public void loadExportFormatsFromString(String formats) {
        if (formats != null && !formats.isEmpty()) {
            String[] formatArray = formats.split(",");
            for (String format : formatArray) {
                this.exportFormats.add(format.trim());
            }
        }
    }
}
