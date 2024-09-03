<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="library">
        <statistics>
            <average_price>
                <xsl:value-of select="sum(book/price) div count(book)"/>
            </average_price>
            <total_books>
                <xsl:value-of select="sum(book/quantity)"/>
            </total_books>
        </statistics>
    </xsl:template>

</xsl:stylesheet>
