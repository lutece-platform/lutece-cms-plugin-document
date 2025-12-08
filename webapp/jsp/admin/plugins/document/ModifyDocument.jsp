<%@ page errorPage="../../ErrorPage.jsp" %>
<%@ page import="fr.paris.lutece.portal.service.util.AppPathService" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean" %>

<script type="text/javascript">
	 _site = "<%= AppPathService.getBaseUrl( request ) %>" 
</script>

${ documentJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ documentJspBean.getModifyDocument( pageContext.request ) }
<%@ include file="../../AdminFooter.jsp" %>