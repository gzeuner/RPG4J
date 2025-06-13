<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <!-- Template für das Wurzelelement 'root' -->
    <xsl:template match="/root">
        <!-- CSV-Header aus den Namen der Kindelemente des ersten 'row' -->
        <xsl:for-each select="row[1]/*">
            <xsl:value-of select="name()"/>
            <xsl:if test="position() != last()">
                <xsl:text>;</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>
</xsl:text> <!-- Zeilenumbruch -->

        <!-- Verarbeite jedes 'row'-Element -->
        <xsl:apply-templates select="row"/>
    </xsl:template>

    <!-- Template für jedes 'row'-Element -->
    <xsl:template match="row">
        <xsl:for-each select="*">
            <!-- Prüfe, ob es sich um ein Datum handelt oder der Wert leer ist -->
            <xsl:choose>
                <!-- Datumswerte (angenommen: Format YYYY-MM-DD) mit Anführungszeichen -->
                <xsl:when test="string-length(.) = 10 and substring(., 5, 1) = '-' and substring(., 8, 1) = '-'">
                    <xsl:text>"</xsl:text>
                    <xsl:value-of select="."/>
                    <xsl:text>"</xsl:text>
                </xsl:when>
                <!-- Leere Werte mit "" -->
                <xsl:when test=". = ''">
                    <xsl:text>""</xsl:text>
                </xsl:when>
                <!-- Sonstige Werte ohne Anführungszeichen -->
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position() != last()">
                <xsl:text>;</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>
</xsl:text> <!-- Zeilenumbruch -->
    </xsl:template>
</xsl:stylesheet>