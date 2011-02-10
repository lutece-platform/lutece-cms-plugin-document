<%@ page errorPage="../../ErrorPagePortal.jsp" %>
<jsp:include page="../../PortalHeader.jsp" />

<jsp:useBean id="documentContent" scope="page" class="fr.paris.lutece.plugins.document.web.DocumentContentJspBean" />

<%= documentContent.getSendDocumentPage( request ) %>
