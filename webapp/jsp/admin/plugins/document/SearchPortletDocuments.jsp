<%@ page errorPage="../../ErrorPage.jsp" contentType="application/json; charset=UTF-8" trimDirectiveWhitespaces="true" %>
<%@page import="fr.paris.lutece.plugins.document.web.portlet.DocumentPortletJspBean"%>
${ documentPortletJspBean.init( pageContext.request, DocumentPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ documentPortletJspBean.getSearchDocuments( pageContext.request ) }
