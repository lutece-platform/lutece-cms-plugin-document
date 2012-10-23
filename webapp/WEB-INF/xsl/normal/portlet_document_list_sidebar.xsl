<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="site-path" select="site-path" />
<xsl:variable name="portlet-id" select="portlet/portlet-id" />
    
<xsl:template match="portlet">

	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hide-for-small</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>

	<div class="boxed2 {$device_class}">
		<h2>	
			<xsl:if test="not(string(display-portlet-title)='1')">		
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />				
			</xsl:if>
			<xsl:if test="not(string(display-portlet-title)='0')">		
				&#160;
			</xsl:if>				
		</h2>		
        <xsl:apply-templates select="document-list-portlet" />
    </div>
</xsl:template>


<xsl:template match="document-list-portlet">
    <ul>
        <xsl:apply-templates select="document" />
    </ul>
</xsl:template>


<xsl:template match="document">
    <li>
        <a href="{$site-path}?document_id={document-id}&#38;portlet_id={$portlet-id}" target="_top">
            <xsl:for-each select="descendant::*">
                <xsl:value-of select="document-title" />
           </xsl:for-each>
	</a>           
           <br />
            <xsl:for-each select="descendant::*">
                <xsl:value-of select="document-summary" />
            </xsl:for-each>		
    </li>
</xsl:template>


</xsl:stylesheet>

