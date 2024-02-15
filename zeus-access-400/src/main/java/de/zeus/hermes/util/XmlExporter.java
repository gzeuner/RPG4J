package de.zeus.hermes.util;

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
 * Provides functionality to export SQL query results from a ResultSet into XML format.
 * This utility class encapsulates the XML document creation, mapping ResultSet data to XML elements,
 * and writing the XML to a file. It simplifies the transformation of database query results into a structured XML document,
 * facilitating easy integration and utilization within database interaction workflows or services.
 *
 * The class employs the DOM (Document Object Model) methodology for XML creation, ensuring the produced XML is well-formed
 * and adheres to XML standards. It configures XML transformation properties to generate an output that is properly indented
 * and encoded, improving the readability and manageability of the resulting XML files.
 *
 * This class is instrumental for applications that need to export data in XML format for purposes such as reporting,
 * data exchange, or system integration.
 */
public class XmlExporter {

    private static final Logger LOGGER = Logger.getLogger(XmlExporter.class.getName());

    /**
     * Exports the given ResultSet to an XML file with the specified root node and file name.
     * It creates an XML document from the ResultSet, then saves this document to a file.
     *
     * @param rs The ResultSet containing the data to be exported.
     * @param rootNode The name of the root element in the generated XML document.
     * @param fileName The name (including path) of the XML file to be created.
     */
    public static void exportResultSetToXML(ResultSet rs, String rootNode, String fileName) {
        try {
            Document doc = createXMLFromResultSet(rs, rootNode);
            saveXMLToFile(doc, fileName);
            LOGGER.log(Level.INFO, "XML file {0} successfully created.", fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when exporting ResultSet to XML.", e);
        }
    }

    /**
     * Creates an XML document from a ResultSet. Each row in the ResultSet is transformed into an "data" element
     * with nested "property" elements representing each column.
     *
     * @param rs The ResultSet to transform into XML.
     * @param rootNode The root element name for the XML document.
     * @return A Document object representing the XML structure of the ResultSet.
     * @throws Exception If any error occurs during the XML document creation process.
     */
    private static Document createXMLFromResultSet(ResultSet rs, String rootNode) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement(rootNode);
        doc.appendChild(rootElement);

        while (rs.next()) {
            Element data = doc.createElement("data");
            rootElement.appendChild(data);

            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                Element property = doc.createElement("property");
                property.setAttribute("path", rsmd.getColumnName(i));
                property.setAttribute("type", getJdbcTypeName(rsmd.getColumnType(i)));
                Element value = doc.createElement("value");
                value.appendChild(doc.createTextNode(rs.getString(i)));
                property.appendChild(value);
                data.appendChild(property);
            }
        }
        return doc;
    }

    /**
     * Retrieves the JDBC type name for a given JDBC type code.
     *
     * @param jdbcType The JDBC type code.
     * @return The name of the JDBC type.
     */
    private static String getJdbcTypeName(int jdbcType) {
        try {
            for (java.lang.reflect.Field field : java.sql.Types.class.getFields()) {
                if (field.getInt(null) == jdbcType) {
                    return field.getName();
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Error when accessing JDBC type names", e);
        }
        return "UNKNOWN";
    }

    /**
     * Saves the given XML Document to a file.
     *
     * @param doc The XML Document to save.
     * @param fileName The file name (including path) where the XML Document should be saved.
     * @throws Exception If any error occurs during the process of saving the XML to a file.
     */
    private static void saveXMLToFile(Document doc, String fileName) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(
                new java.io.File(Config.getInstance().getExportPath() + "/" + fileName));
        transformer.transform(source, result);
    }
}
