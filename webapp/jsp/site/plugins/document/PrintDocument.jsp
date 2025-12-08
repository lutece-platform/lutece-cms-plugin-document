<%@ page errorPage="../../ErrorPagePortal.jsp" %>
<jsp:include page="../../PortalHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.DocumentContentJspBean" %>

${ documentContentJspBean.getPrintDocumentPage( pageContext.request ) }