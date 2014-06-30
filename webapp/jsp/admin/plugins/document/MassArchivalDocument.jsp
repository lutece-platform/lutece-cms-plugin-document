<%@page import="fr.paris.lutece.portal.web.pluginaction.IPluginActionResult"%>
<jsp:useBean id="document" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentJspBean" />

<% 
    document.init( request, document.RIGHT_DOCUMENT_MANAGEMENT ); 
	IPluginActionResult result =  document.getMassArchivalDocument( request );
	if ( result.getRedirect(  ) != null )
	{
		response.sendRedirect( result.getRedirect(  ) );
	}
	else if ( result.getHtmlContent(  ) != null )
	{
%>
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= result.getHtmlContent(  ) %>

<%@ include file="../../AdminFooter.jsp" %>
<%
	}
%>
