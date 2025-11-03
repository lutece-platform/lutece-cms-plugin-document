<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean"%>

${ documentJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentJspBean.doRemoveSelection( pageContext.request ) ) }