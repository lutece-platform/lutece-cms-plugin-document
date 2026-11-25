<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean"%>

${ documentAdminDashboardJspBean.init( pageContext.request, DocumentTypeJspBean.RIGHT_DOCUMENT_TYPES_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentAdminDashboardJspBean.doModifyAdvancedParameters( pageContext.request ) ) }
