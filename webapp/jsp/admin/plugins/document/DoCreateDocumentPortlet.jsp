<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="documentPortlet" scope="session" class="fr.paris.lutece.plugins.document.web.portlet.DocumentPortletJspBean" />

<%
    documentPortlet.init( request, documentPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect(  documentPortlet.doCreate( request )  );
%>




