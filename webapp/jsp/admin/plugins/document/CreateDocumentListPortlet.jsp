<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.portlet.DocumentListPortletJspBean"%>

${ documentListPortletJspBean.init( pageContext.request, DocumentListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ documentListPortletJspBean.getCreate( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>