-- liquibase formatted sql
-- changeset document:init_core_document.sql
-- validCheckSum: ANY
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('DOCUMENT_TYPES_MANAGEMENT','document.adminFeature.document_types_management.name',1,'jsp/admin/plugins/document/ManageDocumentTypes.jsp','document.adminFeature.document_types_management.description',0,'document','CONTENT','ti ti-file-info',NULL);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('DOCUMENT_MANAGEMENT','document.adminFeature.documents_management.name',3,'jsp/admin/plugins/document/ManageDocuments.jsp','document.adminFeature.documents_management.description',0,'document','CONTENT','ti ti-file-description',NULL);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('DOCUMENT_RULES_MANAGEMENT','document.adminFeature.rules_management.name',1,'jsp/admin/plugins/document/ManageRules.jsp','document.adminFeature.rules_management.description',0,'document','CONTENT','ti ti-reorder',NULL);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('DOCUMENT_CATEGORY_MANAGEMENT','document.adminFeature.category_management.name',1,'jsp/admin/plugins/document/ManageCategory.jsp','document.adminFeature.category_management.description',0,'document','CONTENT','ti ti-bookmarks',NULL);

--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('view_spaces_all','Vue sur tous les espaces');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('manage_spaces','Gestion des espaces de documents');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('view_space_work','Vue sur l''espace de travail');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('view_spaces_multimedia','Vue sur les espaces multimedia');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('content_contributor','Producteur de contenu');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('content_validator','Validateur de contenu');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('content_publisher','Gestionnaire de publication');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('view_space_validation','Vue sur l''espace de validation');

--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('view_spaces_multimedia','DOCUMENT_SPACE','5','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('view_space_work','DOCUMENT_SPACE','2','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('manage_spaces','DOCUMENT_SPACE','*','CREATE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('manage_spaces','DOCUMENT_SPACE','*','MOVE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('manage_spaces','DOCUMENT_SPACE','*','MODIFY');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('manage_spaces','DOCUMENT_SPACE','*','DELETE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('view_spaces_all','DOCUMENT_SPACE','0','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('super_admin','DOCUMENT_TYPE','*','*');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','SUBMIT');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','DELETE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','MODIFY');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','VIEW_HISTORY');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','CREATE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_validator','DOCUMENT_TYPE','*','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_validator','DOCUMENT_TYPE','*','VIEW_HISTORY');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_validator','DOCUMENT_TYPE','*','VALIDATE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_publisher','DOCUMENT_TYPE','*','ARCHIVE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_publisher','DOCUMENT_TYPE','*','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_publisher','DOCUMENT_TYPE','*','VIEW_HISTORY');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_publisher','DOCUMENT_TYPE','*','PUBLISH');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('view_space_validation','DOCUMENT_SPACE','3','VIEW');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_contributor','DOCUMENT_TYPE','*','CHANGE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('content_publisher','DOCUMENT_TYPE','*','ASSIGN');

--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
('DOCUMENT_LIST_PORTLET','document.portlet.documentListPortlet.name','plugins/document/CreateDocumentListPortlet.jsp','plugins/document/ModifyDocumentListPortlet.jsp','fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome','document','plugins/document/DoCreateDocumentListPortlet.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/document/portlet/document_list_portlet_type.html','','plugins/document/DoModifyDocumentListPortlet.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/document/portlet/document_list_portlet_type.html','');
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
('DOCUMENT_PORTLET','document.portlet.documentPortlet.name','plugins/document/CreateDocumentPortlet.jsp','plugins/document/ModifyDocumentPortlet.jsp','fr.paris.lutece.plugins.document.business.portlet.DocumentPortletHome','document','plugins/document/DoCreateDocumentPortlet.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/document/portlet/document_portlet_type.html','','plugins/document/DoModifyDocumentPortlet.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/document/portlet/document_portlet_type.html','');

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_MANAGEMENT',2);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_MANAGEMENT',3);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_MANAGEMENT',4);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_RULES_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_RULES_MANAGEMENT',2);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_TYPES_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DOCUMENT_CATEGORY_MANAGEMENT',1);

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_contributor',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_contributor',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_contributor',3);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_publisher',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_publisher',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_validator',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_validator',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('content_validator',4);
INSERT INTO core_user_role (role_key,id_user) VALUES ('manage_spaces',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('manage_spaces',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('manage_spaces',5);
INSERT INTO core_user_role (role_key,id_user) VALUES ('view_space_validation',4);
INSERT INTO core_user_role (role_key,id_user) VALUES ('view_space_work',3);
INSERT INTO core_user_role (role_key,id_user) VALUES ('view_spaces_all',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('view_spaces_all',2);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('DOCUMENT', 2, 1);

-- Cache of the downloaded files disabled by default: the browser cache (Expires header) and a caching proxy serve
-- the files better, without holding whole files in the heap of every instance
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('core.cache.status.DocumentResourceServletCache.enabled', 0);

-- Advanced parameters, managed in the admin dashboard (Document - Advanced configuration)
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('document.advanced_parameters.workflow_auto_validation', 'false');

--
-- FreeMarker templates of the document portlets, registered in the core (Section Template Management feature). The first one of a type is its default template
--
-- changeset document:init_core_document.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Défaut', 'skin/plugins/document/portlet/document_list_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Actor', 'skin/plugins/document/portlet/document_list_portlet_actor.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Article', 'skin/plugins/document/portlet/document_list_portlet_article.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Article Extend', 'skin/plugins/document/portlet/document_list_portlet_article_extend.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Image', 'skin/plugins/document/portlet/document_list_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Défaut Download', 'skin/plugins/document/portlet/document_list_portlet_download.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type PDF Extend', 'skin/plugins/document/portlet/document_list_portlet_pdf_extend.html');

-- changeset document:init_core_document.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Défaut', 'skin/plugins/document/portlet/document_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Actor', 'skin/plugins/document/portlet/document_portlet_actor.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Article', 'skin/plugins/document/portlet/document_portlet_article.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Image', 'skin/plugins/document/portlet/document_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Pdf', 'skin/plugins/document/portlet/document_portlet_pdf.html');

-- changeset document:init_core_document.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The admin dashboard (Document - Advanced configuration) is displayed only once it has a column and an order
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_dashboard WHERE dashboard_name = 'documentAdminDashboardComponent'
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('documentAdminDashboardComponent', 1, 8);
