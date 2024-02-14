<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Specify the output format as text with UTF-8 encoding -->
    <xsl:output method="text" encoding="UTF-8"/>
    <!-- Remove whitespace nodes from all elements -->
    <xsl:strip-space elements="*"/>

    <!-- Template matching the root 'export' element to generate CSV headers -->
    <xsl:template match="/export">
        <!-- Generate CSV header line based on 'path' attributes of the first 'data' group's properties -->
        <xsl:for-each select="data[1]/property">
            <xsl:value-of select="@path"/>
            <!-- Separate header values with semicolons, except the last one -->
            <xsl:if test="position() != last()">
                <xsl:text>;</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <!-- New line after the header -->
        <xsl:text>&#10;</xsl:text>

        <!-- Output the types in the second line -->
        <xsl:for-each select="data[1]/property">
            <xsl:value-of select="@type"/>
            <!-- Separate type values with semicolons, except the last one -->
            <xsl:if test="position() != last()">
                <xsl:text>;</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <!-- New line after the types -->
        <xsl:text>&#10;</xsl:text>

        <!-- Apply templates to each 'data' element -->
        <xsl:apply-templates select="data"/>
    </xsl:template>

    <!-- Template to generate data rows for each 'data' element (starting from the third line) -->
    <xsl:template match="data">
        <xsl:for-each select="property">
            <xsl:value-of select="value"/>
            <!-- Separate property values with semicolons, except the last one -->
            <xsl:if test="position() != last()">
                <xsl:text>;</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <!-- New line after each data row -->
        <xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>
