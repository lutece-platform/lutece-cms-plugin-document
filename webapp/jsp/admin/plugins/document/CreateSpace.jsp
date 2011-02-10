<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentSpace" scope="session" class="fr.paris.lutece.plugins.document.web.spaces.DocumentSpaceJspBean" />

<% documentSpace.init( request, documentSpace.RIGHT_DOCUMENT_SPACE_MANAGEMENT ); %>
<%= documentSpace.getCreateSpace( request ) %>

<%@ include file="../../AdminFooter.jsp" %>