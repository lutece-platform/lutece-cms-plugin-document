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

	<div class="portlet-background-colored  {$device_class} append-bottom" >
        <xsl:if test="not(string(display-portlet-title)='1')">
			<h3 class="portlet-background-colored-header -lutece-border-radius-top">
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
			</h3>
        </xsl:if>
        
		<div class="portlet-background-content -lutece-border-radius-bottom" >
			
			<script type="text/javascript">
				<![CDATA[
			      function displayId(baliseId)
				  {
				  
				  if (document.getElementById(baliseId) != null)
				    {
				    	document.getElementById(baliseId).style.visibility='visible';
				    	document.getElementById(baliseId).style.display='block';
				    }
				  }
				
				function hideId(baliseId)
				  {
				  
				  if (document.getElementById(baliseId) != null)
				    {
				   		document.getElementById(baliseId).style.visibility='hidden';
				    	document.getElementById(baliseId).style.display='none';
				    }
				  }
				  
				  ]]>
		   	</script>
			
			<ul>	
			<!-- Tri la liste d'acteurs par service dans l'ordre alphabétique -->
			<xsl:apply-templates select="document-list-portlet/document/document-xml-content/actor">
		       <xsl:sort select="actor-service" order="ascending"/>
		    </xsl:apply-templates>
			</ul>
		</div>            	
	</div>
</xsl:template>


<xsl:template match="actor">

     <xsl:call-template name="sortieNoeuds"/>

</xsl:template>

<xsl:template name="sortieNoeuds">
	<xsl:variable name="indiceNoeud" select="position()"/>
	<xsl:variable name="current"><xsl:value-of select="actor-service"/></xsl:variable>
	<!-- On récupère le noeud suivant dans la liste d'actor triée -->
	<xsl:variable name="suivant">
		<xsl:if test="not(position()=last())">
			<xsl:call-template name="noeudSuivant">
				<xsl:with-param name="indiceNoeudSuivant" select="$indiceNoeud + 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>
	
	<!-- On récupère le noeud précédent dans la liste d'actor triée -->
	<xsl:variable name="precedent">
		<xsl:if test="position() != 1">
			<xsl:call-template name="noeudSuivant">
				<xsl:with-param name="indiceNoeudSuivant" select="$indiceNoeud - 1"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:variable>
	
	<!-- on affiche le service et l'acteur  -->
	<xsl:call-template name="display">
			<xsl:with-param name="current" select="$current"/>
			<xsl:with-param name="precedent" select="$precedent"/>
			<xsl:with-param name="suivant" select="$suivant"/>
		</xsl:call-template>
	
</xsl:template>
        
<xsl:template name="noeudSuivant">
	<xsl:param name="indiceNoeudSuivant" select="0"/>
	<xsl:for-each select="/portlet/document-list-portlet/document/document-xml-content/actor">
		<xsl:sort select="actor-service" order="ascending"/>
		<xsl:if test="position()=$indiceNoeudSuivant">
			<xsl:value-of select="actor-service"/>
		</xsl:if>
	</xsl:for-each>
</xsl:template> 

