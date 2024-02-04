package de.zeus.hermes.util;

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
