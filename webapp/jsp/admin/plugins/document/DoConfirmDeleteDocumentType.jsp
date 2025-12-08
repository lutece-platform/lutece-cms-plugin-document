<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean"%>

${ documentTypeJspBean.init( pageContext.request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentTypeJspBean.doConfirmDelete( pageContext.request ) ) }