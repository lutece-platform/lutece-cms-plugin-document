<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.portlet.DocumentPortletJspBean"%>

${ documentPortletJspBean.init( pageContext.request, DocumentPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ documentPortletJspBean.getModify( pageContext.request ) }
