<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="document" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentJspBean" />

<% document.init( request, document.RIGHT_DOCUMENT_MANAGEMENT ); %>
<%= document.getMoveDocument( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
