<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.document.service.DocumentTools" %>
<%@page import="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean"%>
${ documentTypeJspBean.init( pageContext.request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) }
<html>
<body>
<pre>
<%= DocumentTools.rebuildXmlContent( request ) %>
</pre>
</body>
</html>