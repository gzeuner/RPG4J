<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output method to text -->
    <xsl:output method="text"/>

    <!-- Template matching the root 'export' element -->
    <xsl:template match="/export">
        <!-- Start the JSON array -->
        <xsl:text>[</xsl:text>
        <!-- Iterate over each 'data' child element -->
        <xsl:for-each select="data">
            <!-- Add a comma before every 'data' element except the first one -->
            <xsl:if test="position() != 1">,</xsl:if>
            <xsl:text>
{</xsl:text>
            <!-- Iterate over each 'property' child element within 'data' -->
            <xsl:for-each select="property">
                <!-- Add a comma before every 'property' element except the first one -->
                <xsl:if test="position() != 1">,</xsl:if>
                <xsl:text>
    "</xsl:text>
                <!-- Output the 'path' attribute value as the property name -->
                <xsl:value-of select="@path"/>
                <xsl:text>" : {"value": "</xsl:text>
                <!-- Output the 'value' element content -->
                <xsl:value-of select="value"/>
                <xsl:text>", "type": "</xsl:text>
                <!-- Output the 'type' attribute value -->
                <xsl:value-of select="@type"/>
                <xsl:text>"}</xsl:text>
            </xsl:for-each>
            <xsl:text>
}</xsl:text>
        </xsl:for-each>
        <!-- End the JSON array -->
        <xsl:text>
]</xsl:text>
    </xsl:template>
</xsl:stylesheet>
