<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean"%>

${ documentPublishingJspBean.init( pageContext.request, DocumentPublishingJspBean.RIGHT_DOCUMENT_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentPublishingJspBean.doPublishingDocument( pageContext.request ) ) }
