<jsp:useBean id="documentResource" scope="request" class="fr.paris.lutece.plugins.document.web.ResourceJspBean" />

<%
        try
        {
            documentResource.loadResource( request );

            response.setHeader("Content-Disposition", "attachment;filename=\"Document\"");
            response.setContentType( documentResource.getContentType() );
            response.getOutputStream(  ).write( documentResource.getContent() );
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        out.clear();
        out = pageContext.pushBody();
%>

