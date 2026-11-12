<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.web.category.CategoryJspBean"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>

${ categoryJspBean.init( pageContext.request, CategoryJspBean.RIGHT_CATEGORY_MANAGEMENT ) }
${ pageContext.setAttribute( 'strContent', categoryJspBean.getModifyCategory( pageContext.request ) ) }
<%
	// An unknown category gives the url of an error message: redirect to it before writing the page
	String strContent = (String) pageContext.getAttribute( "strContent" );
	if ( strContent.startsWith( AppPathService.getBaseUrl( request ) + "jsp/admin/AdminMessage.jsp" ) )
	{
		response.sendRedirect( strContent );
		return;
	}
%>
<jsp:include page="../../AdminHeader.jsp" />

${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../AdminFooter.jsp" %>