<xsl:template name="display">
	<xsl:param name="current"/>
	<xsl:param name="precedent"/>
	<xsl:param name="suivant"/>
	

	<xsl:if test="not(string($current) = '')">
	
	<xsl:choose>
		<!-- Si le service n'a qu'un seul acteur -->
	  <xsl:when test="string($precedent) != string($current) and string($suivant) != string($current)">
	    
	    <li style="list-style:none;" name="">
			<!-- on affiche le service  -->
			<h2>
	           <a href="javascript:displayId('{$current}');"><img src="images/admin/skin/plus.gif" alt=""/></a>
	           <a href="javascript:hideId('{$current}');"><img src="images/admin/skin/minus.gif" alt=""/></a>
	           <xsl:value-of disable-output-escaping="yes" select="actor-service" />
	       </h2>
	       
	       <div style="visibility: hidden; display: none;" id="{$current}">
	       	<!-- on affiche l'acteur  -->
	       	<ul>
	       		<xsl:call-template name="nameactor"/>
	       	</ul>
	       	&#160;
	       </div>
		</li>
	  </xsl:when>
	  <!-- Si le service a plusieur acteur et que l'on est sur le premier acteur -->
	  <xsl:when test="string($precedent) != string($current) and string($suivant) = string($current)">
			
			<xsl:text disable-output-escaping="yes">&lt;</xsl:text>li style="list-style:none;" name=""<xsl:text disable-output-escaping="yes" >&gt;</xsl:text>
	    	<!-- on affiche le service  -->
			<h2>
	           <a href="javascript:displayId('{$current}');"><img src="images/admin/skin/plus.gif" alt=""/></a>
	           <a href="javascript:hideId('{$current}');"><img src="images/admin/skin/minus.gif" alt=""/></a>
	          
	           <xsl:value-of disable-output-escaping="yes" select="actor-service" />
	        </h2>
	        <!-- On ouvre la liste d'acteur du service -->
	        <xsl:text  disable-output-escaping="yes" >&lt;</xsl:text>div style="visibility: hidden; display: none;" id="<xsl:value-of select="$current"/>"<xsl:text  disable-output-escaping="yes" >&gt;</xsl:text>
	       		<xsl:text  disable-output-escaping="yes" >&lt;</xsl:text>ul<xsl:text  disable-output-escaping="yes" >&gt;</xsl:text>
		       		<!-- on affiche le l'acteur  -->
		       		<xsl:call-template name="nameactor"/>
	       		
			
	  </xsl:when>
	  <!-- Si le service a plusieur acteur et que l'on est sur le dernier acteur -->
	  <xsl:when test="string($precedent) = string($current) and string($suivant) != string($current)">
	  			
	  			<!-- on affiche l'acteur  -->
	  			<xsl:call-template name="nameactor"/>
	  		<!-- On ferme la liste d'acteur du service -->
	  		<xsl:text  disable-output-escaping="yes" >&lt;</xsl:text>/ul<xsl:text  disable-output-escaping="yes" >&gt;</xsl:text>
			&#160;
			<xsl:text  disable-output-escaping="yes" >&lt;</xsl:text>/div<xsl:text  disable-output-escaping="yes" >&gt;</xsl:text>
		<xsl:text  disable-output-escaping="yes" >&lt;</xsl:text>/li<xsl:text  disable-output-escaping="yes" >&gt;</xsl:text>
	  </xsl:when>
	  <!-- Si le service a plusieur acteur et que l'on est sur un acteur en millieu de liste -->
	  <xsl:when test="string($precedent) = string($current) and string($suivant) = string($current)">
				<!-- on affiche l'acteur  -->
	       		<xsl:call-template name="nameactor"/>

	  </xsl:when>
	  <xsl:otherwise>
	  
	  </xsl:otherwise>
	</xsl:choose>
	</xsl:if>
</xsl:template>

<!-- affiche le nom et le prénom de l'acteur -->
<xsl:template name="nameactor">

		<li style="list-style:none;">
       		<xsl:variable name="document-id" select="actor-photo/file-resource/resource-document-id"/>
       		<img src="images/admin/skin/square.gif" alt=""/>
			<a href="{$site-path}?document_id={$document-id}&#38;portlet_id={$portlet-id}" target="_top">      
				<xsl:value-of disable-output-escaping="yes" select="actor-firstname" />
				&#160;
				<xsl:value-of disable-output-escaping="yes" select="actor-lastname" />
			</a>
			<br/>
			&#160;&#160;<strong> Fonction :</strong><xsl:value-of disable-output-escaping="yes" select="actor-function"/><br/>
			&#160;&#160;<strong> Téléphone : </strong><xsl:value-of disable-output-escaping="yes" select="actor-telephone"/><br/>
		</li>

</xsl:template>
        
</xsl:stylesheet> 
