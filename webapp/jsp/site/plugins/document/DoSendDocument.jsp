<%@ page errorPage="../../ErrorPagePortal.jsp" %>
<jsp:include page="../../PortalHeader.jsp" />

<jsp:useBean id="documentContent" scope="request" class="fr.paris.lutece.plugins.document.web.DocumentContentJspBean" />

<%
	response.sendRedirect( documentContent.doSendDocument( request ) );
%>