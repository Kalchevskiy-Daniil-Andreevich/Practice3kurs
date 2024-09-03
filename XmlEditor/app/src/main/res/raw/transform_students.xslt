<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <output>
            <students>
                <xsl:apply-templates select="//student"/>
            </students>
        </output>
    </xsl:template>

    <xsl:template match="student">
        <student>
            <name><xsl:value-of select="name"/></name>
            <averageGrade>
                <xsl:call-template name="calculateAverage">
                    <xsl:with-param name="grades" select="grades/subject/grade"/>
                </xsl:call-template>
            </averageGrade>
        </student>
    </xsl:template>

    <xsl:template name="calculateAverage">
        <xsl:param name="grades"/>
        <average>
            <xsl:value-of select="format-number(sum($grades) div count($grades), '0.##')"/>
        </average>
    </xsl:template>

</xsl:stylesheet>
