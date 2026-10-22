<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.publishing.DocumentPublishingJspBean"%>

${ documentPublishingJspBean.init( pageContext.request, DocumentPublishingJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.setAttribute( 'strContent', documentPublishingJspBean.getCreateAutoPublication( pageContext.request, pageContext.response ) ) }

<jsp:include page="../../AdminHeader.jsp" />

${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../AdminFooter.jsp" %>