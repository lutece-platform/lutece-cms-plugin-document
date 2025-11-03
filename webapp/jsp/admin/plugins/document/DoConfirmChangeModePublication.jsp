<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean"%>

${ documentPublishingJspBean.init( pageContext.request, DocumentPublishingJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( documentPublishingJspBean.doConfirmChangeModePublication( pageContext.request ) ) }
