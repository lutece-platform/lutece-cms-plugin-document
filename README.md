![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=cms-plugin-document-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)

# Document Plugin

## Introduction

The Document plugin is the content management application of a Lutece portal.

Its main features are :


 
* Dynamic creation of structured documents. A type of document is created by defining different attributes: simple or enriched text fields, dates, multimedia objects, attachments, values to choose from a list, ...
* The life cycle of the documents (creation, validation, publication, archiving, ...) takes place within filing spaces organized like directories.The operations on the documents and spaces are subject to access rights that can be fully parameterized and integrated in the security management of Lutece.
* A rules engine allows to dynamically create document management operations such as moving spaces or notifications to mailing lists by email.
* The documents are also categorizable on the basis of a thesaurus and can benefit from an extensible metadata system (Dublin Core or other).
* For publishing, the plugin also provides two portlets allowing to publish one or a list of documents. It manages automatic upload and removal dates, as well as a manual scheduling or suspension of each document.
* The documents are accessible through the search engine. The search engine indexes in full text the content areas defined as "to be indexed" in the document type.


## Usage

 **Document Indexing** 

After creating or modifying documents, it is necessary to run the indexing daemon `docSearchIndexer` so that the new documents or modifications are included in the search results.

The indexing daemon can be launched manually from the administration interface (System → Daemons) or runs automatically according to the defined configuration.

## Configuration

 **Indexing and Parsers** 

For complete document indexing, especially for binary files (PDF, Office, etc.), it is recommended to install the **plugin-parser** .

The document plugin provides a default HTML parser ( `DefaultDocumentIndexerParser` ) which uses JSoup to extract text from HTML content. This parser has a low priority (priority 2).

The plugin-parser provides a universal parser based on Apache Tika ( `TikaAutoDetectParser` ) which has a higher priority (priority 1). This parser automatically detects document formatsand can process:
 
* PDF
* MS Office documents (DOC, XLS, PPT, DOCX, XLSX, PPTX)
* OpenDocument documents (ODT, ODS, ODP)
* HTML, XML
* Archives (ZIP, RAR, etc.)
* And many other formats


 **Note:** The plugin-parser brings heavy dependencies (Apache Tika and its libraries). If you only need HTML indexing, the document plugin's default parser is sufficient.For complete indexing including binary files, install the plugin-parser.

The CDI priority system automatically selects the available parser with the highest priority.

## Templates

Since version 7.0.0 the plugin uses no XML nor XSL: the documents are displayed with FreeMarker templates.

 
*  **Document types** : each document type has a back office template (preview of the working content) and a front office template (validated content), edited in the document type page. An empty template means that the defaulttemplate of the plugin is used ( `admin/plugins/document/document_admin_default.html` , `skin/plugins/document/document_content_default.html` ). A template is checked with a sample document before being saved, and a template that fails is replaced by the default template when the document is displayed.
*  **Template model** : `document` ( `title` , `summary` , `categories` , `attributes` by attribute code), `document_type` , and in the front office `publication_date` (may be null), `site_locale` and `request_parameters` . A file attribute is an object with `url` , `fileName` , `contentType` and `image` . An attribute without value is absent: test it with `??` .
*  **Macros** ( `util/document/macro_document_view.html` , whatever the auto-escaping setting of the site): `@documentValue` (escaped text), `@documentHtml` (rich text as is), `@documentFile` , `@documentFileIcon` and `@documentAttribute` (display according to the attribute type).
*  **Portlets** : the document and document list portlets use the section templates of the core (Section Template Management feature). The templates shipped in `skin/plugins/document/portlet/` replace the former XSL styles; a site adds its own templates in the core feature.
*  **Metadata** : the metadata of the documents are stored as a JSON object. The Dublin Core handler renders them as `DC.*` meta tags with the template `skin/plugins/document/document_include_meta.html` .

## Upgrading to 7.0.0

Version 7.0.0 no longer depends on plugin-xmltransformer. The upgrade scripts migrate the data; the legacy columns are dropped at a later start, once their content is no longer needed.

 
*  **Document types** : the initial document types whose XSL stylesheets were never modified receive the new templates automatically. The document types with specific XSL stylesheets are listed in the document types page ("XSL to rewrite"):their stylesheets are shown in the document type page, where they must be rewritten as templates and then discarded. Until then, these document types use the default templates.
*  **Portlets** : each portlet using a shipped XSL style is moved to the matching template. A portlet using a style created by the site uses the default template: choose or create another template in the portlet form.
*  **Metadata** : the `legacyMetadataMigration` daemon converts the XML metadata into JSON, then does nothing. A document whose metadata can not be converted is logged and kept for the next run.
*  **Contents** : the documents are displayed from their attributes. A published document whose only validated version was its XML content must be validated again: the query listing such documents is in the upgrade script `update_db_document-6.0.1-7.0.0.sql` .
*  **API** : the XML methods of the plugin are removed ( `Document.getXml` , `getXmlWorkingContent` , `getXmlValidatedContent` , `getXmlMetadata` , `AttributeManager.getAttributeXmlValue` , `MetadataHandler.getXmlMetadata` , `DocumentTypeService` , `DocumentAttributesService` , `DocumentSpacesService.getXmlSpacesList` ): the modules using them must be updated. The deprecated `getInstance()` methods of the services and of the portlet homes are removed as well: inject the beans instead. `Rule.init()` is removed (it was never called). `DocumentResourceServlet` is no longer a CDI bean: its cache and `putInCache` move to the `ResourceServletCache` bean ( `cacheValidatedResource` ). The unused property `document.regularExpressionPerPage` is removed: the attribute form lists all its regular expressions.
*  **Files** : `servlet/plugins/document/resource` now checks the access to the files. The working files ( `working_content` parameter) are sent only to the administrators allowed to view the document; the validated files are sent to the other users only if the document is valid and published in a published portlet whose page they are allowed tosee. The files of documents that are not published, linked from another document for instance, are no longer public. The unused page `jsp/site/plugins/document/DocumentResource.jsp` is removed.
*  **Back office security tokens** : the back office actions that modify data (documents, document types, publishing, categories, rules, spaces) now require a security token against CSRF attacks, and refuse the request with an"access denied" message otherwise. A site that overrides the admin templates of the plugin must add the token to its copies: `<@input type='hidden' name='token' value='${token}' />` in the forms, and `&token=${token}` in the links to the actions (workflow actions of the documents, moving the attributes of a document type). The creation andmodification of the portlets, which use the portlet form of the core, are not concerned.


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-document/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*