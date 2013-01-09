<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="publication-date" select="publication-date" />
	<!--
	How to display the publication date :
	<xsl:value-of select="$publication-date" />
	-->
	
    <xsl:output method="html" indent="yes"/>

    <xsl:template match="content">
            <xsl:apply-templates select="actor"/>
    </xsl:template>
    
     <xsl:template match="actor">
        <p>
            <xsl:choose>
                <xsl:when test="actor-photo/file-resource!=''">
                      <img src="document?id={actor-photo/file-resource/resource-document-id}&amp;id_attribute={actor-photo/file-resource/resource-attribute-id}" align="left" valign="middle" hspace="20" />
                </xsl:when>
                <xsl:otherwise>               
               </xsl:otherwise>        
            </xsl:choose>
             <strong> <xsl:value-of select="actor-firstname" /> &#160; <xsl:value-of select="actor-lastname" /></strong>
        </p>
        <p>
                 Fonction : <xsl:value-of select="actor-function" />
        </p>
        <br />
        <br />
        <br />
        <br />
     </xsl:template>

 
   <xsl:template match="file-resource">
        <xsl:choose>
            <xsl:when test="(resource-content-type='image/jpeg' or resource-content-type='image/jpg' or  resource-content-type='image/pjpeg' or resource-content-type='image/gif' or resource-content-type='image/png')" >
                <img src="document?id={resource-document-id}&amp;id_attribute={resource-attribute-id}" align="right" />
            </xsl:when>
            <xsl:otherwise>
                <a href="document?id={resource-document-id}&amp;id_attribute={resource-attribute-id}"> 
                    <img src="images/admin/skin/plugins/document/filetypes/file.png" border="0" />
                </a>
            </xsl:otherwise>        
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>

 

