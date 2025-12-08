<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean"%>

${ documentServiceJspBean.getSelectPage( pageContext.request ) }
