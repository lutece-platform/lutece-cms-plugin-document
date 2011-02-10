<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentType" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean" />

<% documentType.init( request, documentType.RIGHT_DOCUMENT_TYPES_MANAGEMENT ); %>
<%= documentType.getModifyDocumentType( request ) %>

<%@ include file="../../AdminFooter.jsp" %>