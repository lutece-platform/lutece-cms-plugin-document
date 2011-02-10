<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="publishing" scope="session" class="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean" />

<% 
	publishing.init( request, publishing.RIGHT_MANAGE_ADMIN_SITE ); 
    response.sendRedirect( publishing.doConfirmChangeModePublication( request ) );
%>
