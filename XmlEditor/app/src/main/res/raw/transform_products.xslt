<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--    общей стоимости продуктов и средней стоимости продукта-->
    <!-- Identity template -->
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="product">
        <xsl:copy>
            <xsl:apply-templates select="name, price, quantity"/>
            <!-- Calculate total cost -->
            <total_cost>
                <xsl:value-of select="price * quantity"/>
            </total_cost>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/products">
        <summary>
            <xsl:apply-templates select="product"/>
            <overall_total_cost>
                <xsl:value-of select="sum(product/price * product/quantity)"/>
            </overall_total_cost>
            <average_cost_per_product>
                <xsl:call-template name="calculateAverage">
                    <xsl:with-param name="prices" select="product/price"/>
                </xsl:call-template>
            </average_cost_per_product>
        </summary>
    </xsl:template>

    <xsl:template name="calculateAverage">
        <xsl:param name="prices"/>
        <average>
            <xsl:value-of select="format-number(sum($prices) div count($prices), '0.##')"/>
        </average>
    </xsl:template>

</xsl:stylesheet>
