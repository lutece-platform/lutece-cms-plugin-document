<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="document" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentJspBean" />

<% 
    document.init( request, document.RIGHT_DOCUMENT_MANAGEMENT ); 
    response.sendRedirect( document.doSearchDocumentById( request ) );
%>
