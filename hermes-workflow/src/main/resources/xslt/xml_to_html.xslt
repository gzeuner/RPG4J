<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output to HTML with UTF-8 encoding and indentation for readability -->
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <!-- Embed CSS styles directly in the XSLT to format the table -->
    <xsl:template match="/">
        <html>
            <head>
                <style>
                    /* Table styling: full width, border collapsed, and with padding */
                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }
                    /* Styling for table headers and cells: borders, padding, and text alignment */
                    th, td {
                        border: 1px solid black;
                        padding: 8px;
                        text-align: left;
                    }
                    /* Background color for table headers */
                    th {
                        background-color: #f2f2f2;
                    }
                </style>
            </head>
            <body>
                <table>
                    <thead>
                        <tr>
                            <!-- Generate table headers from the names of the first 'row' child elements -->
                            <xsl:for-each select="/root/row[1]/*">
                                <th><xsl:value-of select="name()"/></th>
                            </xsl:for-each>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Generate table rows for each 'row' element -->
                        <xsl:for-each select="/root/row">
                            <tr>
                                <!-- Fill table cells with the values of each child element -->
                                <xsl:for-each select="*">
                                    <td><xsl:value-of select="."/></td>
                                </xsl:for-each>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>