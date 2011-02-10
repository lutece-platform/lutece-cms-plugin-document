<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.document.business.DocumentResource"%>
<jsp:useBean id="documentType" scope="session" class="fr.paris.lutece.plugins.document.web.DocumentTypeJspBean" />

<% 
    documentType.init( request, documentType.RIGHT_DOCUMENT_TYPES_MANAGEMENT ); 
    DocumentResource documentResource = documentType.getStyleSheetFile( request ) ;
    try
    {
        response.setHeader( "Content-Disposition", "attachment;filename=\"" + documentResource.getName() + "\"" );
        response.setContentType( documentResource.getContentType(  ) );
        response.getOutputStream(  ).write( documentResource.getContent(  ) );
    }
    catch( Exception e )
    {
        System.out.println( e );
    }
    out.clear(  );
    out = pageContext.pushBody(  );
%>
