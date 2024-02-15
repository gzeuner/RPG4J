<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
                            <!-- Generate table headers from 'path' attributes of the first 'data' group's properties -->
                            <xsl:for-each select="export/data[1]/property">
                                <th><xsl:value-of select="@path"/></th>
                            </xsl:for-each>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Generate table rows for each 'data' element -->
                        <xsl:for-each select="export/data">
                            <tr>
                                <!-- Fill table cells with the 'value' of each property -->
                                <xsl:for-each select="property">
                                    <td><xsl:value-of select="value"/></td>
                                </xsl:for-each>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
