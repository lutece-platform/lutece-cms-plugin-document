<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentSearch" scope="session" class="fr.paris.lutece.plugins.document.web.docsearch.DocSearchJspBean" />

<% documentSearch.init( request, fr.paris.lutece.plugins.document.web.DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ); %>
<%= documentSearch.getSearch( request ) %>

<%@ include file="../../AdminFooter.jsp" %>