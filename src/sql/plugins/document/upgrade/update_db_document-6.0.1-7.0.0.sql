-- liquibase formatted sql
-- changeset document:update_db_document-6.0.1-7.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The columns already exist on a database created by the 7.0.0 scripts
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'document' AND column_name = 'validated_title'
--
-- Snapshot of the title and summary at validation time (replaces the XML validated content)
--
ALTER TABLE document ADD COLUMN validated_title varchar(255) default NULL;
ALTER TABLE document ADD COLUMN validated_summary long varchar;
UPDATE document SET validated_title = title, validated_summary = document_summary;

--
-- FreeMarker templates of the document types (replace the XSL stylesheets).
-- A NULL template means that the default template of the plugin is used.
-- The admin_xsl and content_service_xsl columns are kept so that the specific stylesheets can be rewritten as templates.
--
ALTER TABLE document_type ADD COLUMN admin_template long varchar;
ALTER TABLE document_type ADD COLUMN content_template long varchar;

--
-- Metadata of the documents stored as JSON (replaces the XML metadata).
-- The XML metadata are converted by the legacyMetadataMigration daemon, which clears the xml_metadata column document by document.
--
ALTER TABLE document ADD COLUMN metadata long varchar;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- comment The migration daemon runs only when some documents have XML metadata
-- precondition-sql-check expectedResult:1 SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM document WHERE xml_metadata IS NOT NULL
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('document.legacyMetadataMigration.pending', 'true');

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The initial document types whose XSL stylesheet was never modified (same checksum as one of the shipped versions) get the shipped FreeMarker template, and their stylesheet is discarded
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'document_type' AND column_name IN ('admin_xsl', 'content_service_xsl')
UPDATE document_type SET content_template = '<p>
<#if document.attributes.photo??><@documentFile file=document.attributes.photo class=''float-start me-3'' /></#if>
<strong><@documentValue value=document.attributes.firstname /> <@documentValue value=document.attributes.lastname /></strong>
</p>
<#if document.attributes[''function'']??>
<p>Fonction : <@documentValue value=document.attributes[''function''] /></p>
</#if>', content_service_xsl = NULL
	WHERE code_document_type = 'actor' AND content_template IS NULL AND MD5(content_service_xsl) IN ('933f1afe192ebbbe697e7ea7f2dcae51', '9f735c7c1c04d73971d5b7c4eb5e445e');
UPDATE document_type SET admin_template = '<p><strong>#i18n{document.document_admin_default.labelTitle} : </strong><@documentValue value=document.title /></p>
<p><strong>#i18n{document.document_admin_default.labelSummary} : </strong><@documentValue value=document.summary /></p>
<#if document.attributes.attachment??>
<p><strong>#i18n{document.document_admin_default.labelFile} : </strong></p>
<p><@documentFile file=document.attributes.attachment /></p>
</#if>
<#if document.attributes.body??>
<div><@documentHtml html=document.attributes.body /></div>
</#if>', admin_xsl = NULL
	WHERE code_document_type = 'article' AND admin_template IS NULL AND MD5(admin_xsl) IN ('02b0240d670ab5ea7427312b16e0bd70', '72d69303df4cdb72e3bd8fd15445eefc', 'bcba0207634cd0f00443036655308ec6', 'e99a9fd32b044d6fd47fa929c0809831');
UPDATE document_type SET content_template = '<p><strong><@documentValue value=document.title /></strong></p>
<#if document.attributes.attachment??>
<p><@documentFile file=document.attributes.attachment /></p>
</#if>
<#if document.attributes.body??>
<div><@documentHtml html=document.attributes.body /></div>
</#if>', content_service_xsl = NULL
	WHERE code_document_type = 'article' AND content_template IS NULL AND MD5(content_service_xsl) IN ('47a14c869136527131309fa3c5c6f91b', '8b8d433abd95d08f5caa38a6ca480790', 'a807ab617d0c4facb8d6658ecebd73c8', 'f28bdd7df7f244f8b1389c06a8218a8e');
UPDATE document_type SET content_template = '<p><strong><@documentValue value=document.title /></strong></p>
<#if document.attributes.file??><@documentFile file=document.attributes.file class=''img-fluid'' alt=document.title /></#if>
<#if document.summary?has_content>
<p><@documentValue value=document.summary /></p>
</#if>
<#if document.attributes.credits??>
<p><@documentValue value=document.attributes.credits /></p>
</#if>', content_service_xsl = NULL
	WHERE code_document_type = 'image' AND content_template IS NULL AND MD5(content_service_xsl) IN ('18580344aafea9242b08369250303944', '22399f540c56c8c21452388c6f36594b');

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- comment The document types with specific XSL stylesheets are listed in the back office until the stylesheets are rewritten as templates and discarded
-- precondition-sql-check expectedResult:1 SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM document_type WHERE admin_xsl IS NOT NULL OR content_service_xsl IS NOT NULL
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('document.legacyStylesheets.pending', 'true');

