<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean"%>

${ documentPublishingJspBean.init( pageContext.request, DocumentPublishingJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ documentPublishingJspBean.getCreateAutoPublication( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>