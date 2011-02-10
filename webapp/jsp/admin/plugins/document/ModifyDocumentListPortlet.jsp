<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="documentListPortlet" scope="session" class="fr.paris.lutece.plugins.document.web.portlet.DocumentListPortletJspBean" />


<% documentListPortlet.init( request, documentListPortlet.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= documentListPortlet.getModify( request ) %>
