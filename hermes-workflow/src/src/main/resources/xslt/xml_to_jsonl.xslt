<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output method to text with UTF-8 encoding -->
    <xsl:output method="text" encoding="UTF-8"/>
    <!-- Remove all whitespace nodes from the document -->
    <xsl:strip-space elements="*"/>

    <!-- Template matching the root 'root' element -->
    <xsl:template match="/root">
        <!-- Loop through each 'row' element -->
        <xsl:for-each select="row">
            <xsl:text>{</xsl:text> <!-- Start of a JSON object -->
            <!-- Loop through each child element within 'row' -->
            <xsl:for-each select="*">
                <!-- Add a comma before every element except the first one -->
                <xsl:if test="position() != 1">
                    <xsl:text>, </xsl:text>
                </xsl:if>
                <xsl:text>"</xsl:text> <!-- Start of the property name -->
                <xsl:value-of select="name()"/> <!-- The property name -->
                <xsl:text>": "</xsl:text> <!-- Start of the property value -->
                <xsl:value-of select="."/> <!-- The property value -->
                <xsl:text>"</xsl:text> <!-- End of the property value -->
            </xsl:for-each>
            <xsl:text>}</xsl:text> <!-- End of the JSON object -->
            <!-- Add a newline character after each 'row' object except the last one -->
            <xsl:if test="position() != last()">
                <xsl:text>
</xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>