-- liquibase formatted sql
-- changeset document:update_db_document-4.2.0-5.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,16);