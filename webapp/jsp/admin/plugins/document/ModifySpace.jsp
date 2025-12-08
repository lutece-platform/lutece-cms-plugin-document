<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.spaces.DocumentSpaceJspBean"%>

${ documentSpaceJspBean.init( pageContext.request, DocumentSpaceJspBean.RIGHT_DOCUMENT_SPACE_MANAGEMENT ) }
${ documentSpaceJspBean.getModifySpace( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>