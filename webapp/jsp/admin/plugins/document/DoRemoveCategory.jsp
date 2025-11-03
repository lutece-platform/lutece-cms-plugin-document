<%@ page errorPage="../../ErrorPage.jsp"%>

<%@page import="fr.paris.lutece.plugins.document.web.category.CategoryJspBean"%>

${ categoryJspBean.init( pageContext.request, CategoryJspBean.RIGHT_CATEGORY_MANAGEMENT ) }
${ pageContext.response.sendRedirect( categoryJspBean.doRemoveCategory( pageContext.request ) ) }
