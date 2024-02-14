package de.zeus.hermes.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facilitates the loading of properties files from both the file system and the application's JAR.
 * This utility class provides a mechanism to read configuration settings or any other properties required by
 * the application from a .properties file. It attempts to load the specified properties file first from the
 * file system. If the file is not found in the file system, it then tries to find the file within the application's
 * JAR archive.
 *
 * This dual-loading strategy ensures that the application can access its configuration in various deployment
 * environments, making it easier to manage and deploy across different setups without modifying the codebase.
 */
public class PropertiesLoader {

    private static final Logger LOGGER = Logger.getLogger(PropertiesLoader.class.getName());

    /**
     * Loads a properties file by name. First attempts to load the file from the file system.
     * If the file is not found, attempts to load it from within the application's JAR.
     *
     * @param fileName The name (and possibly path) of the properties file to be loaded.
     * @return A {@link Properties} object containing the key-value pairs from the properties file.
     *         Returns {@code null} if the file cannot be found or loaded from both the file system and JAR.
     */
    public static Properties loadProperties(String fileName) {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(fileName)) {
            // Loads the properties file from the file system
            prop.load(input);
            LOGGER.log(Level.INFO, "Properties file {0} successfully loaded from the file system.", fileName);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Properties file " + fileName + " not found in the file system. Searching in the JAR...", ex);

            // If the file is not found in the file system, attempt to load from the JAR
            try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName)) {
                if (input != null) {
                    prop.load(input);
                    LOGGER.log(Level.INFO, "Properties file {0} successfully loaded from the JAR.", fileName);
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
