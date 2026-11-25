-- liquibase formatted sql
-- changeset document:update_db_core_document-6.0.1-7.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'core_portlet' AND column_name = 'id_template'
--
-- The plugin upgrade scripts run BEFORE the core upgrade script in the same liquibase run (sql/plugins/* sorts before sql/upgrade/*) :
-- the core structures are created here when they do not exist yet, with the very same statements as the core script, which is then skipped.
--
ALTER TABLE core_portlet ADD COLUMN id_template int default 0 NOT NULL;

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'core_portlet_template'
CREATE TABLE IF NOT EXISTS core_portlet_template (
	id_template int AUTO_INCREMENT NOT NULL,
	id_portlet_type varchar(50) default NULL,
	description varchar(255) default NULL,
	template_path varchar(255) default NULL,
	PRIMARY KEY (id_template)
);

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Défaut', 'skin/plugins/document/portlet/document_list_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Actor', 'skin/plugins/document/portlet/document_list_portlet_actor.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Article', 'skin/plugins/document/portlet/document_list_portlet_article.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Article Extend', 'skin/plugins/document/portlet/document_list_portlet_article_extend.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type Image', 'skin/plugins/document/portlet/document_list_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Défaut Download', 'skin/plugins/document/portlet/document_list_portlet_download.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_LIST_PORTLET', 'Type PDF Extend', 'skin/plugins/document/portlet/document_list_portlet_pdf_extend.html');

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Défaut', 'skin/plugins/document/portlet/document_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Actor', 'skin/plugins/document/portlet/document_portlet_actor.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Article', 'skin/plugins/document/portlet/document_portlet_article.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Image', 'skin/plugins/document/portlet/document_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('DOCUMENT_PORTLET', 'Type Pdf', 'skin/plugins/document/portlet/document_portlet_pdf.html');

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Each portlet keeps the layout of its XSL style: the shipped styles are mapped to the matching template, the other styles to the default template (0)
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_actor.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1701);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_article.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1703);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_image.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1704);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_article_extend.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1708, 1710);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_pdf_extend.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1711);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_list_portlet_download.html') WHERE id_portlet_type = 'DOCUMENT_LIST_PORTLET' AND id_style IN (1706, 1739);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_portlet_actor.html') WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND id_style IN (1730);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_portlet_article.html') WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND id_style IN (1732);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_portlet_image.html') WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND id_style IN (1733);
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND template_path = 'skin/plugins/document/portlet/document_portlet_pdf.html') WHERE id_portlet_type = 'DOCUMENT_PORTLET' AND id_style IN (1735);
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type IN ('DOCUMENT_LIST_PORTLET', 'DOCUMENT_PORTLET');

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The XSL styles shipped with the plugin are replaced by the portlet templates. The legacy style tables belong to plugin-xmltransformer and may be absent: skip instead of failing
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style');
DELETE FROM core_style_mode_stylesheet WHERE id_style IN (1700, 1701, 1703, 1704, 1706, 1708, 1710, 1711, 1730, 1732, 1733, 1735, 1739);
DELETE FROM core_style WHERE id_style IN (1700, 1701, 1703, 1704, 1706, 1708, 1710, 1711, 1730, 1732, 1733, 1735, 1739);
DELETE FROM core_stylesheet WHERE id_stylesheet IN (297, 298, 300, 302, 304, 305, 306, 403, 408, 505, 506, 510);

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev6.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The automatic validation of the documents is managed in the admin dashboard (Document - Advanced configuration) instead of document.properties
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_datastore WHERE entity_key = 'document.advanced_parameters.workflow_auto_validation'
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('document.advanced_parameters.workflow_auto_validation', 'true');

-- changeset document:update_db_core_document-6.0.1-7.0.0.sql-rev7.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The admin dashboard (Document - Advanced configuration) is displayed only once it has a column and an order
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_dashboard WHERE dashboard_name = 'documentAdminDashboardComponent'
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('documentAdminDashboardComponent', 1, 8);
