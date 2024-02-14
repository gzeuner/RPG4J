<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Set the output method to text with UTF-8 encoding -->
    <xsl:output method="text" encoding="UTF-8"/>
    <!-- Remove all whitespace nodes from the document for cleaner output -->
    <xsl:strip-space elements="*"/>

    <!-- Template matching the root 'export' element to generate the table -->
    <xsl:template match="/export">
        <xsl:text>|</xsl:text> <!-- Begin table header row with a border -->
        <!-- Iterate over each 'property' element of the first 'data' child to create header cells -->
        <xsl:for-each select="data[1]/property">
            <xsl:text> </xsl:text> <!-- Add space for padding -->
            <xsl:value-of select="@path"/> <!-- Insert the property name as a header cell -->
            <xsl:text> |</xsl:text> <!-- Close the header cell with a border -->
        </xsl:for-each>
        <xsl:text>&#10;|</xsl:text> <!-- Start a new line and begin the separator row -->
        <!-- Generate a separator row for the headers -->
        <xsl:for-each select="data[1]/property">
            <xsl:text> --- |</xsl:text> <!-- Separator for markdown table syntax -->
        </xsl:for-each>
        <xsl:text>&#10;</xsl:text> <!-- End the separator row with a new line -->
        <!-- Apply templates to each 'data' element to generate data rows -->
        <xsl:apply-templates select="data"/>
    </xsl:template>

    <!-- Template for generating data rows -->
    <xsl:template match="data">
        <xsl:text>|</xsl:text> <!-- Begin a data row with a border -->
        <!-- Iterate over each 'property' child to fill in data cells -->
        <xsl:for-each select="property">
            <xsl:text> </xsl:text> <!-- Add space for padding within the cell -->
            <xsl:value-of select="value"/> <!-- Insert the property value -->
            <xsl:text> |</xsl:text> <!-- Close the data cell with a border -->
        </xsl:for-each>
        <xsl:text>&#10;</xsl:text> <!-- End the data row with a new line -->
    </xsl:template>
</xsl:stylesheet>
