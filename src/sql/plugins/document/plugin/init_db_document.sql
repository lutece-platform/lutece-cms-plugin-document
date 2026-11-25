-- liquibase formatted sql
-- changeset document:init_db_document.sql
-- validCheckSum: ANY
-- preconditions onFail:MARK_RAN onError:WARN

-- This setting is required in MariaDB/MySQL when inserting a row with a primary key value of 0.
-- Enabling NO_AUTO_VALUE_ON_ZERO ensures that 0 is inserted explicitly instead of being replaced by 1.
SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';

INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('file','document.attributeType.file.name','document.attributeType.file.description','fr.paris.lutece.plugins.document.service.attributes.FileManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('image','document.attributeType.image.name','document.attributeType.image.description','fr.paris.lutece.plugins.document.service.attributes.ImageFileManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('listbox','document.attributeType.listbox.name','document.attributeType.listbox.description','fr.paris.lutece.plugins.document.service.attributes.ListBoxManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('multiline','document.attributeType.multiline.name','document.attributeType.multiline.description','fr.paris.lutece.plugins.document.service.attributes.MultilineManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('richtext','document.attributeType.richtext.name','document.attributeType.richtext.description','fr.paris.lutece.plugins.document.service.attributes.RichTextManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('text','document.attributeType.text.name','document.attributeType.text.description','fr.paris.lutece.plugins.document.service.attributes.TextManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('url','document.attributeType.url.name','document.attributeType.url.description','fr.paris.lutece.plugins.document.service.attributes.UrlManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('date','document.attributeType.date.name','document.attributeType.date.description','fr.paris.lutece.plugins.document.service.attributes.DateManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('numerictext','document.attributeType.numerictext.name','document.attributeType.numerictext.description','fr.paris.lutece.plugins.document.service.attributes.NumerictextManager');
INSERT INTO document_attr_type (code_attr_type,name_key,description_key,manager_class) VALUES ('internallink','document.attributeType.internallink.name','document.attributeType.internallink.description','fr.paris.lutece.plugins.document.service.attributes.InternalLinkManager');

--
-- Dumping data for table document_attr_type_parameter
--
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('text','size','document.attributeType.text.parameter.size.label',1,'document.attributeType.text.parameter.size.description','50');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('text','maxlength','document.attributeType.text.parameter.maxlength.label',2,'document.attributeType.text.parameter.maxlength.description','60');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('listbox','items','document.attributeType.listbox.parameter.items.label',2,'document.attributeType.listbox.parameter.items.description','');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('listbox','value','document.attributeType.listbox.parameter.defaultvalue.label',1,'document.attributeType.listbox.parameter.defaultvalue.description','');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('numerictext','size','document.attributeType.numerictext.parameter.size.label',1,'document.attributeType.numerictext.parameter.size.description','5');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('numerictext','maxlength','document.attributeType.numerictext.parameter.maxlength.label',2,'document.attributeType.numerictext.parameter.maxlength.description','5');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('date','defaultvalue','document.attributeType.date.parameter.defaultvalue.label',1,'document.attributeType.date.parameter.defaultvalue.description','');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('date','currentdate','document.attributeType.date.parameter.currentdate.label',2,'document.attributeType.date.parameter.currentdate.description','');
INSERT INTO document_attr_type_parameter (code_attr_type,parameter_name,parameter_label_key,parameter_index,parameter_description_key,parameter_default_value) VALUES ('file', 'extension', 'document.attributeType.file.parameter.extension.label', 1, 'document.attributeType.file.parameter.extension.description', 'docx,xlsx,pdf');
INSERT INTO document_attr_type_parameter (code_attr_type, parameter_name, parameter_label_key, parameter_index, parameter_description_key, parameter_default_value) VALUES ('image', 'extension', 'document.attributeType.image.parameter.extension.label', 1, 'document.attributeType.image.parameter.extension.description', 'jpg,jpeg,png');

--
-- Dumping data for table document_page_template
--
INSERT INTO document_page_template (page_template_path,picture_path,description) VALUES ('/skin/plugins/document/document_content_service.html','themes/shared/plugins/document/images/page_templates/page_template_document0.png','Défaut');
INSERT INTO document_page_template (page_template_path,picture_path,description) VALUES ('/skin/plugins/document/page_templates/page_template_document1.html','themes/shared/plugins/document/images/page_templates/page_template_document1.png','Pleine page');
INSERT INTO document_page_template (page_template_path,picture_path,description) VALUES ('/skin/plugins/document/page_templates/page_template_document2.html','themes/shared/plugins/document/images/page_templates/page_template_document2.png','Rubrique en-dessous');

--
-- Dumping data for table document_rule
--
INSERT INTO document_rule (id_rule,rule_type) VALUES (3,'moveSpace');
INSERT INTO document_rule (id_rule,rule_type) VALUES (2,'moveSpace');
INSERT INTO document_rule (id_rule,rule_type) VALUES (1,'moveSpace');
INSERT INTO document_rule (id_rule,rule_type) VALUES (4,'moveSpace');
INSERT INTO document_rule (id_rule,rule_type) VALUES (6,'moveSpace');

--
-- Dumping data for table document_rule_attr
--
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (3,'id_state','4');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (2,'id_state','3');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (3,'id_space_source','3');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (3,'id_space_destination','2');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (1,'id_space_destination','3');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (1,'id_state','2');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (2,'id_space_source','3');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (2,'id_space_destination','4');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (1,'id_space_source','2');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (4,'id_space_source','4');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (4,'id_space_destination','15');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (4,'id_state','5');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (6,'id_space_source','15');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (6,'id_space_destination','4');
INSERT INTO document_rule_attr (id_rule,attr_name,attr_value) VALUES (6,'id_state','3');

--
-- Dumping data for table document_space
--
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (0,-1,'Racine des contenus','Racine des contenus','detail',1,0,NULL);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (1,0,'Contenu Editorial','Contenu Editorial','detail',1,0,NULL);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (2,1,'Espace de travail','Espace de travail','detail',10,0,1);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (3,1,'Espace de validation','Espace de validation','detail',11,1,0);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (4,1,'Espace de publication','Espace de publication','detail',12,2,0);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (5,0,'Bibliotheque multimedia','Bibliotheque multimedia','detail',1,1,NULL);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (6,5,'Images','Images','thumbnail',2,0,1);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (7,0,'Fichiers en telechargement','Fichiers en telechargement','detail',1,2,NULL);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (8,7,'Fichiers PDF','Fichiers PDF','thumbnail',4,0,1);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (14,0,'Acteurs locaux','Contient les fiches acteurs locaux','detail',5,3,1);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (15,1,'Archives','Archives','detail',9,4,0);
INSERT INTO document_space (id_space,id_parent,document_space_name,description,document_space_view,id_space_icon,space_order,document_creation_allowed) VALUES (18,5,'Icones et pictos','Icones et pictos','thumbnail',2,NULL,1);

--
-- Dumping data for table document_space_action
--
--
-- Table document_space_action
--
INSERT INTO document_space_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES (1,'document.spaces.action.createSpace.name','document.spaces.action.createSpace.description','jsp/admin/plugins/document/CreateSpace.jsp','folder-plus fs-2','CREATE');
INSERT INTO document_space_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES (2,'document.spaces.action.deleteSpace.name','document.spaces.action.deleteSpace.description','jsp/admin/plugins/document/DeleteSpace.jsp','folder-x fs-2','DELETE');
INSERT INTO document_space_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES (3,'document.spaces.action.modifySpace.name','document.spaces.action.modifySpace.description','jsp/admin/plugins/document/ModifySpace.jsp','folder-cog fs-2','MODIFY');
INSERT INTO document_space_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES (4,'document.spaces.action.manageUsers.name','document.spaces.action.manageUsers.description','jsp/admin/plugins/document/ManageSpaceUsers.jsp','folder-user fs-2','USERS');
INSERT INTO document_space_action (id_action, name_key, description_key, action_url, icon_url, action_permission) VALUES (5,'document.spaces.action.moveSpace.name','document.spaces.action.moveSpace.description','jsp/admin/plugins/document/MoveSpace.jsp','folder-symlink fs-2','MOVE');
--
-- Dumping data for table document_space_document_type
--
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (2,'article');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (3,'article');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (4,'article');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (6,'image');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (8,'pdf');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (14,'actor');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (15,'article');
INSERT INTO document_space_document_type (id_space,code_document_type) VALUES (18,'image');

--
-- Dumping data for table document_space_icon
--
INSERT INTO document_space_icon VALUES (1, 'themes/shared/plugins/document/images/spaces/folder.svg');
INSERT INTO document_space_icon VALUES (2, 'themes/shared/plugins/document/images/spaces/folder-down.svg');
INSERT INTO document_space_icon VALUES (3, 'themes/shared/plugins/document/images/spaces/folder-x.svg');
INSERT INTO document_space_icon VALUES (4, 'themes/shared/plugins/document/images/spaces/folder-cog.svg');
INSERT INTO document_space_icon VALUES (5, 'themes/shared/plugins/document/images/spaces/folder-check.svg');
INSERT INTO document_space_icon VALUES (6, 'themes/shared/plugins/document/images/spaces/folder-bolt.svg');
INSERT INTO document_space_icon VALUES (7, 'themes/shared/plugins/document/images/spaces/folder-root.svg');
INSERT INTO document_space_icon VALUES (8, 'themes/shared/plugins/document/images/spaces/folder-photo.svg');
INSERT INTO document_space_icon VALUES (9, 'themes/shared/plugins/document/images/spaces/folder-star.svg');
INSERT INTO document_space_icon VALUES (10, 'themes/shared/plugins/document/images/spaces/folder-exclamation.svg');
INSERT INTO document_space_icon VALUES (11, 'themes/shared/plugins/document/images/spaces/folder-heart.svg');
INSERT INTO document_space_icon VALUES (12, 'themes/shared/plugins/document/images/spaces/folder-photo-up.svg');

--
-- Dumping data for table document_type
--
INSERT INTO document_type (code_document_type,document_type_name,description,thumbnail_attr_id,default_thumbnail_url,admin_template,content_template,metadata_handler) VALUES ('actor','Fiche acteur','Fiche acteur local',41,'',NULL,'<p>
<#if document.attributes.photo??><@documentFile file=document.attributes.photo class=''float-start me-3'' /></#if>
<strong><@documentValue value=document.attributes.firstname /> <@documentValue value=document.attributes.lastname /></strong>
</p>
<#if document.attributes[''function'']??>
<p>Fonction : <@documentValue value=document.attributes[''function''] /></p>
</#if>','dublincore');
INSERT INTO document_type (code_document_type,document_type_name,description,thumbnail_attr_id,default_thumbnail_url,admin_template,content_template,metadata_handler) VALUES ('article','Article','Article',0,'','<p><strong>#i18n{document.document_admin_default.labelTitle} : </strong><@documentValue value=document.title /></p>
<p><strong>#i18n{document.document_admin_default.labelSummary} : </strong><@documentValue value=document.summary /></p>
<#if document.attributes.attachment??>
<p><strong>#i18n{document.document_admin_default.labelFile} : </strong></p>
<p><@documentFile file=document.attributes.attachment /></p>
</#if>
<#if document.attributes.body??>
<div><@documentHtml html=document.attributes.body /></div>
</#if>','<p><strong><@documentValue value=document.title /></strong></p>
<#if document.attributes.attachment??>
<p><@documentFile file=document.attributes.attachment /></p>
</#if>
<#if document.attributes.body??>
<div><@documentHtml html=document.attributes.body /></div>
</#if>','none');
INSERT INTO document_type (code_document_type,document_type_name,description,thumbnail_attr_id,default_thumbnail_url,admin_template,content_template,metadata_handler) VALUES ('image','Image','Image',43,'',NULL,'<p><strong><@documentValue value=document.title /></strong></p>
<#if document.attributes.file??><@documentFile file=document.attributes.file class=''img-fluid'' alt=document.title /></#if>
<#if document.summary?has_content>
<p><@documentValue value=document.summary /></p>
</#if>
<#if document.attributes.credits??>
<p><@documentValue value=document.attributes.credits /></p>
</#if>','dublincore');
INSERT INTO document_type (code_document_type,document_type_name,description,thumbnail_attr_id,default_thumbnail_url,admin_template,content_template,metadata_handler) VALUES ('video','Video','Video',0,'',NULL,NULL,'dublincore');
INSERT INTO document_type (code_document_type,document_type_name,description,thumbnail_attr_id,default_thumbnail_url,admin_template,content_template,metadata_handler) VALUES ('pdf','PDF','Fichier PDF',0,'themes/shared/plugins/document/images/filetypes/file-type-pdf.svg',NULL,NULL,'dublincore');

--
-- Dumping data for table document_type_attr
--
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (8,'dossier','multiline','body','corps','Corps du dossier',0,1,1);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (11,'article','file','attachment','Piece jointe','Piece jointe',4,0,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (12,'article','richtext','body','Corps','Corps de l''article',5,1,1);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (10,'article','url','url','Url','Url liée à l''article',2,0,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (30,'actor','text','firstname','Prenom','Prenom',1,1,1);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (31,'actor','text','lastname','Nom','Nom',2,1,1);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (32,'actor','multiline','function','Fontion','Fonction',3,0,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (41,'actor','image','photo','Photo','Photo',4,0,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (43,'image','image','file','Fichier','Fichier image',2,1,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (44,'image','text','credits','Credits','Credits',3,1,0);
INSERT INTO document_type_attr (id_document_attr,code_document_type,code_attr_type,code,document_type_attr_name,description,attr_order,required,searchable) VALUES (48,'pdf','file','file','Fichier','Fichier',2,1,1);
--
-- Dumping data for table document_type_attr_parameters
--
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (30,'maxlength',1,'50');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (30,'size',1,'50');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (31,'maxlength',1,'50');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (31,'size',1,'50');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (44,'size',1,'60');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (44,'maxlength',1,'100');
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (48,'extension',0,'pdf');

--
-- Dumping data for table document_view
--
INSERT INTO document_view (code_view,name_key) VALUES ('detail','document.view.detail');
INSERT INTO document_view (code_view,name_key) VALUES ('thumbnail','document.view.thumbnail');

--
-- Dumping data for table document_workflow_action
--
--
-- Table document_workflow_action
--
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (1, 'document.workflow.action.deleteDocument.name', 'document.workflow.action.deleteDocument.description', 'jsp/admin/plugins/document/DeleteDocument.jsp?', 'trash', 'DELETE', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (2, 'document.workflow.action.modifyDocument.name', 'document.workflow.action.modifyDocument.description', 'jsp/admin/plugins/document/ModifyDocument.jsp?', 'pencil', 'MODIFY', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (3, 'document.workflow.action.submitForApproval.name', 'document.workflow.action.submitForApproval.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=2&', 'cog', 'SUBMIT', 2);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (4, 'document.workflow.action.approveDocument.name', 'document.workflow.action.approveDocument.description', 'jsp/admin/plugins/document/DoValidateDocument.jsp?id_state=3&', 'check', 'VALIDATE', 3);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (5, 'document.workflow.action.assignDocument.name', 'document.workflow.action.assignDocument.description', 'jsp/admin/plugins/document/ManageDocumentPublishing.jsp?', 'globe', 'ASSIGN', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (6, 'document.workflow.action.rejectDocument.name', 'document.workflow.action.rejectDocument.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=4&', 'minus', 'VALIDATE', 4);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (7, 'document.workflow.action.submitAgain.name', 'document.workflow.action.submitAgain.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=2&', 'check', 'SUBMIT', 2);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (8, 'document.workflow.action.archiveDocument.name', 'document.workflow.action.archiveDocument.description', 'jsp/admin/plugins/document/DoConfirmArchiveDocument.jsp?id_state=5&', 'archive', 'ARCHIVE', 5);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (9, 'document.workflow.action.changeDocument.name', 'document.workflow.action.changeDocument.description', 'jsp/admin/plugins/document/ModifyDocument.jsp?id_state=6&', 'pencil', 'CHANGE', 6);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (10, 'document.workflow.action.submitChanges.name', 'document.workflow.action.submitChanges.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=7&', 'cog', 'SUBMIT', 7);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (11, 'document.workflow.action.rejectChanges.name', 'document.workflow.action.rejectChanges.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=6&', 'minus', 'VALIDATE', 6);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (12, 'document.workflow.action.approveChanges.name', 'document.workflow.action.approveChanges.description', 'jsp/admin/plugins/document/DoValidateDocument.jsp?id_state=3&', 'check', 'VALIDATE', 3);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (13, 'document.workflow.action.unarchiveDocument.name', 'document.workflow.action.unarchiveDocument.description', 'jsp/admin/plugins/document/DoChangeState.jsp?id_state=3&', 'archive-off', 'ARCHIVE', 3);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (14, 'document.workflow.action.history.name', 'document.workflow.action.history.description', 'jsp/admin/plugins/document/DocumentHistory.jsp?', 'list', 'VIEW_HISTORY', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (15, 'document.workflow.action.previewDocument.name', 'document.workflow.action.previewDocument.description', 'jsp/admin/plugins/document/PreviewDocument.jsp?', 'eye', 'VIEW', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (16, 'document.workflow.action.moveDocument.name', 'document.workflow.action.moveDocument.description', 'jsp/admin/plugins/document/MoveDocument.jsp?', 'folder-symlink', 'MOVE', NULL);
-- INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (17, 'document.workflow.action.commentDocument.name', 'document.workflow.action.commentDocument.description', 'jsp/admin/plugins/document/modules/comment/ManageDocumentComments.jsp?', 'comment', 'COMMENT', NULL);
INSERT INTO document_workflow_action (id_action, name_key, description_key, action_url, icon_url, action_permission, id_finish_state) VALUES (18, 'document.workflow.action.publishDocument.name', 'document.workflow.action.publishDocument.description', NULL, 'globe', 'PUBLISH', NULL);

--
-- Dumping data for table document_workflow_state
--
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (1,'document.workflow.state.writing.name','document.workflow.state.writing.description',1);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (2,'document.workflow.state.waitingForApproval.name','document.workflow.state.waitingForApproval.description',2);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (3,'document.workflow.state.approved.name','document.workflow.state.approved.description',3);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (4,'document.workflow.state.rejected.name','document.workflow.state.rejected.description',4);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (5,'document.workflow.state.archived.name','document.workflow.state.archived.description',5);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (6,'document.workflow.state.inChange.name','document.workflow.state.inChange.description',6);
INSERT INTO document_workflow_state (id_state,name_key,description_key,state_order) VALUES (7,'document.workflow.state.waitingForChangesApproval.name','document.workflow.state.waitingForChangesApproval.name',7);

--
-- Dumping data for table document_workflow_transition
--
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,1);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,2);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,3);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (1,16);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,2);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,4);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,6);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (2,16);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,0);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,5);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,8);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,9);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,1);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,2);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,7);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (4,16);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (5,1);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (5,13);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (5,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (5,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (6,9);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (6,10);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (6,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (6,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (6,16);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (7,11);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (7,12);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (7,14);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (7,15);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (7,16);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,17);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,18);
INSERT INTO document_workflow_transition (id_state,id_action) VALUES (3,16);