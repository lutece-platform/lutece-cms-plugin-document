<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.rules.DocumentRulesJspBean"%>

${ documentRulesJspBean.init( pageContext.request, DocumentRulesJspBean.RIGHT_RULES_MANAGEMENT ) }
${ pageContext.response.sendRedirect( documentRulesJspBean.doSelectSpace( pageContext.request ) ) }
