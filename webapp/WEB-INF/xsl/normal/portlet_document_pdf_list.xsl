<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="site-path" select="site-path" />
<xsl:variable name="portlet-id" select="portlet/portlet-id" />

<xsl:template match="portlet">
	<div class="portlet-background-colored append-bottom" >
        <xsl:if test="not(string(display-portlet-title)='1')">
			<h3 class="portlet-background-colored-header -lutece-border-radius-top">
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
			</h3>
        </xsl:if>
		<div class="portlet-background-content -lutece-border-radius-bottom" >
		     <ul>
         	       <xsl:apply-templates select="document-list-portlet/document" />
             </ul>  
		</div>
	</div>
</xsl:template>


<xsl:template match="document">      
<xsl:if test="not(string(document-xml-content)='null')">
   <!-- <li>
        <a href="{$site-path}?document_id={document-id}&amp;portlet_id={$portlet-id}" target="_top">      
             <xsl:value-of select="document-xml-content/pdf/document-title" />   
        </a>
		<br />
		<xsl:value-of select="document-xml-content/pdf/document-summary" />   
	
    </li>-->
    <li>
        <a href="document?id={document-id}&amp;id_attribute=48" target="_blank">      
        	<xsl:for-each select="descendant::*">
                <xsl:apply-templates select="file-resource" />
           </xsl:for-each>  
          	<xsl:for-each select="descendant::*">
                <xsl:value-of select="document-title" />
           </xsl:for-each>  
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
	    <xsl:for-each select="descendant::*">
        	<xsl:value-of select="document-summary" />
        </xsl:for-each>
    </li>	
</xsl:if>
</xsl:template>              

<xsl:template match="file-resource">
	<xsl:choose>
		<xsl:when test="(resource-content-type='application/pdf')" >
			<img src="images/local/skin/plugins/document/filetypes/pdf.png" border="0" alt="" width="32" height="32" />
		</xsl:when>

		<xsl:otherwise>
		   <img src="images/local/skin/plugins/document/filetypes/file.png" border="0" alt="" width="32" height="32"/>
		</xsl:otherwise>        
	</xsl:choose>
	</xsl:template>
</xsl:stylesheet>

