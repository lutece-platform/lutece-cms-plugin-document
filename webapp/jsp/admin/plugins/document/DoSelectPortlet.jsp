<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean"%>

${ documentServiceJspBean.init( pageContext.request ) }
${ pageContext.response.sendRedirect( documentServiceJspBean.doSelectPortlet( pageContext.request ) ) }
