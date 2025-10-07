-- liquibase formatted sql
-- changeset document:update_db_document-4.2.0-5.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO document_type_attr_parameters (id_document_attr,parameter_name,id_list_parameter,parameter_value) VALUES (48,'extension',0,'pdf');
