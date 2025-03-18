<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output method to text -->
    <xsl:output method="text"/>

    <!-- Template matching the root 'root' element -->
    <xsl:template match="/root">
        <!-- Start the JSON array -->
        <xsl:text>[</xsl:text>
        <!-- Iterate over each 'row' child element -->
        <xsl:for-each select="row">
            <!-- Add a comma before every 'row' element except the first one -->
            <xsl:if test="position() != 1">,</xsl:if>
            <xsl:text>
{</xsl:text>
            <!-- Iterate over each child element within 'row' -->
            <xsl:for-each select="*">
                <!-- Add a comma before every element except the first one -->
                <xsl:if test="position() != 1">,</xsl:if>
                <xsl:text>
    "</xsl:text>
                <!-- Output the element name as the property name -->
                <xsl:value-of select="name()"/>
                <xsl:text>": "</xsl:text>
                <!-- Output the element value -->
                <xsl:value-of select="."/>
                <xsl:text>"</xsl:text>
            </xsl:for-each>
            <xsl:text>
}</xsl:text>
        </xsl:for-each>
        <!-- End the JSON array -->
        <xsl:text>
]</xsl:text>
    </xsl:template>
</xsl:stylesheet>