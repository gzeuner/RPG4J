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
 * The XmlExporter class is designed to facilitate the conversion of SQL query results from a ResultSet into an XML format.
 * This utility class provides a static method to export data efficiently, encapsulating the process of XML document creation,
 * element mapping, and file writing. It abstracts the complexity involved in transforming database query results into a structured
 * XML document, allowing for easy integration and usage within database interaction layers or services.
 *
 * Utilizing the DOM (Document Object Model) approach for XML creation, it ensures that the generated XML is well-formed and
 * adheres to standards. The class also sets up the necessary XML transformation properties to produce a neatly indented
 * and encoded output, enhancing the readability and usability of the generated XML files.
 *
 * Usage of this class simplifies the process of XML file generation from database results, making it a valuable tool for
 * applications requiring data exportation in XML format for reporting, data exchange, or integration purposes.
 */
public class XmlExporter {


    private static final Logger LOGGER = Logger.getLogger(XmlExporter.class.getName());

    public static void exportResultSetToXML(ResultSet rs, String rootNode, String fileName) {
        try {
            Document doc = createXMLFromResultSet(rs, rootNode);
            saveXMLToFile(doc, fileName);
            LOGGER.log(Level.INFO, "XML file {0} successfully created.", fileName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error when exporting ResultSet to XML.", e);
        }
    }

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
                Element value = doc.createElement("value");
                value.appendChild(doc.createTextNode(rs.getString(i)));
                property.appendChild(value);
                data.appendChild(property);
            }
        }
        return doc;
    }

    private static void saveXMLToFile(Document doc, String fileName) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(new java.io.File(fileName));
        transformer.transform(source, result);
    }
}
