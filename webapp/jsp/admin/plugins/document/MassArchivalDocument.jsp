<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean"%>

${ documentJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ documentJspBean.getMassArchivalDocument( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
