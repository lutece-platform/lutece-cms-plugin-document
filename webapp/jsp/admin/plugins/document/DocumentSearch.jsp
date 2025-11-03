<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean"%>

${ docSearchJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ docSearchJspBean.getSearch( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>