--
-- The legacy columns are dropped once their content is no longer needed. The preconditions use onFail:CONTINUE:
-- a changeset whose precondition fails is attempted again at the next start, until the migration is complete.
--

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev4.sql
-- preconditions onFail:CONTINUE onError:MARK_RAN
-- comment Dropped when every legacy XSL stylesheet has been discarded in the back office
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM document_type WHERE admin_xsl IS NOT NULL OR content_service_xsl IS NOT NULL
ALTER TABLE document_type DROP COLUMN admin_xsl;
ALTER TABLE document_type DROP COLUMN content_service_xsl;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev5.sql
-- preconditions onFail:CONTINUE onError:MARK_RAN
-- comment Dropped when the legacyMetadataMigration daemon has converted every XML metadata
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM document WHERE xml_metadata IS NOT NULL
ALTER TABLE document DROP COLUMN xml_metadata;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev6.sql
-- preconditions onFail:CONTINUE onError:MARK_RAN
-- comment The XML contents are dropped unless a published document has a validated XML content but no validated attribute
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM document d WHERE d.xml_validated_content IS NOT NULL AND EXISTS ( SELECT 1 FROM document_published p WHERE p.id_document = d.id_document ) AND NOT EXISTS ( SELECT 1 FROM document_content c WHERE c.id_document = d.id_document AND c.validated = 1 )
--
-- The documents are displayed from the document_content table. A published document whose validated XML content was its only validated version
-- blocks the drop: it must be validated again in the back office. Documents concerned :
--   SELECT d.id_document, d.title FROM document d WHERE d.xml_validated_content IS NOT NULL AND EXISTS ( SELECT 1 FROM document_published p WHERE p.id_document = d.id_document ) AND NOT EXISTS ( SELECT 1 FROM document_content c WHERE c.id_document = d.id_document AND c.validated = 1 )
ALTER TABLE document DROP COLUMN xml_working_content;
ALTER TABLE document DROP COLUMN xml_validated_content;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev7.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Tabler icons of the workflow actions
UPDATE document_workflow_action SET icon_url='pencil' WHERE id_action=2;
UPDATE document_workflow_action SET icon_url='archive' WHERE id_action=8;
UPDATE document_workflow_action SET icon_url='pencil' WHERE id_action=9;
UPDATE document_workflow_action SET icon_url='archive-off' WHERE id_action=13;
UPDATE document_workflow_action SET icon_url='folder-symlink' WHERE id_action=16;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev8.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Tabler icons of the space actions
UPDATE document_space_action SET icon_url='folder-plus fs-2' WHERE id_action=1;
UPDATE document_space_action SET icon_url='folder-x fs-2' WHERE id_action=2;
UPDATE document_space_action SET icon_url='folder-cog fs-2' WHERE id_action=3;
UPDATE document_space_action SET icon_url='folder-user fs-2' WHERE id_action=4;
UPDATE document_space_action SET icon_url='folder-symlink fs-2' WHERE id_action=5;

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev9.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The images of the plugin moved from themes/shared/plugins/document/images to themes/shared/plugins/document/images. The space icons were stored with Windows separators (themes\\shared\\...), which a browser does not resolve as a path: they become URL paths first
UPDATE document_space_icon SET icon_url = REPLACE( icon_url, '\\', '/' );
UPDATE document_space_icon SET icon_url = REPLACE( icon_url, 'themes/shared/plugins/document/images/', 'themes/shared/plugins/document/images/' );
UPDATE document_page_template SET picture_path = REPLACE( picture_path, 'themes/shared/plugins/document/images/', 'themes/shared/plugins/document/images/' );
UPDATE document_type SET default_thumbnail_url = REPLACE( default_thumbnail_url, 'themes/shared/plugins/document/images/', 'themes/shared/plugins/document/images/' );

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev10.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The default thumbnail of the pdf document type pointed to an image that the plugin no longer ships
UPDATE document_type SET default_thumbnail_url = 'themes/shared/plugins/document/images/filetypes/file-type-pdf.svg' WHERE default_thumbnail_url = 'images/local/skin/plugins/document/filetypes/pdf.png';

-- changeset document:update_db_document-6.0.1-7.0.0.sql-rev11.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The action URLs were stored with their ampersands escaped (&amp;): the templates escape on output now, which wrote &amp;amp; and lost the id_document parameter
UPDATE document_workflow_action SET action_url = REPLACE( action_url, '&amp;', '&' ) WHERE action_url LIKE '%&amp;%';
