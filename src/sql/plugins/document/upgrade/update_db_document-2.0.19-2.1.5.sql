-- liquibase formatted sql
-- changeset document:update_db_document-2.0.19-2.1.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Table document_content
--
ALTER TABLE document_content ADD validated  smallint default 0 NOT NULL;
ALTER TABLE document_content DROP PRIMARY KEY , ADD PRIMARY KEY (id_document,id_document_attr, validated);
