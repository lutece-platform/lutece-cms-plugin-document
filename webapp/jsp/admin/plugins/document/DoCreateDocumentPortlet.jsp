<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.portlet.DocumentPortletJspBean"%>

${ documentPortletJspBean.init( pageContext.request, DocumentPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( documentPortletJspBean.doCreate( pageContext.request ) ) }




