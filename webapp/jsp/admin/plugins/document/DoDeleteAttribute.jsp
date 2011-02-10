<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="documentType" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean" />

<% 
    documentType.init( request, documentType.RIGHT_DOCUMENT_TYPES_MANAGEMENT ); 
    response.sendRedirect( documentType.doDeleteAttribute( request ) );
%>
