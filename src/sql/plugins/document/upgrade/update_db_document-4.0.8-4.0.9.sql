-- liquibase formatted sql
-- changeset document:update_db_document-4.0.8-4.0.9.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Table document
--

/* Add skip HTML generation columns */
ALTER TABLE document ADD COLUMN skip_portlet    SMALLINT NOT NULL DEFAULT 0 AFTER id_page_template_document;
ALTER TABLE document ADD COLUMN skip_categories SMALLINT NOT NULL DEFAULT 0 AFTER skip_portlet;
