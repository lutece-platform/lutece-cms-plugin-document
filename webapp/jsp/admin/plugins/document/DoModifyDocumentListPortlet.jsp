<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.portlet.DocumentListPortletJspBean"%>

${ documentListPortletJspBean.init( pageContext.request, DocumentListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( documentListPortletJspBean.doModify( pageContext.request ) ) }


