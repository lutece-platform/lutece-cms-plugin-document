<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean"%>

${ pageContext.setAttribute( 'strContent', documentServiceJspBean.getSelectPage( pageContext.request, pageContext.response ) ) }

<jsp:include page="../../insert/InsertServiceHeader.jsp" />

${ pageContext.getAttribute( 'strContent' ) }
