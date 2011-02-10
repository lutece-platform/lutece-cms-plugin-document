<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="documentPortlet" scope="session" class="fr.paris.lutece.plugins.document.web.portlet.DocumentPortletJspBean" />


<% documentPortlet.init( request, documentPortlet.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= documentPortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
