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

	<div class="portlet-background-colored {$device_class} append-bottom" >
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
    <li>
        <a href="document?id={document-id}&amp;id_attribute=48" target="_blank">      
        	<xsl:for-each select="descendant::*">
                <xsl:apply-templates select="file-resource" />
           </xsl:for-each>  
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

