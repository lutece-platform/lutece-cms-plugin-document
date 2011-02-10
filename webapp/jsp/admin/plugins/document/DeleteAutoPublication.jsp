<%@ page errorPage="../../ErrorPage.jsp"%>

<jsp:useBean id="documentAutoPublication" scope="session" class="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean" />

<%
	documentAutoPublication.init(request, documentAutoPublication.RIGHT_MANAGE_ADMIN_SITE);
	response.sendRedirect(documentAutoPublication.getConfirmDeleteAutoPublication(request));
%>



