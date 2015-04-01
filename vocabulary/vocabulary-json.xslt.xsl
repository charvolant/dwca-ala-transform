<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
        >
    <xsl:output method="text"/>
    <xsl:variable name="tl" select="string-length(/rdf:RDF/rdf:Description[1]/@rdf:about)"/>

    <xsl:template match="/rdf:RDF">
{
        <xsl:for-each select="rdf:Description[1]"><xsl:call-template name="data"/></xsl:for-each>,
  "terms": [
        <xsl:apply-templates select="rdf:Description[@rdf:about != /rdf:RDF/rdf:Description[1]/@rdf:about]"/>
        ]
}
    </xsl:template>

    <xsl:template name="data">
        "name": "<xsl:value-of select="substring(@rdf:about, $tl + 1)"/>",
        <xsl:choose>
            <xsl:when test="dcterms:title">"title": "<xsl:call-template name="escapeQuote"><xsl:with-param name="pText" select="dcterms:title"/></xsl:call-template>",
            </xsl:when>
            <xsl:when test="rdfs:label">"title": "<xsl:call-template name="escapeQuote"><xsl:with-param name="pText" select="rdfs:label"/></xsl:call-template>",
            </xsl:when>
        </xsl:choose>
        <xsl:choose>
            <xsl:when test="rdfs:comment">"description": "<xsl:call-template name="escapeQuote"><xsl:with-param name="pText" select="rdfs:comment"/></xsl:call-template>",
            </xsl:when>
            <xsl:when test="dcterms:description">"description": "<xsl:call-template name="escapeQuote"><xsl:with-param name="pText" select="dcterms:description"/></xsl:call-template>",
            </xsl:when>
        </xsl:choose>
        "uri": "<xsl:value-of select="@rdf:about"/>"

    </xsl:template>

    <xsl:template name="escapeQuote">
        <xsl:param name="pText" select="."/>

        <xsl:if test="string-length($pText) >0">
            <xsl:value-of select="substring-before(concat($pText, '&quot;'), '&quot;')"/>

            <xsl:if test="contains($pText, '&quot;')">
                <xsl:text>\"</xsl:text>

                <xsl:call-template name="escapeQuote">
                    <xsl:with-param name="pText" select="substring-after($pText, '&quot;')"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template match="rdf:Description">
      {
        <xsl:call-template name="data"/>
      }<xsl:if test="position() != last()">,</xsl:if>
    </xsl:template>

</xsl:stylesheet>