<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentHistory" scope="session" class="fr.paris.lutece.plugins.document.web.history.DocumentHistoryJspBean" />

<% documentHistory.init( request, fr.paris.lutece.plugins.document.web.DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ); %>

<%= documentHistory.getHistory( request ) %>

<%@ include file="../../AdminFooter.jsp" %>