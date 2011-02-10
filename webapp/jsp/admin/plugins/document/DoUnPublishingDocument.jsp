<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="documentPublishing" scope="session" class="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean" />

<%
    documentPublishing.init( request, documentPublishing.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( documentPublishing.doUnPublishingDocument( request ) );
%>
