<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentPublishing" scope="session" class="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean" />

<% documentPublishing.init( request, documentPublishing.RIGHT_DOCUMENT_MANAGEMENT ); %>
<%= documentPublishing.getManageDocumentPublishing( request ) %>

<%@ include file="../../AdminFooter.jsp" %>