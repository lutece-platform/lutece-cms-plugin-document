ALTER TABLE document_category ADD COLUMN workgroup_key varchar(50) default NULL;

ALTER TABLE document_space ADD COLUMN workgroup_key varchar(50) default NULL;

ALTER TABLE document_workflow_action ADD COLUMN id_finish_state int default NULL;

DELETE FROM document_workflow_action;

INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(1,'document.workflow.action.deleteDocument.name','document.workflow.action.deleteDocument.description','jsp/admin/plugins/document/DeleteDocument.jsp?','images/admin/skin/plugins/document/actions/delete.png','DELETE',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(2,'document.workflow.action.modifyDocument.name','document.workflow.action.modifyDocument.description','jsp/admin/plugins/document/ModifyDocument.jsp?','images/admin/skin/plugins/document/actions/modify.png','MODIFY',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(3,'document.workflow.action.submitForApproval.name','document.workflow.action.submitForApproval.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=2&amp;','images/admin/skin/plugins/document/actions/submit.png','SUBMIT',2);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(4,'document.workflow.action.approveDocument.name','document.workflow.action.approveDocument.description','jsp/admin/plugins/document/DoValidateDocument.jsp?id_state=3&amp;','images/admin/skin/plugins/document/actions/validate.png','VALIDATE',3);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(5,'document.workflow.action.publishDocument.name','document.workflow.action.publishDocument.description','jsp/admin/plugins/document/ManageDocumentPublishing.jsp?','images/admin/skin/plugins/document/actions/publish.png','PUBLISH',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(6,'document.workflow.action.rejectDocument.name','document.workflow.action.rejectDocument.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=4&amp;','images/admin/skin/plugins/document/actions/reject.png','VALIDATE',4);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(7,'document.workflow.action.submitAgain.name','document.workflow.action.submitAgain.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=2&amp;','images/admin/skin/plugins/document/actions/submit_again.png','SUBMIT',2);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(8,'document.workflow.action.archiveDocument.name','document.workflow.action.archiveDocument.description','jsp/admin/plugins/document/DoConfirmArchiveDocument.jsp?id_state=5&amp;','images/admin/skin/plugins/document/actions/archive.png','ARCHIVE',5);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(9,'document.workflow.action.changeDocument.name','document.workflow.action.changeDocument.description','jsp/admin/plugins/document/ModifyDocument.jsp?id_state=6&amp;','images/admin/skin/plugins/document/actions/change.png','CHANGE',6);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(10,'document.workflow.action.submitChanges.name','document.workflow.action.submitChanges.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=7&amp;','images/admin/skin/plugins/document/actions/submit_changes.png','SUBMIT',7);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(11,'document.workflow.action.rejectChanges.name','document.workflow.action.rejectChanges.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=6&amp;','images/admin/skin/plugins/document/actions/reject_changes.png','VALIDATE',6);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(12,'document.workflow.action.approveChanges.name','document.workflow.action.approveChanges.description','jsp/admin/plugins/document/DoValidateDocument.jsp?id_state=3&amp;','images/admin/skin/plugins/document/actions/validate.png','VALIDATE',3);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(13,'document.workflow.action.unarchiveDocument.name','document.workflow.action.unarchiveDocument.description','jsp/admin/plugins/document/DoChangeState.jsp?id_state=3&amp;','images/admin/skin/plugins/document/actions/unarchive.png','ARCHIVE',3);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(14,'document.workflow.action.history.name','document.workflow.action.history.description','jsp/admin/plugins/document/DocumentHistory.jsp?','images/admin/skin/plugins/document/actions/history.png','VIEW_HISTORY',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(15,'document.workflow.action.previewDocument.name','document.workflow.action.previewDocument.description','jsp/admin/plugins/document/PreviewDocument.jsp?','images/admin/skin/plugins/document/actions/view.png','VIEW',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(16,'document.workflow.action.moveDocument.name','document.workflow.action.moveDocument.description','jsp/admin/plugins/document/MoveDocument.jsp?','images/admin/skin/plugins/document/actions/move.png','MOVE',null);
INSERT INTO document_workflow_action (id_action,name_key,description_key,action_url,icon_url,action_permission,id_finish_state) VALUES 
(17,'document.workflow.action.commentDocument.name','document.workflow.action.commentDocument.description','jsp/admin/plugins/document/ManageDocumentComments.jsp?','images/admin/skin/plugins/document/actions/comment.png','COMMENT',null);
