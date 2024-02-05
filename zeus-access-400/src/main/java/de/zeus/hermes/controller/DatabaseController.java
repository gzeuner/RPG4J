package de.zeus.hermes.controller;

import de.zeus.hermes.util.Config;
import de.zeus.hermes.manager.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * DatabaseController is responsible for managing the database connection lifecycle within the application.
 * It utilizes the {@link DatabaseManager} to establish and close connections to the database based on the
 * configuration parameters defined in the {@link Config} class. This controller ensures that a single
 * instance of the database connection is maintained and used throughout the application, supporting
 * operations such as connecting to the database and disconnecting from it.
 *
 * The class provides methods to connect to the database using configuration parameters (driver, URL, username,
 * and password) and to safely close the connection when it is no longer needed. Logging is used to track the
 * success and failure of connection attempts, providing visibility into the database connection status.
 */
public class DatabaseController {

    private static final Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());
    private final DatabaseManager dbManager = new DatabaseManager();
    private final Config config = Config.getInstance();
    private Connection connection = null;

    // Establishes a database connection using the configurations specified in Config class.
    public void connectToDatabase() {
        if (connection == null) {
            connection = dbManager.getDatabaseConnection(config.getDriver(),
                    config.getDatabaseUrl(), config.getUsername(), config.getPassword());
            if (connection != null) {
                LOGGER.log(Level.INFO,"Successfully connected to the database.");
            }
        }
    }

    // Closes the database connection if it is currently established.
    public void disconnectFromDatabase() {
        if (connection != null) {
            dbManager.closeDatabaseConnection(connection);
            LOGGER.log(Level.INFO, "Database connection has been disconnected.");
            connection = null;
        }
    }
    public void runSqlToXmlProcess() {
        try {
            connectToDatabase();
            String query = config.getQuery();
            PreparedStatement stmt = dbManager.getPreparedStmt(connection, query);
            ResultSet rs = stmt.executeQuery();

            // Creates XML document from ResultSet
            Document doc = createXMLFromResultSet(rs, "export");

            // Saves the XML document in a file
            saveXMLToFile(doc, "export");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when executing the database operation", e);
        } finally {
            disconnectFromDatabase();
        }
    }

    private Document createXMLFromResultSet(ResultSet rs, String rootNode) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // Root-Element
        Element rootElement = doc.createElement(rootNode);
        doc.appendChild(rootElement);

        // Data from ResultSet
        while (rs.next()) {
            // Create XML Representation
            Element data = doc.createElement("data");
            rootElement.appendChild(data);

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                Element property = doc.createElement("property");
                property.setAttribute("path", rsmd.getColumnName(i));
                Element value = doc.createElement("value");
                value.appendChild(doc.createTextNode(rs.getString(i)));
                property.appendChild(value);
                data.appendChild(property);
            }
        }
        return doc;
    }

    private void saveXMLToFile(Document doc, String rootNode) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);

        // Create Filename with Timestamp
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String fileName = timestamp + "_" + rootNode + ".xml";

        StreamResult result = new StreamResult(new java.io.File(fileName));
        transformer.transform(source, result);

        LOGGER.log(Level.INFO, "XML file {0} successfully created.", fileName);
    }
}
