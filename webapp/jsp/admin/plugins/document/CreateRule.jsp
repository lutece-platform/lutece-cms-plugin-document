<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.rules.DocumentRulesJspBean"%>

${ documentRulesJspBean.init( pageContext.request, DocumentRulesJspBean.RIGHT_RULES_MANAGEMENT ) }
${ documentRulesJspBean.setRuleSession(null) }
${ documentRulesJspBean.getCreateRule( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>