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
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
			</h3>
        </xsl:if>
		<div>
		     <ul class="unstyled">        
                 <xsl:apply-templates select="document-list-portlet/document" />	
            </ul>  
		</div>            	
	</div>
</xsl:template>


<xsl:template match="document">      
    <li> 
        <xsl:if test="not(string(document-xml-content)='null')">
            <a href="{$site-path}?document_id={document-id}&#38;portlet_id={$portlet-id}" target="_top">      
                <xsl:apply-templates select="document-xml-content/image/image-file/file-resource" />
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
                <xsl:value-of disable-output-escaping="yes" select="document-xml-content/image/image-description" />
            <br />
                <xsl:value-of disable-output-escaping="yes" select="document-xml-content/image/image-credits" />
            <br />
        </xsl:if>
    </li>        
</xsl:template>              
	

<xsl:template match="file-resource">
	<xsl:choose>
		<xsl:when test="(resource-content-type='image/jpeg' or resource-content-type='image/jpg' or  resource-content-type='image/pjpeg' or resource-content-type='image/gif' or resource-content-type='image/png')">
			<img src="servlet/plugins/document/resource?id={resource-document-id}&amp;id_attribute={resource-attribute-id}" alt="" class="img-rounded" width="120" height="120"/>
		</xsl:when>
		<xsl:otherwise>
             <a href="servlet/plugins/document/resource?id={resource-document-id}&amp;id_attribute={resource-attribute-id}"> 
			   <img src="images/admin/skin/plugins/document/filetypes/file.png" class="img-rounded" alt="" width="120" height="120"/>
             </a>
		</xsl:otherwise>        
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
