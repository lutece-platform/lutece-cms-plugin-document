-- liquibase formatted sql
-- changeset document:update_db_document-3.1.2-3.1.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO document_attr_type_parameter (code_attr_type, parameter_name, parameter_label_key, parameter_index, parameter_description_key, parameter_default_value) VALUES ('file', 'extension', 'document.attributeType.file.parameter.extension.label', 1, 'document.attributeType.file.parameter.extension.description', 'docx,xlsx,pdf');
INSERT INTO document_attr_type_parameter (code_attr_type, parameter_name, parameter_label_key, parameter_index, parameter_description_key, parameter_default_value) VALUES ('image', 'extension', 'document.attributeType.image.parameter.extension.label', 1, 'document.attributeType.image.parameter.extension.description', 'jpg,jpeg,png');

/* Update icons for Bootstrap 3 */
UPDATE document_space_action SET icon_url=CONCAT('glyphicon glyph', icon_url);
UPDATE document_workflow_action SET icon_url=CONCAT('glyphicon glyph', icon_url);