<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentJspBean"%>

${ documentJspBean.init( pageContext.request, DocumentJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ documentJspBean.getManageDocuments( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>