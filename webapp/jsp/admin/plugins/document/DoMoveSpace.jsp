<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.spaces.DocumentSpaceJspBean"%>

${ documentSpaceJspBean.init( pageContext.request, DocumentSpaceJspBean.RIGHT_DOCUMENT_SPACE_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentSpaceJspBean.doMoveSpace( pageContext.request ) ) }
