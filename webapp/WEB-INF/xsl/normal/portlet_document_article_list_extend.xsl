<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:param name="target" select="target" />
	<xsl:param name="page-id" select="page-id" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hide-for-small</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>
	
		<div class="portlet  {$device_class} -lutece-border-radius append-bottom">
			<xsl:if test="not(string(display-portlet-title)='1')">
				<h3 id="article_{$portlet-id}">
					<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
				</h3><br />
			</xsl:if>
			<xsl:apply-templates select="document-list-portlet/document" />
			<xsl:text disable-output-escaping="yes">
				<![CDATA[<div class="clear">&#160;</div>]]>
			</xsl:text>
		</div>
	</xsl:template>

	<xsl:template match="document">
		<xsl:if test="not(string(document-xml-content)='null')">
			<div class="span-6 portlet -lutece-border-radius append-bottom">
				<a
					href="{$site-path}?document_id={document-id}&#38;portlet_id={$portlet-id} {$target}">
					<xsl:for-each select="descendant::*">
						<xsl:value-of select="document-title" />
					</xsl:for-each>
				</a>
                <br />
                <xsl:for-each select="descendant::*">
                    <xsl:value-of select="document-summary" />
                </xsl:for-each>
				<xsl:if test="document-rating">
					<br />
	        		<img src="images/local/skin/plugins/extend/modules/rating/stars_{document-rating}.png" alt="Score" title="Score" />
	        		(<xsl:value-of select="document-number-rating" />)
		        </xsl:if>   
                <xsl:if test="document-number-comment">
                    <br />
                    <xsl:value-of select="document-number-comment" /> commentaires
                </xsl:if>   
				<xsl:if test="document-number-hits">
					<br />
                    <xsl:value-of select="document-number-hits" /> vues
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
