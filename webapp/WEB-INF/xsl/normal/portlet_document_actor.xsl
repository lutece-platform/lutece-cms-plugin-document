<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hide-for-small</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>
	
		<div class="portlet-background-colored  {$device_class} append-bottom">
			<xsl:if test="not(string(display-portlet-title)='1')">
				<h3 class="portlet-background-colored-header -lutece-border-radius-top">
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
		<div class="portlet-background-content -lutece-border-radius-bottom">
			<xsl:output method="html" indent="yes" />
			<xsl:if test="not(string(document-xml-content)='null')">
				<xsl:apply-templates
					select="document-xml-content/actor" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="actor">
		<p>
			<xsl:choose>
				<xsl:when test="actor-photo/file-resource!=''">
					<img
						src="document?id={actor-photo/file-resource/resource-document-id}&amp;id_attribute={actor-photo/file-resource/resource-attribute-id}"
						align="left" valign="middle" hspace="20" />
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
			<strong>
				<xsl:value-of select="actor-firstname" />
				&#160;
				<xsl:value-of select="actor-lastname" />
			</strong>
		</p>
		<p>
			Fonction :
			<xsl:value-of select="actor-function" />
		</p>
	</xsl:template>

	<xsl:template match="file-resource">
		<xsl:choose>
			<xsl:when
				test="(resource-content-type='image/jpeg' or resource-content-type='image/jpg' or  resource-content-type='image/pjpeg' or resource-content-type='image/gif' or resource-content-type='image/png')">
				<img
					src="document?id={resource-document-id}&amp;id_attribute={resource-attribute-id}"
					align="right" />
			</xsl:when>
			<xsl:otherwise>
				<a
					href="document?id={resource-document-id}&amp;id_attribute={resource-attribute-id}">
					<img
						src="images/local/skin/plugins/document/filetypes/file.png"
						border="0" />
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

