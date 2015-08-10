<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
	
		<xsl:variable name="device_class">
		<xsl:choose>
			<xsl:when test="string(display-on-small-device)='0'">hidden-xs</xsl:when>
			<xsl:when test="string(display-on-normal-device)='0'">hidden-sm</xsl:when>
			<xsl:when test="string(display-on-large-device)='0'">hidden-md</xsl:when>
			<xsl:when test="string(display-on-xlarge-device)='0'">hidden-lg</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		</xsl:variable>

		<div class="portlet {$device_class}">
			<xsl:if test="not(string(display-portlet-title)='1')">
				<h3>
					<xsl:value-of disable-output-escaping="yes"
						select="portlet-name" />
				</h3>
			</xsl:if>

			<xsl:apply-templates select="document-portlet/document" />
			<xsl:if test="string(document-portlet/document)=''">
				<xsl:text disable-output-escaping="yes">
					&amp;nbsp;
				</xsl:text>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="document">
		<xsl:output method="html" indent="yes" />
		<xsl:if test="not(string(document-xml-content)='null')">
			<xsl:apply-templates select="document-xml-content/shortarticle" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="shortarticle">
		<p><strong><xsl:value-of select="document-title" /></strong></p>
		<p><xsl:value-of disable-output-escaping="yes"	select="document-summary" /></p>
	</xsl:template>
</xsl:stylesheet>

