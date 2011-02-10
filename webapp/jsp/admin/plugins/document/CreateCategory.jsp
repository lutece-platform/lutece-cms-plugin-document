<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="category" scope="session" class="fr.paris.lutece.plugins.document.web.category.CategoryJspBean" />

<% category.init( request, category.RIGHT_CATEGORY_MANAGEMENT); %>
<%= category.getCreateCategory( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
