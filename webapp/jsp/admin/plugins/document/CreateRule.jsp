<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="documentRules" scope="session" class="fr.paris.lutece.plugins.document.web.rules.DocumentRulesJspBean" />

<% documentRules.init( request, documentRules.RIGHT_RULES_MANAGEMENT );
	documentRules.setRuleSession(null);
%>
<%= documentRules.getCreateRule( request ) %>

<%@ include file="../../AdminFooter.jsp" %>