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
            <xsl:apply-templates select="document-list-portlet" />
		</div>
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
		<xsl:if test="(string(resource-is-votable)='1')">
			<br />
        	<xsl:variable name="resource-score" select="resource-score" />
	        <img src="images/local/skin/plugins/rating/stars_{$resource-score}.png" alt="Score" title="Score" />
        </xsl:if>   
           <br />
            <xsl:for-each select="descendant::*">
                <xsl:value-of select="document-summary" />
            </xsl:for-each>
            <br />
            <xsl:if test="(string(document-is-commentable)='1')">
                <img src="images/local/skin/plugins/document/nb_comments.png" alt="Nombre de commentaires" title="Nombre de commentaires" align="middle"/>&#160;<xsl:value-of select="document-comment-nb" />      
            </xsl:if>   
    </li>
</xsl:template>


</xsl:stylesheet>

