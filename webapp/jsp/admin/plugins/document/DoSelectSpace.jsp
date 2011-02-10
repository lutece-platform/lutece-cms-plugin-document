<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="documentRules" scope="session" class="fr.paris.lutece.plugins.document.web.rules.DocumentRulesJspBean" />
<% documentRules.init( request, documentRules.RIGHT_RULES_MANAGEMENT ); %>
<%  response.sendRedirect( documentRules.doSelectSpace( request ) ); %>
