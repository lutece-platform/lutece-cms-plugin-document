-- liquibase formatted sql
-- changeset document:update_db_document-4.0.9-4.2.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE document_space_action SET icon_url='plus-circle' WHERE id_action=1;
UPDATE document_space_action SET icon_url='trash' WHERE id_action=2;
UPDATE document_space_action SET icon_url='edit' WHERE id_action=3;
UPDATE document_space_action SET icon_url='user' WHERE id_action=4;
UPDATE document_space_action SET icon_url='arrows-alt' WHERE id_action=5;

UPDATE document_workflow_action SET icon_url='trash' WHERE id_action=1;
UPDATE document_workflow_action SET icon_url='edit' WHERE id_action=2;
UPDATE document_workflow_action SET icon_url='cog' WHERE id_action=3;
UPDATE document_workflow_action SET icon_url='check' WHERE id_action=4;
UPDATE document_workflow_action SET icon_url='globe' WHERE id_action=5;
UPDATE document_workflow_action SET icon_url='minus' WHERE id_action=6;
UPDATE document_workflow_action SET icon_url='check' WHERE id_action=7;
UPDATE document_workflow_action SET icon_url='download' WHERE id_action=8;
UPDATE document_workflow_action SET icon_url='edit' WHERE id_action=9;
UPDATE document_workflow_action SET icon_url='cog' WHERE id_action=10;
UPDATE document_workflow_action SET icon_url='minus' WHERE id_action=11;
UPDATE document_workflow_action SET icon_url='check' WHERE id_action=12;
UPDATE document_workflow_action SET icon_url='upload' WHERE id_action=13;
UPDATE document_workflow_action SET icon_url='list' WHERE id_action=14;
UPDATE document_workflow_action SET icon_url='eye' WHERE id_action=15;
UPDATE document_workflow_action SET icon_url='arrows-alt' WHERE id_action=16;
UPDATE document_workflow_action SET icon_url='globe' WHERE id_action=18;