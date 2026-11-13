-- liquibase formatted sql
-- changeset document:update_db_document-6.0.1-6.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- The URLs of the workflow actions are written by the templates, which escape them: store them as plain URLs
UPDATE document_workflow_action SET action_url = REPLACE( action_url, '&amp;', '&' ) WHERE action_url LIKE '%&amp;%';
