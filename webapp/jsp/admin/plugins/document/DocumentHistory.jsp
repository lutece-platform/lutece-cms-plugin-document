<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean"%>

${ documentHistoryJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ documentHistoryJspBean.getHistory( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>