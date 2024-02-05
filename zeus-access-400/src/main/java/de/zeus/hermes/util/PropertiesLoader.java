package de.zeus.hermes.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesLoader {

    private static final Logger LOGGER = Logger.getLogger(PropertiesLoader.class.getName());

    public static Properties loadProperties(String fileName) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(fileName)) {
            // Loads the properties file
            prop.load(input);
            LOGGER.log(Level.INFO, "Properties file {0} successfully loaded from the file system.", fileName);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Properties file " + fileName + " not found in the file system. Searching in the JAR...", ex);

            // If the file is not in the file system, search in the JAR
            try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName)) {
                if (input != null) {
                    prop.load(input);
                    LOGGER.log(Level.INFO, "Properties-File {0} successfully loaded from the JAR.", fileName);
                } else {
                    LOGGER.log(Level.SEVERE, "File {0} not found, neither in the file system nor in the JAR.", fileName);
                    return null;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading the properties file " + fileName, e);
                return null;
            }
        }
        return prop;
    }
}
