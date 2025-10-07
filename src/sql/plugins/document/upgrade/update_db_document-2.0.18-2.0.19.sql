-- liquibase formatted sql
-- changeset document:update_db_document-2.0.18-2.0.19.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Table structure for table document_indexer_action
--
DROP TABLE IF EXISTS document_indexer_action;
CREATE TABLE document_indexer_action (
  id_action int default 0 NOT NULL,
  id_record int default 0 NOT NULL,
  id_task int default 0 NOT NULL,
  PRIMARY KEY (id_action)  
);


--
-- Table document_workflow_transition
--
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,18);


--
-- Table document_workflow_action
--
UPDATE document_workflow_action SET 
	name_key = 'document.workflow.action.assignDocument.name', 
	description_key = 'document.workflow.action.assignDocument.description', 
	icon_url = 'images/admin/skin/plugins/document/actions/assign.png', 
	action_permission = 'ASSIGN' 
WHERE id_action = 5;

INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(18,'document.workflow.action.publishDocument.name','document.workflow.action.publishDocument.description',NULL,NULL,'PUBLISH',null);
