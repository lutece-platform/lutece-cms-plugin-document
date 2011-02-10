<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
    
    <xsl:template match="metadata">
    	<xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="title">
    	<meta name="DC.Title" content="{.}" />
    </xsl:template>
    <xsl:template match="creator">
    	<meta name="DC.Creator" content="{.}" />
    </xsl:template>
    <xsl:template match="subject">
    	<meta name="DC.Subject" content="{.}" />
    </xsl:template>
    <xsl:template match="description">
    	<meta name="DC.Description" content="{.}" />
    </xsl:template>
    <xsl:template match="publisher">
    	<meta name="DC.Publisher" content="{.}" />
    </xsl:template>
    <xsl:template match="contributor">
    	<meta name="DC.Contributor" content="{.}" />
    </xsl:template>
    <xsl:template match="date">
    	<meta name="DC.Date" content="{.}" />
    </xsl:template>
    <xsl:template match="type">
    	<meta name="DC.Type" content="{.}" />
    </xsl:template>
    <xsl:template match="format">
    	<meta name="DC.Format" content="{.}" />
    </xsl:template>
    <xsl:template match="identifier">
    	<meta name="DC.Identifier" content="{.}" />
    </xsl:template>
    <xsl:template match="source">
    	<meta name="DC.Source" content="{.}" />
    </xsl:template>
    <xsl:template match="language">
    	<meta name="DC.Language" content="{.}" />
    </xsl:template>
    <xsl:template match="relation">
    	<meta name="DC.Relation" content="{.}" />
    </xsl:template>
    <xsl:template match="coverage">
    	<meta name="DC.Coverage" content="{.}" />
    </xsl:template>
    <xsl:template match="rights">
    	<meta name="DC.Rights" content="{.}" />
    </xsl:template>
    
</xsl:stylesheet>
