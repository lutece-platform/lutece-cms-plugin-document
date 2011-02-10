<%@ page errorPage="../../../../ErrorPage.jsp" %>

<jsp:include page="../../../../AdminHeader.jsp" />

<jsp:useBean id="documentComment" scope="session" class="fr.paris.lutece.plugins.document.modules.comment.web.DocumentCommentJspBean" />

<% documentComment.init( request, documentComment.RIGHT_DOCUMENT_MANAGEMENT ); %>
<%= documentComment.getManageComments( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>