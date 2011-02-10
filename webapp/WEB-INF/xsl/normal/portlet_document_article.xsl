<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
		<div class="portlet-background-colored append-bottom">
			<xsl:if test="not(string(display-portlet-title)='1')">
				<h3 class="portlet-background-colored-header -lutece-border-radius-top">
					<xsl:value-of disable-output-escaping="yes"
						select="portlet-name" />
				</h3>
			</xsl:if>

			<xsl:apply-templates select="document-portlet/document" />
			<xsl:if test="string(document-portlet/document)=''">
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</xsl:if>

		</div>
	</xsl:template>

	<xsl:template match="document">
		<div class="portlet-background-content -lutece-border-radius-bottom">
			<xsl:output method="html" indent="yes" />
			<xsl:if test="not(string(document-xml-content)='null')">
				<xsl:apply-templates
					select="document-xml-content/article" />
			</xsl:if>
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
					<xsl:apply-templates
						select="article-attachment/file-resource" />
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</p>
		<p>
			<xsl:value-of disable-output-escaping="yes"
				select="article-body" />
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
						src="images/admin/skin/plugins/document/filetypes/file.png"
						border="0" />
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

