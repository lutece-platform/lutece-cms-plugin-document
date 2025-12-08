<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean"%>

${ documentTypeJspBean.init( pageContext.request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) }
${ documentTypeJspBean.getCreateDocumentType( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>