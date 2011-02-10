<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="documentListPortlet" scope="session" class="fr.paris.lutece.plugins.document.web.portlet.DocumentListPortletJspBean" />

<%
    documentListPortlet.init( request, documentListPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect(  documentListPortlet.doCreate( request )  );
%>




