<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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

		<div class="{$device_class}">
			<xsl:if test="not(string(display-portlet-title)='1')">
				<h3 class="">
					<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
				</h3>
			</xsl:if>

			<div class="portlet-content">
				<xsl:apply-templates select="document-portlet/document/document-xml-content/article" />
			</div>

		</div>
	</xsl:template>

	<xsl:template match="article">
		<p>
			<strong>
				<xsl:value-of select="document-title" />
			</strong>
		</p>
		<p>
			<xsl:choose>
				<xsl:when test="article-attachment/file-resource!=''">
					<xsl:apply-templates select="article-attachment/file-resource" />
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</p>
		<p>
			<xsl:value-of disable-output-escaping="yes" select="article-body" />
		</p>
	</xsl:template>

	<xsl:template match="file-resource">
		<xsl:choose>
			<xsl:when
				test="(resource-content-type='image/jpeg' or resource-content-type='image/jpg' or  resource-content-type='image/pjpeg' or resource-content-type='image/gif' or resource-content-type='image/png')">
				<img
					src="servlet/plugins/document/resource?id={resource-document-id}&amp;id_attribute={resource-attribute-id}"
					align="right" />
			</xsl:when>
			<xsl:otherwise>
				<a
					href="servlet/plugins/document/resource?id={resource-document-id}&amp;id_attribute={resource-attribute-id}">
					<img
						src="images/local/skin/plugins/document/filetypes/file.png"
						border="0" />
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
