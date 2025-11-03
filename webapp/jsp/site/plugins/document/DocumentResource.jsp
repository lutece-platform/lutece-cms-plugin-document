
<%@ page errorPage="ErrorPagePortal.jsp" %>
<%@ page import="fr.paris.lutece.portal.web.LocalVariables" %>
<%@ page import="jakarta.el.ELException" %>
<%@ page pageEncoding="UTF-8" %>

<jsp:useBean id="documentResource" scope="request" class="fr.paris.lutece.plugins.document.web.ResourceJspBean" />

<%
	LocalVariables.setLocal(config, request, response);
	try
	{
		documentResource.loadResource(request);

		response.setHeader("Content-Disposition", "attachment;filename=\"Document\"");
		response.setContentType(documentResource.getContentType());
		response.getOutputStream().write(documentResource.getContent());
		response.flushBuffer();
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
	finally
	{
		LocalVariables.setLocal(null, null, null);
	}
%>