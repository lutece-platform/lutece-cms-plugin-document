<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="fr.paris.lutece.portal.service.util.AppPathService" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="document" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentJspBean" />

<script type="text/javascript">
	 _site = "<%= AppPathService.getBaseUrl( request ) %>" 
</script>

<% document.init( request, document.RIGHT_DOCUMENT_MANAGEMENT ); %>
<%= document.getModifyDocument( request ) %>
<%@ include file="../../AdminFooter.jsp" %>
