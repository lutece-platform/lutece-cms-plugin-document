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
			<h3><xsl:value-of disable-output-escaping="yes" select="portlet-name" /></h3>
        </xsl:if>
		<ul>
     	    <xsl:apply-templates select="document-list-portlet/document" />
     	</ul>  
	</div>
</xsl:template>

<xsl:template match="document">      
<xsl:if test="not(string(document-xml-content)='null')">
    <li>
        <a href="{$site-path}?document_id={document-id}&amp;portlet_id={$portlet-id}" target="_top">      
             <xsl:value-of select="document-xml-content/sound/sound-title" />   
       </a>
       	<xsl:if test="(string(resource-is-votable)='1')">
			<br />
        	<xsl:variable name="resource-score" select="resource-score" />
	        <img src="images/local/skin/plugins/rating/stars_{$resource-score}.png" alt="Score" title="Score" />
        </xsl:if>   
		<xsl:if test="(string(is-download-stat)='1')">
			<br />
			#i18n{rating.resource_vote.labelDownloadCount} : <xsl:value-of select="resource-download-stat" />
		</xsl:if>
       <br />
          <xsl:value-of select="document-xml-content/sound/sound-file" />
       <br />
          <xsl:value-of select="document-xml-content/sound/sound-author" />
   </li>       
</xsl:if>
</xsl:template>

</xsl:stylesheet>


