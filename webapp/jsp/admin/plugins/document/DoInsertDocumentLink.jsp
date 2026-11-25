<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean"%>

${ pageContext.response.sendRedirect( documentServiceJspBean.doInsertUrl( pageContext.request ) ) }
