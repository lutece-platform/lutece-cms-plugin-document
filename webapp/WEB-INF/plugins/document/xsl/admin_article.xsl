<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" indent="yes"/>
    
    <xsl:template match="content">
        <xsl:apply-templates select="article"/>
    </xsl:template>
    
    <xsl:template match="article">
        <p>
            <strong>#i18n{document.document_admin_default_xsl.labelTitle} :  </strong>
            <xsl:value-of select="document-title" />
        </p>
        <p>
            <strong>#i18n{document.document_admin_default_xsl.labelSummary} :  </strong>
            <xsl:value-of select="document-summary" />
        </p>
        <p>
            <strong>Fichier : </strong> 
         </p>
         <p>   
            <xsl:choose>
                <xsl:when test="article-attachment/file-resource!=''">
                       <xsl:apply-templates select="article-attachment/file-resource" />
                </xsl:when>
                <xsl:otherwise>               
               </xsl:otherwise>        
            </xsl:choose>
        </p>        
	      <p>
              <xsl:value-of disable-output-escaping="yes" select="article-body" /> 
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
