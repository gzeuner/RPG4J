package de.zeus.hermes.controller;

import de.zeus.hermes.manager.DatabaseManager;
import de.zeus.hermes.util.Config;
import de.zeus.hermes.util.XMLtransformer;
import de.zeus.hermes.util.XmlExporter;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the database interactions and manages the export process of query results to various file formats.
 * This class handles the lifecycle of database connections using configurations provided by the {@link Config} class
 * and employs the {@link DatabaseManager} for connection management. It ensures a single, reusable connection is maintained
 * throughout the application's lifecycle.
 *
 * The class also facilitates the conversion of SQL query results into different formats (XML, JSON, CSV, HTML, and Markdown)
 * by leveraging XSLT transformations. It prioritizes finding XSLT files in the local filesystem before falling back to
 * resources within the application's JAR file, providing flexibility in resource management.
 */
public class DatabaseController {

    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());
    private final DatabaseManager dbManager = new DatabaseManager();
    private final Config config = Config.getInstance();
    private Connection connection = null;

    /**
     * Establishes a database connection using the configuration parameters specified in the {@link Config} class.
     * Ensures that only one connection is active at any time.
     */
    public void connectToDatabase() {
        if (connection == null) {
            connection = dbManager.getDatabaseConnection(config.getDriver(),
                    config.getDatabaseUrl(), config.getUsername(), config.getPassword());
            if (connection != null) {
                LOGGER.log(Level.INFO, "Successfully connected to the database.");
            }
        }
    }

    /**
     * Closes the database connection if it is currently established, ensuring resources are cleanly released.
     */
    public void disconnectFromDatabase() {
        if (connection != null) {
            dbManager.closeDatabaseConnection(connection);
            LOGGER.log(Level.INFO, "Database connection has been disconnected.");
            connection = null;
        }
    }

    /**
     * Executes a SQL query to fetch data, exports the result to XML, and then transforms and exports the XML
     * to various formats including JSON, JSON Lines, CSV, HTML, and Markdown. This process demonstrates the
     * application's ability to handle data transformation and exportation flexibly.
     */
    public void runSqlToExportProcess() {
        try {
            connectToDatabase();
            String query = config.getQuery();
            PreparedStatement stmt = dbManager.getPreparedStmt(connection, query);
            ResultSet rs = stmt.executeQuery();
            Set<String> exportFormats = config.getExportFormats();
            String baseFileName = generateBaseFileName("export");
            // Export base xml format
            XmlExporter.exportResultSetToXML(rs, "export", baseFileName + ".xml");
            // Process configured export formats
            exportFormats.forEach(format -> {
                // Ignore upper/lower case during format check
                if (!"xml".equalsIgnoreCase(format)) {
                    transformAndExport(baseFileName, "xml", format.toLowerCase());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when executing the database operation", e);
        } finally {
            disconnectFromDatabase();
        }
    }

    /**
     * Generates a base filename using the current timestamp and a specified root node name. This filename
     * serves as the foundation for all exported files.
     *
     * @param rootNode The root node name to be included in the filename.
     * @return A string representing the base filename.
     */
    private String generateBaseFileName(String rootNode) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return timestamp + "_" + rootNode;
    }

    /**
     * Transforms XML content to a specified format using an XSLT file and exports the result. The method
     * first attempts to locate the XSLT file in the filesystem. If not found, it tries to load it from
     * the application's JAR resources.
     *
     * @param baseFileName The base filename for the input and output files.
     * @param inputFormat The format of the input file (expected to be 'xml').
     * @param outputFormat The desired format of the output file.
     */
    private void transformAndExport(String baseFileName, String inputFormat, String outputFormat) {
        String inputFile = baseFileName + "." + inputFormat;
        String outputFile = baseFileName + "." + outputFormat;
        String xsltPath = findXsltPath("xml_to_" + outputFormat + ".xslt");
        XMLtransformer.transformXML(inputFile, xsltPath, outputFile);
    }

    /**
     * Attempts to find the path of an XSLT file, first in the filesystem and then within the JAR file.
     * This allows for flexible XSLT file management and supports development and deployment scenarios.
     *
     * @param xsltFileName The name of the XSLT file to locate.
     * @return The path to the XSLT file if found, or {@code null} if the file cannot be located.
     */
    private String findXsltPath(String xsltFileName) {
        try {
            String fileSystemPath = Paths.get("src/main/resources", xsltFileName).toString();
            File file = new File(fileSystemPath);
            if (file.exists()) {
                return fileSystemPath;
            } else {
                return getClass().getClassLoader().getResource(xsltFileName).toURI().getPath();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not find XSLT file in filesystem or JAR", e);
            return null;
        }
    }
}
