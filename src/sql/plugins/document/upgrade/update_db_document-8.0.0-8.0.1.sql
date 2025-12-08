-- liquibase formatted sql
-- changeset document:update_db_document-8.0.0-8.0.1
-- preconditions onFail:MARK_RAN onError:WARN

--
-- DOCUMENT-XXX : Add AUTO_INCREMENT to primary keys
--
ALTER TABLE document MODIFY COLUMN id_document int AUTO_INCREMENT;
ALTER TABLE document_type_attr MODIFY COLUMN id_document_attr int AUTO_INCREMENT;
ALTER TABLE document_category MODIFY COLUMN id_category int AUTO_INCREMENT;
ALTER TABLE document_rule MODIFY COLUMN id_rule int AUTO_INCREMENT;
