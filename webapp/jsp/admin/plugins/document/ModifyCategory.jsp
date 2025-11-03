<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.document.web.category.CategoryJspBean"%>

${ categoryJspBean.init( pageContext.request, CategoryJspBean.RIGHT_CATEGORY_MANAGEMENT ) }
${ categoryJspBean.getModifyCategory( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
