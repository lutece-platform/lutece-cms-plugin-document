<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean"%>

${ documentServiceJspBean.getSelectPortlet( pageContext.request ) }
