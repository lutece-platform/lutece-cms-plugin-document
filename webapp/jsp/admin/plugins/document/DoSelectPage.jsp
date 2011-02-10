<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="documentServiceJspBean" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentServiceJspBean" />

<% response.sendRedirect( documentServiceJspBean.doSelectPage( request ) );%>
