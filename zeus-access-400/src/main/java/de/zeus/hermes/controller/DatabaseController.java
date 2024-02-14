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
 * Manages database interactions and the export process, converting query results into various file formats.
 * Utilizes configurations from the Config class for database connections, managed by DatabaseManager, ensuring a single,
 * reusable connection. Supports converting SQL query results into multiple formats (XML, JSON, CSV, HTML, and Markdown)
 * using XSLT transformations, with an emphasis on local filesystem resource management.
 */
public class DatabaseController {

    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());
    private final DatabaseManager dbManager = new DatabaseManager();
    private final Config config = Config.getInstance();
    private Connection connection = null;

    /**
     * Connects to the database using parameters from Config. Maintains a single active connection.
     */
    public void connectToDatabase() {
        if (connection == null) {
            connection = dbManager.getDatabaseConnection(config.getDriver(), config.getDatabaseUrl(), config.getUsername(), config.getPassword());
            if (connection != null) {
                LOGGER.log(Level.INFO, "Database connection established successfully.");
            }
        }
    }

    /**
     * Disconnects the current database connection, releasing resources.
     */
    public void disconnectFromDatabase() {
        if (connection != null) {
            dbManager.closeDatabaseConnection(connection);
            LOGGER.log(Level.INFO, "Database connection closed.");
            connection = null;
        }
    }

    /**
     * Executes the configured SQL query, exports results to XML, and transforms them into specified formats.
     * Demonstrates flexible data handling and export capabilities.
     */
    public void runSqlToExportProcess() {
        try {
            connectToDatabase();
            String query = config.getQuery();
            PreparedStatement stmt = dbManager.getPreparedStmt(connection, query);
            ResultSet rs = stmt.executeQuery();
            Set<String> exportFormats = config.getExportFormats();
            String baseFileName = generateBaseFileName("export");
            // Export to XML initially
            XmlExporter.exportResultSetToXML(rs, "export", baseFileName + ".xml");
            // Process each configured format
            exportFormats.forEach(format -> {
                if (!"xml".equalsIgnoreCase(format)) {
                    transformAndExport(baseFileName, "xml", format.toLowerCase());
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred during the database operation.", e);
        } finally {
            disconnectFromDatabase();
        }
    }

    /**
     * Creates a base filename using the current timestamp and a specified identifier.
     *
     * @param rootNode Name for inclusion in the filename, enhancing traceability.
     * @return The generated base filename.
     */
    private String generateBaseFileName(String rootNode) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        return timestamp + "_" + rootNode;
    }

    /**
     * Transforms XML to a designated format using an XSLT file, focusing on filesystem searches.
     *
     * @param baseFileName Base name for both input and output files.
     * @param inputFormat Expected input format ('xml').
     * @param outputFormat Target format for the output.
     */
    private void transformAndExport(String baseFileName, String inputFormat, String outputFormat) {
        String inputFile = baseFileName + "." + inputFormat;
        String outputFile = baseFileName + "." + outputFormat;
        String xsltPath = findXsltPath("xml_to_" + outputFormat + ".xslt");
        if (xsltPath != null) {
            XMLtransformer.transformXML(inputFile, xsltPath, outputFile);
        } else {
            LOGGER.log(Level.WARNING, "XSLT path for format conversion not found: {0}", outputFormat);
        }
    }

    /**
     * Attempts to find an XSLT file in the filesystem, prioritizing the application's current directory.
     *
     * @param xsltFileName Name of the XSLT file to locate.
     * @return The file path if found, otherwise null.
     */
    private String findXsltPath(String xsltFileName) {
        File file = new File(xsltFileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            LOGGER.log(Level.WARNING, "XSLT file not found in the current directory: {0}", xsltFileName);
            return null;
        }
    }
}
