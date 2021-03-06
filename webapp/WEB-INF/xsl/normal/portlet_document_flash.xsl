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
		<div>
			<xsl:output method="html" indent="yes" />
			<xsl:if test="not(string(document-xml-content)='null')">
				<xsl:apply-templates
					select="document-xml-content/flash" />
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="flash">
		<h1><xsl:value-of select="document-title" /></h1>
		<p>
			<xsl:value-of select="document-summary" />
		</p>
		<p>
			<xsl:choose>
				<xsl:when test="(flash-object/file-resource/resource-content-type='application/x-shockwave-flash')">
					<object 
						data="servlet/plugins/document/resource?id={flash-object/file-resource/resource-document-id}&amp;id_attribute={flash-object/file-resource/resource-attribute-id}" 
						type="application/x-shockwave-flash" 
						width="{flash-width}" 
						height="{flash-height}"
						align="{flash-align}"
						classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
						codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0"
					>
						<param name="Autostart" value="{flash-autostart}" />
						<param name="Quality" value="{flash-quality}" />
						<param name="Loop" value="{flash-loop}" />
						<param name="menu" value="{flash-menu}" />
						<param name="allowScriptAccess" value="samedomain" />
						<param name="movie" value="servlet/plugins/document/resource?id={flash-object/file-resource/resource-document-id}&amp;id_attribute={flash-object/file-resource/resource-attribute-id}" />
						<EMBED 
							src="servlet/plugins/document/resource?id={flash-object/file-resource/resource-document-id}&amp;id_attribute={flash-object/file-resource/resource-attribute-id}" 
							type="application/x-shockwave-flash"
							width="{flash-width}" 
							height="{flash-height}"
							align="{flash-align}"
							quality="{flash-quality}"
							loop="{flash-loop}"
							menu="{flash-menu}"
							allowScriptAccess="samedomain"
							pluginspace="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash">
						</EMBED>
					</object>
				</xsl:when>
			</xsl:choose>
		</p>
		<blockquote>
			<xsl:value-of disable-output-escaping="yes" select="flash-credits" />
		</blockquote>
	</xsl:template>
	
</xsl:stylesheet>
