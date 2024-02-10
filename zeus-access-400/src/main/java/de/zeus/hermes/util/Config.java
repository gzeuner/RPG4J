package de.zeus.hermes.util;

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
}
