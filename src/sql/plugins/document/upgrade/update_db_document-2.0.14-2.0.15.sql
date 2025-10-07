-- liquibase formatted sql
-- changeset document:update_db_document-2.0.14-2.0.15.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE document_workflow_action SET action_url= 'jsp/admin/plugins/document/modules/comment/ManageDocumentComments.jsp?' WHERE id_action = 17;