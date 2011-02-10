<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="site-path" select="site-path" />
<xsl:variable name="portlet-id" select="portlet/portlet-id" />
    
<xsl:template match="portlet">
	<div class="boxed2">
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
            <br />
			<br />				
			<xsl:if test="(string(document-is-commentable)='1')">
				<img src="images/local/skin/plugins/document/nb_comments.png" alt="Nombre de commentaires" title="Nombre de commentaires" align="middle"/>&#160;<xsl:value-of select="document-comment-nb" />      
			</xsl:if>   			
    </li>
</xsl:template>


</xsl:stylesheet>

