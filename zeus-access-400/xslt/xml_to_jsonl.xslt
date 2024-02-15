<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output method to text with UTF-8 encoding -->
    <xsl:output method="text" encoding="UTF-8"/>
    <!-- Remove all whitespace nodes from the document -->
    <xsl:strip-space elements="*"/>

    <!-- Template matching the root 'export' element -->
    <xsl:template match="/export">
        <!-- Loop through each 'data' element -->
        <xsl:for-each select="data">
            <xsl:text>{</xsl:text> <!-- Start of a JSON object -->
            <!-- Loop through each 'property' element within 'data' -->
            <xsl:for-each select="property">
                <!-- Add a comma before every 'property' element except the first one -->
                <xsl:if test="position() != 1">
                    <xsl:text>, </xsl:text>
                </xsl:if>
                <xsl:text>"</xsl:text> <!-- Start of the property name -->
                <xsl:value-of select="@path"/> <!-- The property name -->
                <xsl:text>": {"value": "</xsl:text> <!-- Start of the property object -->
                <xsl:value-of select="value"/> <!-- The property value -->
                <xsl:text>", "type": "</xsl:text> <!-- The property type -->
                <xsl:value-of select="@type"/>
                <xsl:text>"}</xsl:text> <!-- End of the property object -->
            </xsl:for-each>
            <xsl:text>}</xsl:text> <!-- End of the JSON object -->
            <!-- Add a newline character after each 'data' object except the last one -->
            <xsl:if test="position() != last()">
                <xsl:text>&#10;</xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
